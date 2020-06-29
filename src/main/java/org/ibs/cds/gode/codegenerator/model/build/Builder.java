package org.ibs.cds.gode.codegenerator.model.build;

import org.ibs.cds.gode.codegenerator.bind.ArtifactPackaging;
import org.ibs.cds.gode.codegenerator.config.EngineConfiguration;
import org.ibs.cds.gode.codegenerator.entity.AppCodeGenerator;
import org.ibs.cds.gode.codegenerator.entity.PathPackage;
import org.ibs.cds.gode.codegenerator.exception.CodeGenerationFailure;
import org.ibs.cds.gode.codegenerator.model.util.SystemRunner;
import org.ibs.cds.gode.codegenerator.spec.ProgLanguage;
import org.ibs.cds.gode.entity.manager.BuildDataManager;
import org.ibs.cds.gode.entity.type.App;
import org.ibs.cds.gode.entity.type.BuildData;
import org.ibs.cds.gode.status.BinaryStatus;
import org.ibs.cds.gode.util.NetworkUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class Builder {

    public static BuildComplete build(BuildDataManager buildDataManager,BuildModel data, App foundApp){
        String port = String.valueOf(NetworkUtil.nextFreePort());
        if(data.getArtifactPackaging() == ArtifactPackaging.MAVEN && data.getProgLanguage() == ProgLanguage.JAVA){
           return javaMaven(buildDataManager, data, foundApp, port);
        }
        return null;
    }

    private static JavaMavenBuildComplete javaMaven(BuildDataManager buildDataManager, BuildModel data, App foundApp, String port) {
        try {
        AppCodeGenerator appCodeGenerator = new AppCodeGenerator(foundApp, data);
        boolean generate = appCodeGenerator.generate();
        if(generate){
            buildDataManager.save(BuildData.fromModel(data, foundApp));
            verifyNodeVersion();
            String path = PathPackage.path(EngineConfiguration.getCodeGenPath(),foundApp.getVersion().toString(), foundApp.getName().concat("-container").toLowerCase(),foundApp.getName().concat("-functions").toLowerCase());
            runIde(path, port);
            return new JavaMavenBuildComplete(BinaryStatus.SUCCESS, "http://localhost:".concat(port));
        }
        } catch (Exception e) {
            throw CodeGenerationFailure.SYSTEM_ERROR.provide(e, "Code build failed");
        }
        return new JavaMavenBuildComplete(BinaryStatus.FAILURE, null);
    }

    private static void runIde(String path, String port){
        CompletableFuture.runAsync(()->{
            try {
                File file = new File("ide.sh");
                System.out.println(SystemRunner.run(file.getAbsolutePath(),path, port));
            } catch (IOException | InterruptedException e) {
                throw CodeGenerationFailure.SYSTEM_ERROR.provide(e);
            }
        });
    }

    private static void verifyNodeVersion() throws IOException, InterruptedException {
        String version = SystemRunner.run("node","-v");
        if(!(version.startsWith("v11") || version.startsWith("11"))){
            throw CodeGenerationFailure.SYSTEM_ERROR.provide("Node version should be 11, but currently it is ".concat(version));
        }
    }
}
