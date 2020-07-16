package org.ibs.cds.gode.entity.type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.ibs.cds.gode.codegenerator.bind.ArtifactPackaging;
import org.ibs.cds.gode.codegenerator.model.build.BuildModel;
import org.ibs.cds.gode.codegenerator.spec.ProgLanguage;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.List;

@Data
@Entity
public class BuildData extends ManagedEntity {
    private ProgLanguage progLanguage;
    private ArtifactPackaging artifactPackaging;
    @OneToMany(cascade = CascadeType.ALL)
    private List<EntityStorePolicy> entityStorePref;
    @JsonIgnore
    private boolean latest;
    @OneToOne
    private App app;
    private boolean secure;
    @OneToMany(cascade = CascadeType.ALL)
    private List<RelationshipStorePolicy> relationshipStorePolicy;

    public static BuildData fromModel(BuildModel model, App app){
        BuildData data = new BuildData();
        data.setLatest(true);
        data.setProgLanguage(model.getProgLanguage());
        data.setArtifactPackaging(model.getArtifactPackaging());
        data.setApp(app);
        data.setEntityStorePref(model.getEntityStorePref());
        data.setSecure(model.isSecure());
        data.setRelationshipStorePolicy(model.getRelationshipStorePolicy());
        return data;
    }

    @JsonIgnore
    public BuildModel toBuildModel(){
        BuildModel model = new BuildModel();
        model.setEntityStorePref(this.entityStorePref);
        model.setApp(this.app);
        model.setArtifactPackaging(this.artifactPackaging);
        model.setProgLanguage(this.progLanguage);
        model.setSecure(this.secure);
        model.setRelationshipStorePolicy(this.relationshipStorePolicy);
        return model;
    }

}
