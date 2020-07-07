package org.ibs.cds.gode.codegenerator.entity;

import lombok.Data;
import org.ibs.cds.gode.codegenerator.artefact.LangObject;
import org.ibs.cds.gode.codegenerator.model.build.BuildModel;
import org.ibs.cds.gode.codegenerator.spec.GraphQLUtil;
import org.ibs.cds.gode.entity.type.EntityField;
import org.ibs.cds.gode.entity.type.FieldType;
import org.ibs.cds.gode.util.Assert;

@Data
public class CodeEntityField extends CodeField implements ResolvedFromModel<EntityField, LangObject>{

    private LangObject object;
    private final EntityField field;
    private BuildModel buildModel;
    private String graphQLType;
    private CodeObjectField objectField;
    private boolean autosequence;

    public CodeEntityField(EntityField field, BuildModel buildModel) {
        super(field, buildModel);
        this.field = field;
        this.object = process(field, buildModel);
        this.graphQLType = GraphQLUtil.getGrapQLType(field);
        this.buildModel = buildModel;
        if(field.getObjectType() != null){
            this.objectField = new CodeObjectField(field.getObjectType(), buildModel);
        }
        this.autosequence = field.getType() == FieldType.SEQUENCE;
    }
}
