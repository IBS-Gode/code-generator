package org.ibs.cds.gode.entity.type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.ibs.cds.gode.entity.store.IStoreType;
import org.ibs.cds.gode.entity.store.StoreEntity;
import org.ibs.cds.gode.entity.store.StoreType;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.OffsetDateTime;

@MappedSuperclass
public class MinionEntity extends StoreEntity<Long> implements Serializable {


    public Long getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(Long artifactId) {
        this.artifactId = artifactId;
    }

    @Id
    private Long artifactId;
    @Override @JsonIgnore
    public Long getId() {
        return this.artifactId;
    }

    @Override @JsonIgnore
    public void setId(Long artifactId) {
        this.artifactId = artifactId;
    }

    @Override @JsonIgnore
    public boolean isValidated() {
        return super.isValidated();
    }

    @Override @JsonIgnore
    public void setValidated(boolean validated) {
        super.setValidated(validated);
    }

    @Override @JsonIgnore
    public Boolean isActive() {
        return super.isActive();
    }

    @Override @JsonIgnore
    public void setActive(Boolean active) {
        super.setActive(active);
    }

    @Override @JsonIgnore
    public OffsetDateTime getCreatedOn() {
        return super.getCreatedOn();
    }

    @Override @JsonIgnore
    public void setCreatedOn(OffsetDateTime createdOn) {
        super.setCreatedOn(createdOn);
    }

    @Override @JsonIgnore
    public OffsetDateTime getUpdatedOn() {
        return super.getUpdatedOn();
    }

    @Override @JsonIgnore
    public void setUpdatedOn(OffsetDateTime updatedOn) {
        super.setUpdatedOn(updatedOn);
    }

    @Override @JsonIgnore
    public Long getAppId() {
        return super.getAppId();
    }

    @Override @JsonIgnore
    public void setAppId(Long appId) {
        super.setAppId(appId);
    }

    @Override @JsonIgnore
    public IStoreType getStoreType() {
        return StoreType.JPA;
    }
}
