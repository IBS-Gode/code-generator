
package org.ibs.cds.gode.entity.type;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Data
@Entity
public class Pipeline extends ManagedEntity
{

    private String name;
    @OneToOne(cascade = CascadeType.ALL)
    private PipelineSource source;
    private final static long serialVersionUID = -2658940907395455355L;

}
