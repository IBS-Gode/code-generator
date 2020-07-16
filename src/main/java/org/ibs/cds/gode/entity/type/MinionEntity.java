package org.ibs.cds.gode.entity.type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.ibs.cds.gode.entity.store.StoreType;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
public class MinionEntity extends JPAEntity<Long> implements Serializable {


    public Long getArtifactId() {
        return artifactId;
    }
    private boolean active = true;

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
    public boolean isActive() {
        return active;
    }

    @Override @JsonIgnore
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override @JsonIgnore
    public Date getCreatedOn() {
        return super.getCreatedOn();
    }

    @Override @JsonIgnore
    public void setCreatedOn(Date createdOn) {
        super.setCreatedOn(createdOn);
    }

    @Override @JsonIgnore
    public Date getUpdatedOn() {
        return super.getUpdatedOn();
    }

    @Override @JsonIgnore
    public void setUpdatedOn(Date updatedOn) {
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
    public StoreType getStoreType() {
        return StoreType.JPA;
    }
}
