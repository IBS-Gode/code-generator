package org.ibs.cds.gode.codegenerator.entity;

import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;
import org.ibs.cds.gode.codegenerator.artefact.JavaObject;
import org.ibs.cds.gode.codegenerator.artefact.LangObject;
import org.ibs.cds.gode.codegenerator.exception.CodeGenerationFailure;
import org.ibs.cds.gode.codegenerator.model.build.BuildModel;
import org.ibs.cds.gode.codegenerator.spec.GraphQLUtil;
import org.ibs.cds.gode.entity.type.Field;
import org.ibs.cds.gode.entity.type.ManagedIdentifiedObject;
import org.ibs.cds.gode.entity.type.ObjectType;
import org.ibs.cds.gode.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CodeObjectField  extends ManagedIdentifiedObject implements ResolvedFromModel<ObjectType, List<LangObject>> {

    private final ObjectType objectType;
    private final BuildModel buildModel;
    private List<LangObject> fields;
    private List<Pair<String,String>> graphQLTypes;

    public CodeObjectField(ObjectType objectType, BuildModel buildModel) {
        this.objectType = objectType;
        this.buildModel = buildModel;
        this.fields = process(objectType, buildModel);
        this.setName(objectType.getName());
        this.graphQLTypes = getGraphQLTypesDetail();
        this.setDescription(objectType.getDescription());
    }

    @NotNull
    public List<Pair<String, String>> getGraphQLTypesDetail() {
        return this.objectType != null ?
                this.objectType.getFields().stream().map(k-> Pair.of(k.getName(), GraphQLUtil.getPrimitiveGrapQLType(k.getType()))).collect(Collectors.toUnmodifiableList())
                : Collections.emptyList();
    }

    @Override
    public List<LangObject> process(ObjectType objectType, BuildModel buildModel) {
        if(objectType == null) return Collections.emptyList();
        return objectType.getFields().stream().map(f -> resolveFieldType(f)).collect(Collectors.toList());
    }

    @NotNull
    public JavaObject resolveFieldType(Field f) {
        var type =f.getType();
        var name = f.getName();
        switch (type) {
            case DATE:
                return new JavaObject(name, "OffsetDateTime", "java.time");
            case BOOLEAN:
                return new JavaObject(name, "Boolean", StringUtils.EMPTY);
            case TEXT:
            case LONG_TEXT:
                return new JavaObject(name, "String", StringUtils.EMPTY);
            case NUMBER: case SEQUENCE:
                return new JavaObject(name, "Long", StringUtils.EMPTY);
            case HIGH_PRECISION_NUMBER:
                return new JavaObject(name, "BigInteger", "java.math");
            case DECIMAL:
                return new JavaObject(name, "Double", StringUtils.EMPTY);
            case HIGH_PRECISION_DECIMAL:
                return new JavaObject(name, "BigDecimal", "java.math");
            default:
                throw CodeGenerationFailure.DATATYPE_NOT_IDENTIFIED.provide("Internal object field type resolution failed: ".concat(f.getName()));
        }
    }
}
