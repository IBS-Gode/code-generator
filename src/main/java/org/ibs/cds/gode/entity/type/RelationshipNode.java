package org.ibs.cds.gode.entity.type;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Data
@Entity
public class RelationshipNode extends ManagedEntity{
    private String role;
    @ManyToOne
    private RawEntitySpec entity;
}
