
package org.ibs.cds.gode.entity.type;

import lombok.Data;
import org.ibs.cds.gode.stream.config.Sink;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.io.Serializable;

@Data
@Entity
public class PipelineNode extends ManagedEntity
{

    private String name;
    private String mapTo;
    @OneToOne(cascade = CascadeType.ALL)
    private PipelineSink sink;
    @OneToOne(cascade = CascadeType.ALL)
    private PipelineNode next;
    private final static long serialVersionUID = 520730522255386580L;

}
