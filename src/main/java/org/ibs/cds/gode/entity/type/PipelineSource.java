
package org.ibs.cds.gode.entity.type;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Data
@Entity
public class PipelineSource extends ManagedEntity
{

    private PipelineSourceType type;
    private String entity;
    private String queue;
    @OneToOne(cascade = CascadeType.ALL)
    private PipelineNode next;
    private final static long serialVersionUID = -4959370730009887738L;

}
