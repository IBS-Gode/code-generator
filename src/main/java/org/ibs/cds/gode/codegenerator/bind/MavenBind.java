package org.ibs.cds.gode.codegenerator.bind;

import org.apache.maven.shared.invoker.*;
import org.ibs.cds.gode.codegenerator.bind.ArtefactBinding;
import org.ibs.cds.gode.codegenerator.exception.CodeGenerationException;
import org.ibs.cds.gode.codegenerator.model.deploy.CodeDeployer;
import org.ibs.cds.gode.exception.Error;
import org.ibs.cds.gode.status.BinaryStatus;

import java.io.File;
import java.util.Arrays;

public class MavenBind implements ArtefactBinding, CodeDeployer {

    static{
        System.setProperty("maven.home",findMvn());
    }

    public static boolean runWithOpts(String pomFile, String mvnOpts, String... args) {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File(pomFile));
        request.setGoals(Arrays.asList(args));
        if(mvnOpts != null)request.setMavenOpts(mvnOpts);
        Invoker invoker = new DefaultInvoker();
        try {
            return invoker.execute(request).getExitCode() == 0;
        } catch (MavenInvocationException e) {
            throw new RuntimeException("Failed to execute maven",e);
        }
    }

    private static String findMvn() {
        if (System.getenv("M2_HOME") != null) {
            return System.getenv("M2_HOME");
        }

        for (String dirname : System.getenv("PATH").split(File.pathSeparator)) {
            File file = new File(dirname, "mvn");
            if (file.isFile() && file.canExecute()) {
                return  new File(dirname).getParentFile().toString();
            }
        }
        throw new CodeGenerationException(new Error(0,"No maven found"));
    }

    public boolean run(String configFile, String... args) {
        return runWithOpts(configFile, null,"clean","install");
    }

    @Override
    public BinaryStatus deploy(String deployConfig) {
        return BinaryStatus.valueOf(runWithOpts(deployConfig, null ,"spring-boot:run"));
    }

    @Override
    public BinaryStatus upLoadImage(String deployConfig) {
        return BinaryStatus.valueOf(runWithOpts(deployConfig, null ,"clean","install","docker:build"));
    }

    @Override
    public BinaryStatus runDockerCompose(String deployConfig) {
        return BinaryStatus.valueOf(runWithOpts(deployConfig, null ,"clean","install","docker:build", "docker-compose:up"));
    }
}
