package org.ibs.cds.gode.codegenerator.model.deploy;

import org.ibs.cds.gode.entity.type.DeploymentModelType;
import lombok.Data;
import org.ibs.cds.gode.entity.type.Specification;

import java.util.HashMap;
import java.util.Map;

@Data
public class DeploymentModel {
    private Specification app;
    private DeploymentModelType type;
    private Map<String,String> localDeploymentRequired;
    private Map<String,String> dockerDeploymentRequired;

    public DeploymentModel() {
        this.localDeploymentRequired = new HashMap();
    }
}
