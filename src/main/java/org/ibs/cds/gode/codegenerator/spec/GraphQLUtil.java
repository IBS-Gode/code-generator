package org.ibs.cds.gode.codegenerator.spec;

import org.ibs.cds.gode.codegenerator.exception.CodeGenerationFailure;
import org.ibs.cds.gode.entity.type.EntityField;
import org.ibs.cds.gode.entity.type.FieldType;

public class GraphQLUtil {

    public  static String getGrapQLType(EntityField field){
       switch (field.getType()){
           case LONG_TEXT: case TEXT: return "String";
           case NUMBER:case SEQUENCE: case HIGH_PRECISION_NUMBER: return "Int";
           case DECIMAL: case HIGH_PRECISION_DECIMAL: return "Float";
           case BOOLEAN: return "Boolean";
           case RELATIONSHIP: return field.getRelationship().getName();
           case DATE: return "Date";
           case OBJECT: return field.getObjectType().getName();
       }
       throw CodeGenerationFailure.DATATYPE_NOT_IDENTIFIED.provide("No specific type identified for GraphQL");
    }

    public  static String getPrimitiveGrapQLType(FieldType type){
        switch (type){
            case LONG_TEXT: case TEXT: return "String";
            case NUMBER:case SEQUENCE: case HIGH_PRECISION_NUMBER: return "Int";
            case DECIMAL: case HIGH_PRECISION_DECIMAL: return "Float";
            case BOOLEAN: return "Boolean";
        }
        throw CodeGenerationFailure.DATATYPE_NOT_IDENTIFIED.provide("No specific type identified for GraphQL");
    }
}
