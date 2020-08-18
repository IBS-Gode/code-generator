package org.ibs.cds.gode.codegenerator.model.deploy;

import org.ibs.cds.gode.codegenerator.bind.ArtifactPackaging;
import org.ibs.cds.gode.codegenerator.bind.MavenBind;
import org.ibs.cds.gode.codegenerator.exception.CodeGenerationFailure;
import org.ibs.cds.gode.codegenerator.model.build.BuildModel;
import org.ibs.cds.gode.codegenerator.spec.ProgLanguage;
import org.ibs.cds.gode.status.BinaryStatus;
import org.ibs.cds.gode.util.Assert;

public interface CodeDeployer {
    BinaryStatus deploy(String deployConfig);
    BinaryStatus upLoadImage(String deployConfig);
    BinaryStatus runDockerCompose(String deployConfig);

    static BinaryStatus deploy(BuildModel model,String deployConfig) {
        Assert.notNull("Deployment config cannot be null", deployConfig);
        if(model.getProgLanguage()== ProgLanguage.JAVA && model.getArtifactPackaging() == ArtifactPackaging.MAVEN){
            return new MavenBind().deploy(deployConfig);
        }
        throw CodeGenerationFailure.DEPLOYMENT_FAILURE.provide("Only local deployment with maven is supported now");
    }
    static BinaryStatus deployDocker(BuildModel model,String deployConfig) {
        Assert.notNull("Deployment config cannot be null", deployConfig);
        if(model.getProgLanguage()== ProgLanguage.JAVA && model.getArtifactPackaging() == ArtifactPackaging.MAVEN){
            return new MavenBind().runDockerCompose(deployConfig);
        }
        throw CodeGenerationFailure.DEPLOYMENT_FAILURE.provide("Only local deployment with maven is supported now");
    }
    static BinaryStatus upLoadImage(BuildModel model,String deployConfig) {
        Assert.notNull("Deployment config cannot be null", deployConfig);
        if(model.getProgLanguage()== ProgLanguage.JAVA && model.getArtifactPackaging() == ArtifactPackaging.MAVEN){
            return new MavenBind().upLoadImage(deployConfig);
        }
        throw CodeGenerationFailure.DEPLOYMENT_FAILURE.provide("Only local deployment with maven is supported now");
    }
}
