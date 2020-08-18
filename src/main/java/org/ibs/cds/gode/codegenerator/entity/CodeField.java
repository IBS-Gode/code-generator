package org.ibs.cds.gode.codegenerator.entity;

import lombok.Data;
import org.ibs.cds.gode.codegenerator.artefact.JavaObject;
import org.ibs.cds.gode.codegenerator.artefact.LangObject;
import org.ibs.cds.gode.codegenerator.exception.CodeGenerationFailure;
import org.ibs.cds.gode.codegenerator.model.build.BuildModel;
import org.ibs.cds.gode.codegenerator.spec.GraphQLUtil;
import org.ibs.cds.gode.entity.type.EntityField;
import org.ibs.cds.gode.entity.type.FieldType;
import org.ibs.cds.gode.system.GodeAppEnvt;
import org.ibs.cds.gode.util.Assert;
import org.ibs.cds.gode.util.StringUtils;
import org.jetbrains.annotations.NotNull;

@Data
public class CodeField implements ResolvedFromModel<EntityField, LangObject> {

    private LangObject object;
    private String graphQLType;
    private boolean sequence;

    public CodeField(EntityField field,BuildModel buildModel) {
        Assert.notNull(field);
        this.object = process(field, buildModel);
        this.graphQLType = GraphQLUtil.getGrapQLType(field);
    }

    public LangObject process(EntityField field, BuildModel buildModel){
        String name = field.getName();
        FieldType type = field.getType();
        Assert.notNull("FieldType and BuildModel cannot be empty",type, buildModel);
        if(field.getType() == FieldType.SEQUENCE) this.sequence = true;
        switch (buildModel.getProgLanguage()){
            case JAVA: return getJavaObject(field, name, type);
            default: throw CodeGenerationFailure.LANGUAGE_NOT_FOUND.provide(buildModel.getProgLanguage());
        }

    }

    @NotNull
    private JavaObject getJavaObject(EntityField field, String name, FieldType type) {
        switch (type){
            case DATE: return new JavaObject(name, "Date", "java.util");
            case BOOLEAN: return new JavaObject(name, "Boolean", StringUtils.EMPTY);
            case TEXT: case LONG_TEXT: return new JavaObject(name, "String", StringUtils.EMPTY);
            case NUMBER: case SEQUENCE: return new JavaObject(name, "Long", StringUtils.EMPTY);
            case HIGH_PRECISION_NUMBER: return new JavaObject(name, "BigInteger","java.math");
            case DECIMAL: return new JavaObject(name, "Double", StringUtils.EMPTY);
            case HIGH_PRECISION_DECIMAL: return new JavaObject(name, "BigDecimal", "java.math");
            case OBJECT:
                String objectName = field.getObjectType().getName();
                return new JavaObject(name, objectName, GodeAppEnvt.ENTITY_TYPE_PACKAGE_NAME);
            case RELATIONSHIP:
                String relationshipName = field.getRelationship().getName();
                return new JavaObject(name, relationshipName, GodeAppEnvt.ENTITY_TYPE_PACKAGE_NAME);
            default:
                throw CodeGenerationFailure.DATATYPE_NOT_IDENTIFIED.provide(field);
        }
    }
}
