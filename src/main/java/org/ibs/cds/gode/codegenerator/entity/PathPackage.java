package org.ibs.cds.gode.codegenerator.entity;

import lombok.Getter;
import org.ibs.cds.gode.util.Assert;
import org.ibs.cds.gode.util.StringUtils;

import java.io.File;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum PathPackage {

    DEFAULT_DIRECTORY("org.ibs.cds.gode",true),
    DEFAULT__ENTITY_DIRECTORY(DEFAULT_DIRECTORY.getValue().concat(".entity"),true),
    DEFAULT__ENTITY_TYPE_DIRECTORY(DEFAULT__ENTITY_DIRECTORY.getValue().concat(".type"),true),
    DEFAULT__ENTITY_VIEW_DIRECTORY(DEFAULT__ENTITY_DIRECTORY.getValue().concat(".view"),true),
    DEFAULT__ENTITY_MANAGER_DIRECTORY(DEFAULT__ENTITY_DIRECTORY.getValue().concat(".manager"),true),

    DEFAULT__ENTITY_REPO_DIRECTORY(DEFAULT__ENTITY_DIRECTORY.getValue().concat(".repo"),true),
    DEFAULT__ENTITY_STORE_REPO_DIRECTORY(DEFAULT__ENTITY_DIRECTORY.getValue().concat(".store.repo"),true),
    DEFAULT__ENTITY_CACHE_REPO_DIRECTORY(DEFAULT__ENTITY_DIRECTORY.getValue().concat(".cache.repo"),true),

    DEFAULT__ENTITY_CONTROLLER_DIRECTORY(DEFAULT__ENTITY_DIRECTORY.getValue().concat(".controller"),true),
    DEFAULT__ENTITY_GQL_DIRECTORY(DEFAULT__ENTITY_DIRECTORY.getValue().concat(".gql"),true),
    DEFAULT__ENTITY_FUNCTION_DIRECTORY(DEFAULT__ENTITY_DIRECTORY.getValue().concat(".function"), true),
    DEFAULT__ENTITY_QUERY_DIRECTORY(DEFAULT__ENTITY_DIRECTORY.getValue().concat(".query"), true),

    DEFAULT__APP_DATAPIPELINE_DIRECTORY(DEFAULT_DIRECTORY.getValue().concat(".stream"),true),
    DEFAULT__APP_DATAPIPELINE_SYNC_DIRECTORY(DEFAULT__APP_DATAPIPELINE_DIRECTORY.getValue().concat(".synchroniser"),true),
    DEFAULT__APP_DATAPIPELINE_PUBLISHER_DIRECTORY(DEFAULT__APP_DATAPIPELINE_DIRECTORY.getValue().concat(".publisher"),true),

    DEFAULT__APP_DIRECTORY(DEFAULT_DIRECTORY.getValue().concat(".app"),true),
    DEFAULT__APP_FUNCTION_DIRECTORY(DEFAULT__APP_DIRECTORY.getValue().concat(".function"),true),
    DEFAULT_GROUP_ID("org.ibs.cds.gode",false);
    ;


    private @Getter final String value;
    private @Getter final boolean composable;
    PathPackage(String value, boolean composable){
        this.value = value;
        this.composable = composable;
    }

    public String compose(String base, boolean prefix){
        Assert.notNull("Base cannot be null", base);
        return this.composable ? prefix ? base.concat(File.separator).concat(StringUtils.replace(this.getValue(),".", File.separator)) : StringUtils.replace(this.getValue(),".", File.separator).concat(File.separator).concat(base) : this.value;
    }

    public String composePrefix(String base){
        Assert.notNull("Base cannot be null", base);
        return this.composable ? compose(base, true): this.value;
    }

    public static String path(String... pathElements){
        return Stream.of(pathElements).collect(Collectors.joining(File.separator));
    }

    public String pathOf(String... pathElements){
        return compose(Stream.of(pathElements).collect(Collectors.joining(File.separator)), true);
    }
}
