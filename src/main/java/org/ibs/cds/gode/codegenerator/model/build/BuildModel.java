package org.ibs.cds.gode.codegenerator.model.build;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.ibs.cds.gode.codegenerator.bind.ArtifactPackaging;
import org.ibs.cds.gode.codegenerator.spec.ProgLanguage;
import org.ibs.cds.gode.entity.type.EntityStorePolicy;
import org.ibs.cds.gode.entity.type.RelationshipStorePolicy;
import org.ibs.cds.gode.entity.type.Specification;

import java.util.ArrayList;
import java.util.List;

@Data
public class BuildModel {
    private ProgLanguage progLanguage;
    private ArtifactPackaging artifactPackaging;
    private List<EntityStorePolicy> entityStorePref;
    private Specification app;
    private boolean secure;
    private boolean systemQueue;
    private List<RelationshipStorePolicy> relationshipStorePolicy;
    private boolean pipelineGeneration;
    
    private Inherit previous;
    
    public BuildModel() {
        this.entityStorePref = new ArrayList<>();
    }
    
    @JsonIgnore
    public boolean isInherited(){
        return previous != null && previous.isInherit();
    }
}
