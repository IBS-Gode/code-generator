package org.ibs.cds.gode.entity.type;

import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import lombok.Data;

/**
 *
 * @author manugraj
 */
@Data @Entity
public class DeploymentMethod extends ManagedEntity {
    
    private DeploymentModelType type;
    
    @ElementCollection
    private List<DeploymentRequirement> properties;

}
