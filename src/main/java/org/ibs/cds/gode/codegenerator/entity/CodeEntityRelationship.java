package org.ibs.cds.gode.codegenerator.entity;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.ibs.cds.gode.codegenerator.artefact.Buildable;
import org.ibs.cds.gode.codegenerator.config.CodeGenerationComponent;
import org.ibs.cds.gode.codegenerator.model.build.BuildModel;
import org.ibs.cds.gode.codegenerator.spec.StoreName;
import org.ibs.cds.gode.entity.relationship.RelationshipType;
import org.ibs.cds.gode.entity.type.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CodeEntityRelationship extends Specification implements Buildable, CodeGenerationComponent {

    private StoreName storeName;
    private List<CodeField> fields;
    private CodeEntityRelationshipNode startNode;
    private CodeEntityRelationshipNode endNode;
    private List<CodeField> viewFields;
    private String viewName;
    private RelationshipType type;

    public CodeEntityRelationship(RelationshipEntitySpec spec, RelationshipStorePolicy storePolicy, BuildModel buildModel) {
        StoreName relationshipStore = storePolicy.getStoreName();
        this.storeName = relationshipStore == null ? StoreName.NEO4J : relationshipStore;
        this.viewFields = spec.getFields().stream().map(k->new CodeField(k, buildModel)).collect(Collectors.toList());
        this.fields = CollectionUtils.isNotEmpty(storePolicy.getAdditionalFields()) ?
                storePolicy.getAdditionalFields().stream().map(k->new CodeField(k, buildModel)).collect(Collectors.toList())
        : new ArrayList();
        this.fields.addAll(this.viewFields);
        this.setName(spec.getName());
        this.viewName = this.getName().concat("View");
        this.startNode = new CodeEntityRelationshipNode();
        RawEntitySpec startEntity = spec.getStartNode().getEntity();
        RawEntitySpec endEntity = spec.getEndNode().getEntity();
        this.startNode.setEntityName(startEntity.getName());
        this.startNode.setEntityViewName(startEntity.getName().concat("View"));
        this.startNode.setRole(spec.getStartNode().getRole());
        this.startNode.setIdField(new CodeField(startEntity.getIdField(), buildModel));
        this.endNode = new CodeEntityRelationshipNode();
        this.endNode.setRole(spec.getEndNode().getRole());
        this.endNode.setIdField(new CodeField(endEntity.getIdField(), buildModel));
        this.endNode.setEntityName(endEntity.getName());
        this.endNode.setEntityViewName(endEntity.getName().concat("View"));
        this.type = spec.getType();
        this.setVersion(spec.getVersion());
    }

    @Override
    public ComponentName getComponentName() {
        return ComponentName.RELATIONSHIP;
    }
}
