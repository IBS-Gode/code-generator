package org.ibs.cds.gode.codegenerator.spec;


import lombok.Getter;
import org.ibs.cds.gode.entity.store.StoreType;


public enum StoreName {
    MONGODB(StoreType.MONGODB, Level.HIGH, Level.MEDIUM, Level.LOW, true, true),
    MYSQL(StoreType.JPA, Level.LOW, Level.LOW, Level.LOW, true, true),
    ORACLE_DB(StoreType.JPA, Level.HIGH, Level.MEDIUM, Level.LOW, true, true),
    DB2(StoreType.JPA, Level.MEDIUM, Level.LOW, Level.LOW, true, true),
    POSTGRE_SQL(StoreType.JPA, Level.HIGH, Level.MEDIUM, Level.LOW, true, true),
    MARIA_DB(StoreType.JPA, Level.LOW, Level.LOW, Level.LOW, true, true),
    CASSANDRA(StoreType.CASSANDRA, Level.HIGH, Level.HIGH, Level.LOW, false, false),
    ELASTICSEARCH(StoreType.ELASTICSEARCH, Level.MEDIUM, Level.MEDIUM, Level.LOW, false, false),
    NEO4J(StoreType.NEO4J, Level.HIGH, Level.MEDIUM, Level.HIGH, true, false);

    private final @Getter
    StoreType storeType;
    private final @Getter
    Level readLevel;
    private final @Getter
    Level writeLevel;
    private final @Getter
    Level realtiveRead;
    private final @Getter
    Boolean transactional;
    private  @Getter final boolean dynamicQuery;
    StoreName(StoreType storeType, Level readLevel, Level writeLevel, Level realtiveRead, Boolean transactional, boolean dynamicQuery) {
        this.storeType = storeType;
        this.readLevel = readLevel;
        this.writeLevel = writeLevel;
        this.realtiveRead = realtiveRead;
        this.transactional = transactional;
        this.dynamicQuery = dynamicQuery;
    }

    StoreName(StoreType storeType, Level readLevel, Level writeLevel, Level realtiveRead, Boolean transactional) {
        this.storeType = storeType;
        this.readLevel = readLevel;
        this.writeLevel = writeLevel;
        this.realtiveRead = realtiveRead;
        this.transactional = transactional;
        this.dynamicQuery = false;
    }

    public String digest() {
        return readLevel.toString().substring(0, 1) + writeLevel.toString().substring(0, 1) + readLevel.toString().substring(0, 1) + transactional.toString().substring(0, 1);
    }

}
