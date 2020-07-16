package org.ibs.cds.gode.codegenerator.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ibs.cds.gode.codegenerator.bind.ArtefactBinding;
import org.ibs.cds.gode.codegenerator.config.EngineConfiguration;
import org.ibs.cds.gode.codegenerator.model.build.BuildModel;
import org.ibs.cds.gode.codegenerator.model.deploy.DeploymentRequirement;
import org.ibs.cds.gode.codegenerator.velocity.VelocityGeneratorEngine;
import org.ibs.cds.gode.entity.store.StoreType;
import org.ibs.cds.gode.entity.type.App;
import org.ibs.cds.gode.status.BinaryStatus;
import org.ibs.cds.gode.utils.RelationshipUtils;
import org.ibs.cds.gode.utils.StoreUtils;

import java.util.Arrays;
import java.util.stream.Stream;

@Data
@RequiredArgsConstructor
@Slf4j
public class AppCodeGenerator {

    private final App rawApp;
    private final BuildModel buildModel;

    public boolean generate() {
        CodeApp app = new CodeApp(rawApp, buildModel);
        CodeAdminApp codeAdminApp = new CodeAdminApp(app, buildModel);
        CodeAppPipeline pipeline = new CodeAppPipeline(app, buildModel);

        EngineConfiguration configuration = new EngineConfiguration(buildModel);
        String repo = app.getVersion().toString();
        
        VelocityGeneratorEngine<CodeEntity> codeEntityVelocityGeneratorEngine = new VelocityGeneratorEngine(configuration, repo);
        VelocityGeneratorEngine<CodeAppFunctionNode> codeAppFunctionVelocityGeneratorEngine = new VelocityGeneratorEngine(configuration, repo);
        VelocityGeneratorEngine<CodeEntityRelationship> relationshipVelocityGeneratorEngine = new VelocityGeneratorEngine(configuration, repo);
        VelocityGeneratorEngine<CodeApp> codeAppVelocityGeneratorEngine = new VelocityGeneratorEngine(configuration, repo);
        VelocityGeneratorEngine<CodeAdminApp> codeAdminAppVelocityGeneratorEngine = new VelocityGeneratorEngine(configuration, repo);
        VelocityGeneratorEngine<CodeAppPipeline> codeAppPipelineGeneratorEngine = new VelocityGeneratorEngine(configuration, repo);

        commonProperties(
                codeEntityVelocityGeneratorEngine,
                codeAppFunctionVelocityGeneratorEngine,
                codeAppVelocityGeneratorEngine,
                codeAdminAppVelocityGeneratorEngine,
                relationshipVelocityGeneratorEngine,
                codeAppPipelineGeneratorEngine
        );

        codeEntityVelocityGeneratorEngine.addToContext("app", app);
        codeEntityVelocityGeneratorEngine.addToContext("StoreUtils", StoreUtils.class);

        relationshipVelocityGeneratorEngine.addToContext("app", app);
        relationshipVelocityGeneratorEngine.addToContext("StoreUtils", StoreUtils.class);
        relationshipVelocityGeneratorEngine.addToContext("RelationshipUtils", RelationshipUtils.class);

        codeAppFunctionVelocityGeneratorEngine.addToContext("app", app);

        //Generation
        BinaryStatus entityGenerationStatus = BinaryStatus.valueOf(app.getEntities().stream()
                .map(k -> codeEntityVelocityGeneratorEngine.run(k))
                .allMatch(k -> k == BinaryStatus.SUCCESS));

        BinaryStatus relationshipGenerationStatus = BinaryStatus.valueOf(app.getRelationships().stream()
                .map(relationshipVelocityGeneratorEngine::run)
                .allMatch(k -> k == BinaryStatus.SUCCESS));

        BinaryStatus appFunctionGenerationStatus = codeAppFunctionVelocityGeneratorEngine.run(app.getAppFunction());

        BinaryStatus codeAppStatus = codeAppVelocityGeneratorEngine.run(app);

        BinaryStatus codeAdminStatus = codeAdminAppVelocityGeneratorEngine.run(codeAdminApp);

        BinaryStatus codeAppBuildStatus = BinaryStatus.valueOf(codeAppVelocityGeneratorEngine.getBuildable().stream()
                .map(buildable -> ArtefactBinding.resolve(buildModel.getArtifactPackaging()).run(buildable))
                .allMatch(k -> k));

        boolean statusAccumulated = entityGenerationStatus.isSuccess()
                && codeAppStatus.isSuccess()
                && relationshipGenerationStatus.isSuccess()
                && codeAdminStatus.isSuccess()
                && codeAppBuildStatus.isSuccess();

        if (buildModel.isPipelineGeneration() && !buildModel.isInherited()) {
            BinaryStatus codePipelineStatus = codeAppPipelineGeneratorEngine.run(pipeline);
            log.info("Code Generation status- Entity: {} | App Function: {} | Relationships: {} | Application: {} | Admin App: {} | Draft-Pipeline : {}",
                    entityGenerationStatus, appFunctionGenerationStatus, relationshipGenerationStatus, codeAppStatus, codeAdminStatus, codePipelineStatus);
            log.info("Code Build status: {} ", codeAppBuildStatus);
            return statusAccumulated && codePipelineStatus.isSuccess();
        }
        
        log.info("Code Generation status- Entity: {} | App Function: {} | Relationships: {} | Application: {} | Admin App: {}",
                entityGenerationStatus, appFunctionGenerationStatus, relationshipGenerationStatus, codeAppStatus, codeAdminStatus);
        log.info("Code Build status: {} ", codeAppBuildStatus);
        return statusAccumulated;
    }

    private void commonProperties(VelocityGeneratorEngine... generatorEngines) {
        Arrays.stream(generatorEngines).forEach(generatorEngine -> {
            generatorEngine.addToContext("PathPackage", PathPackage.class);
            generatorEngine.addToContext("CodeAppUtil", CodeAppUtil.class);
            generatorEngine.addToContext("StoreType", StoreType.class);
            generatorEngine.addToContext("DeploymentRequirement", DeploymentRequirement.class);
            Stream.of(PathPackage.values()).forEach(pathPackage -> generatorEngine.addToContext(pathPackage.name(), pathPackage));
        });

    }
    
}
