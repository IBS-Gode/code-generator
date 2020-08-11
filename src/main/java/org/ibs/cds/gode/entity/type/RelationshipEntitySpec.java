package org.ibs.cds.gode.entity.type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.ibs.cds.gode.entity.relationship.RelationshipType;

import javax.persistence.*;

@Data
@Entity
public class RelationshipEntitySpec extends RawEntitySpec {
    private final static IdField RELATIONSHIP_IDFIELD = new IdField();
    static {
        RELATIONSHIP_IDFIELD.setType(FieldType.NUMBER);
        RELATIONSHIP_IDFIELD.setName("relationshipId");
    }
    private RelationshipType type;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinTable(name="RStartNode")
    private RelationshipNode startNode;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinTable(name="REndNode")
    private RelationshipNode endNode;
    public RelationshipEntitySpec() {
        super();
        this.setIdField(RELATIONSHIP_IDFIELD);
    }

    @Override
    @JsonIgnore
    public IdField getIdField() {
        return super.getIdField();
    }
}
