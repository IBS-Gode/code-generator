package org.ibs.cds.gode.codegenerator.entity;

import org.ibs.cds.gode.codegenerator.config.CodeGenerationComponent;
import org.ibs.cds.gode.codegenerator.config.EngineConfiguration;
import org.ibs.cds.gode.entity.type.App;

public class CodeAppUtil {

    public static String adminAppName(CodeApp app) {
        return app.getName().concat("-Monitor");
    }

    public static String containerAppName(CodeApp app) {
        return containerAppName(app.getName());
    }
    
    public static String containerAppName(String appName) {
        return appName.concat("-container");
    }

    public static String containerAppName(App app) {
        return app.getName().concat("-container");
    }

    public static String functionAppName(CodeAppFunction function) {
        return function.getName().concat("-container");
    }

    public static String javaPath() {
        return PathPackage.path("src", "main", "java");
    }

    public static String testPath() {
        return PathPackage.path("src", "test", "java");
    }

    public static String pipelinePath() {
        return PathPackage.path("build");
    }

    public static String javaResourcePath() {
        return PathPackage.path("src", "main", "resources");
    }

    public static String graphqlPath() {
        return PathPackage.path(CodeAppUtil.javaResourcePath(), "graphql");
    }

    public static String pipelinePath(CodeApp app) {
        return PathPackage.path(appPath(app), "build").toLowerCase();
    }

    public static String testcasePath(CodeAppTestCase testCase) {
        return PathPackage.path(appPath(testCase.getApp()), testCase.getName()).toLowerCase();
    }

    public static String appPath(App app) {
        return appPath(app.getName(), String.valueOf(app.getVersion()));
    }

    public static String appPath(CodeApp app) {
        return appPath(app.getName(), String.valueOf(app.getVersion()));
    }

    public static String customisableApp(CodeApp app) {
        return PathPackage.path(appPath(app.getName(), String.valueOf(app.getVersion())), CodeGenerationComponent.CUSTOMISE).toLowerCase();
    }

    public static String customisableApp(App app) {
        return PathPackage.path(appPath(app.getName(), String.valueOf(app.getVersion())), CodeGenerationComponent.CUSTOMISE).toLowerCase();
    }
    
    public static String appPath(String appName, String appVersion) {
        return PathPackage.path(EngineConfiguration.getCodeGenPath(), appVersion, CodeAppUtil.containerAppName(appName).toLowerCase());
    }
}
