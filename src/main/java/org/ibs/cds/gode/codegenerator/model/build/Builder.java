package org.ibs.cds.gode.codegenerator.model.build;

import org.ibs.cds.gode.codegenerator.bind.ArtifactPackaging;
import org.ibs.cds.gode.codegenerator.config.CodeGenerationComponent;
import org.ibs.cds.gode.codegenerator.config.EngineConfiguration;
import org.ibs.cds.gode.codegenerator.entity.AppCodeGenerator;
import org.ibs.cds.gode.codegenerator.entity.CodeAppUtil;
import org.ibs.cds.gode.codegenerator.entity.PathPackage;
import org.ibs.cds.gode.codegenerator.exception.CodeGenerationFailure;
import org.ibs.cds.gode.codegenerator.spec.ProgLanguage;
import org.ibs.cds.gode.entity.manager.BuildDataManager;
import org.ibs.cds.gode.entity.type.App;
import org.ibs.cds.gode.entity.type.BuildData;
import org.ibs.cds.gode.status.BinaryStatus;
import org.ibs.cds.gode.util.NetworkUtil;

import org.ibs.cds.gode.codegenerator.ide.CloudIDE;
import org.ibs.cds.gode.util.Assert;
import org.springframework.stereotype.Service;

@Service
public class Builder {

    private final BuildDataManager buildDataManager;
    private final CloudIDE ide;

    public Builder(BuildDataManager buildDataManager, CloudIDE ide) {
        this.buildDataManager = buildDataManager;
        this.ide = ide;
    }

    public BuildComplete build(BuildModel userModel, App foundApp) {
        //Derive inherited build model
        BuildModel buildModel = processBuildModel(userModel, foundApp);
        String port = String.valueOf(NetworkUtil.nextFreePort());
        if (buildModel.getArtifactPackaging() == ArtifactPackaging.MAVEN && buildModel.getProgLanguage() == ProgLanguage.JAVA) {
            return javaMaven(buildModel, foundApp, port);
        }
        return null;
    }

    private BuildModel processBuildModel(BuildModel data, App foundApp) {
        if (data.isInherited()) {
            BuildData latestBuild = getInheritedModel(data, foundApp);
            Assert.notNull("No successful build found for given app", latestBuild);
            return latestBuild.toBuildModel();
        }
        return data;
    }

    private BuildData getInheritedModel(BuildModel data, App foundApp) {
        if (data.getPrevious().getInheritFrom() > 0L) {
            return buildDataManager.findLatestBuild(foundApp.getName(), data.getPrevious().getInheritFrom());
        }
        return buildDataManager.findLatestBuild(foundApp.getArtifactId());
    }

    private JavaMavenBuildComplete javaMaven(BuildModel data, App foundApp, String port) {
        try {
            AppCodeGenerator appCodeGenerator = new AppCodeGenerator(foundApp, data);
            boolean generate = appCodeGenerator.generate();
            if (generate) {
                buildDataManager.save(BuildData.fromModel(data, foundApp));
                String remoteIdeUrl = runIDE(foundApp, port);
                return new JavaMavenBuildComplete(BinaryStatus.SUCCESS, remoteIdeUrl);
            }
        } catch (Exception e) {
            throw CodeGenerationFailure.SYSTEM_ERROR.provide(e, "Code build failed");
        }
        return new JavaMavenBuildComplete(BinaryStatus.FAILURE, null);
    }

    public String runIDE(App foundApp, String port) {
        return ide.runIDE(CodeAppUtil.customisableApp(foundApp), port);
    }
}
