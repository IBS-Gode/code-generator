
package org.ibs.cds.gode.codegenerator.entity;

import lombok.Data;
import org.ibs.cds.gode.entity.type.Specification;

@Data
public class CodeEntityFunction{

    private final boolean storeFunction;
    private final Specification entity;

    public static CodeEntityFunction fromEntity(CodeEntity entity){
        return new CodeEntityFunction(entity.getStorePolicy().isAvailable(), entity);
    }
    
    public static CodeEntityFunction fromRelationship(CodeEntityRelationship entity){
        return new CodeEntityFunction(true, entity);
    }
}