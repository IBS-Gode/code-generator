
package org.ibs.cds.gode.entity.type;

import lombok.Data;

import javax.persistence.Entity;
import java.io.Serializable;

@Data
@Entity
public class PipelineSink extends ManagedEntity
{

    private String name;
    private String entity;
    private final static long serialVersionUID = -8109456363358074492L;

}
