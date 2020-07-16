package org.ibs.cds.gode.entity.type;

import javax.persistence.Embeddable;
import lombok.Data;

/**
 *
 * @author manugraj
 */
@Data @Embeddable
public class DeploymentRequirement {

    private String key;
    private String value;
}
