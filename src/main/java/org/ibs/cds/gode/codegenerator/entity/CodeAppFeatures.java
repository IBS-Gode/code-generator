package org.ibs.cds.gode.codegenerator.entity;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.ibs.cds.gode.entity.store.StoreType;
import org.ibs.cds.gode.entity.type.FieldType;

@Data
public class CodeAppFeatures {
    private final boolean cacheRequired;
    private final boolean jpaStoreRequired;
    private final boolean mongoRequired;
    private final boolean cassandraRequired;
    private final boolean statefulEntityIncluded;
    private final boolean statelessEntityIncluded;
    private final boolean relationshipEntityIncluded;
    private final boolean queueSystemRequired;
    private final boolean secure;
    private final boolean autosequenceFields;

    public CodeAppFeatures(CodeApp app) {
        this.cacheRequired = isCacheRequired(app);
        this.jpaStoreRequired = app.getEntities().stream().anyMatch(k -> k.getStorePolicy().isAvailable() &&
                k.getStorePolicy().getPolicy().getStoreName().getStoreType() == StoreType.JPA);
        this.mongoRequired = app.getEntities().stream().anyMatch(k -> k.getStorePolicy().isAvailable() &&
                k.getStorePolicy().getPolicy().getStoreName().getStoreType() == StoreType.MONGODB);
        this.statefulEntityIncluded = app.getEntities().stream().anyMatch(k -> k.getStorePolicy().isAvailable());
        this.statelessEntityIncluded = app.getEntities().stream().anyMatch(k -> !k.getStorePolicy().isAvailable());
        this.relationshipEntityIncluded = CollectionUtils.isNotEmpty(app.getRelationships());
        this.queueSystemRequired = app.isSystemQueue() ||
                app.getEntities().stream().anyMatch(k -> k.getStorePolicy().isAsync());
        this.secure = app.isSecure();
        this.cassandraRequired = app.getEntities().stream().anyMatch(k -> k.getStorePolicy().isAvailable() &&
                k.getStorePolicy().getPolicy().getStoreName().getStoreType() == StoreType.CASSANDRA);
        this.autosequenceFields = app.getEntities().stream().flatMap(k->k.getFields().stream()).anyMatch(CodeEntityField::isAutosequence);
    }

    private boolean isCacheRequired(CodeApp app) {
        return app.getEntities().stream().anyMatch(k ->
                (k.getStorePolicy().isAvailable() && k.getStorePolicy().isCached()) ||
                        k.getFields().stream().map(j -> j.getField()).anyMatch(i -> i.getType() == FieldType.SEQUENCE));
    }
}
