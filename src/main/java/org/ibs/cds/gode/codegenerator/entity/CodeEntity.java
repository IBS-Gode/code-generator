package org.ibs.cds.gode.codegenerator.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.ibs.cds.gode.codegenerator.artefact.Buildable;
import org.ibs.cds.gode.codegenerator.config.CodeGenerationComponent;
import org.ibs.cds.gode.codegenerator.model.build.BuildModel;
import org.ibs.cds.gode.entity.type.EntityStorePolicy;
import org.ibs.cds.gode.entity.type.FieldType;
import org.ibs.cds.gode.entity.type.Specification;
import org.ibs.cds.gode.entity.type.StatefulEntitySpec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class CodeEntity extends Specification implements Buildable, CodeGenerationComponent {

    private StatefulEntitySpec model;
    private CodeEntityStorePolicy storePolicy;
    private CodeEntityField idField;
    private List<CodeEntityField> fields;
    private BuildModel buildModel;
    private List<CodeEntityField> viewFields;
    private String viewName;
    private boolean sequenceFieldsAvailable;

    public CodeEntity(StatefulEntitySpec userProvided, BuildModel buildModel, Map<Long, EntityStorePolicy> entityStorePolicyMap) {
        this.setName(userProvided.getName());
        this.viewName = this.getName().concat("View");
        this.setDescription(userProvided.getDescription());
        this.setVersion(userProvided.getVersion());
        this.buildModel = buildModel;
        storePolicy = new CodeEntityStorePolicy(userProvided, this.buildModel);
        this.model =  userProvided;
        this.fields = this.model.getFields().stream().map(field -> new CodeEntityField(field, buildModel)).collect(Collectors.toList());
        this.idField = new CodeEntityField(this.model.getIdField(), buildModel);
        EntityStorePolicy entityStorePolicy = entityStorePolicyMap.get(userProvided.getArtifactId());
        if(entityStorePolicy != null && CollectionUtils.isNotEmpty(entityStorePolicy.getAdditionalFields())){
            this.viewFields = entityStorePolicy.getAdditionalFields().stream().map(field -> new CodeEntityField(field, buildModel)).collect(Collectors.toList());
        }else {
            this.viewFields = new ArrayList();
        }
        this.viewFields.addAll(fields);
        this.sequenceFieldsAvailable = this.fields.stream().anyMatch(CodeEntityField::isAutosequence);
    }

    @Override
    public ComponentName getComponentName() {
        return ComponentName.ENTITY;
    }

    @JsonIgnore
    public List<CodeObjectField> getObjectFields(){
        return fields == null ? Collections.emptyList() : fields.stream()
                .filter(k->k.getField().getType() == FieldType.OBJECT)
                .map(k->k.getObjectField()).collect(Collectors.toList());
    }

    @JsonIgnore
    public List<CodeEntityField> getAutoSequenceFieldList(){
        return this.fields.stream().filter(CodeEntityField::isAutosequence).collect(Collectors.toList());
    }

}
