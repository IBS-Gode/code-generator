package org.ibs.cds.gode.codegenerator.model.deploy;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.ibs.cds.gode.codegenerator.config.CodeGenerationComponent;
import org.ibs.cds.gode.codegenerator.config.EngineConfiguration;
import org.ibs.cds.gode.codegenerator.entity.CodeApp;
import org.ibs.cds.gode.codegenerator.entity.CodeAppUtil;
import org.ibs.cds.gode.codegenerator.entity.PathPackage;
import org.ibs.cds.gode.codegenerator.exception.CodeGenerationFailure;
import org.ibs.cds.gode.codegenerator.model.util.PropertyUtil;
import org.ibs.cds.gode.status.BinaryStatus;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
public class Deployer {
    private static  final String LOCAL_APP = "http://localhost:%s/swagger-ui.html";
    private static  final String LOCAL_ADMIN_APP = "http://localhost:%s/%s";
    public static DeploymentComplete doDeployment(DeploymentModel model, CodeApp app) {
        try {
            Map<String, String> localDeployment = model.getLocalDeploymentRequired();
            if (MapUtils.isNotEmpty(model.getLocalDeploymentRequired())) {
                if (updateRequirement(app, localDeployment)) {
                    String pom = PathPackage.path(EngineConfiguration.getCodeGenPath(), app.getVersion().toString(), CodeAppUtil.containerAppName(app).toLowerCase(), CodeGenerationComponent.ComponentName.APP.getNature(), app.getName().toLowerCase(), "pom.xml");
                    String adminPom = PathPackage.path(EngineConfiguration.getCodeGenPath(), app.getVersion().toString(), CodeAppUtil.containerAppName(app).toLowerCase(), CodeGenerationComponent.ComponentName.ADMIN.getNature(), CodeAppUtil.adminAppName(app).toLowerCase(), "pom.xml");
                    CompletableFuture
                            .supplyAsync(()->{
                                log.info("Deployment started with args:{}",adminPom);
                                return CodeDeployer.deploy(app.getBuildModel(), adminPom);
                            });
                    CompletableFuture
                            .supplyAsync(()->{
                                log.info("Deployment started with args:{}",pom);
                                return CodeDeployer.deploy(app.getBuildModel(), pom);
                            });
                    return new LocalDeploymentComplete(String.format(LOCAL_APP, localDeployment.get(LocalDeploymentRequirement.APP_PORT.getPropertyName())),
                            String.format(LOCAL_ADMIN_APP, localDeployment.get(LocalDeploymentRequirement.ADMIN_PORT.getPropertyName()), CodeAppUtil.adminAppName(app).toLowerCase()),
                            BinaryStatus.SUCCESS);
                }
                return new LocalDeploymentComplete(null,null,BinaryStatus.FAILURE);
            }
            throw CodeGenerationFailure.SYSTEM_ERROR.provide("Only local deployment is supported now");
        } catch (Exception e) {
            throw CodeGenerationFailure.SYSTEM_ERROR.provide(e);
        }
    }

    public static boolean updateRequirement(CodeApp app, Map<String, String> localDeployment) {
        List<LocalDeploymentRequirement> requirement = localDeployment.entrySet().stream().map(k -> LocalDeploymentRequirement.from(k.getKey(), k.getValue(), app)).filter(Objects::nonNull).collect(Collectors.toList());
        String property = PathPackage.path(EngineConfiguration.getCodeGenPath(), app.getVersion().toString(), CodeAppUtil.containerAppName(app).toLowerCase());
        return PropertyUtil.update(property, requirement, app);
    }
}
