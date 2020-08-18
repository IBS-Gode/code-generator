package org.ibs.cds.gode.codegenerator.config;

import org.ibs.cds.gode.codegenerator.bind.ArtifactPackaging;
import org.ibs.cds.gode.codegenerator.exception.CodeGenerationFailure;
import org.ibs.cds.gode.codegenerator.model.build.BuildModel;
import org.ibs.cds.gode.codegenerator.spec.ProgLanguage;

import java.io.File;
import java.util.Set;
import java.util.StringJoiner;

public interface BuildComponentConfiguration {
    String CONFIG_PATH = "config";
    String TEMPLATE_PATH = "template";
    String CONFIG_FILE = "configuration.yml";
    Set<ComponentConfiguration> getConfiguration();
    ProgLanguage getLanguage();
    ArtifactPackaging getPackingSystem();

    static Class<? extends BuildComponentConfiguration> getConfiguration(BuildModel model){
        ProgLanguage language = model.getProgLanguage();
        ArtifactPackaging packaging = model.getArtifactPackaging();
        if(language == ProgLanguage.JAVA && packaging == ArtifactPackaging.MAVEN){
            return JavaMavenBuildComponentConfiguration.class;
        }
        throw CodeGenerationFailure.LANGUAGE_NOT_FOUND.provide(language.toString().concat("/").concat(packaging.toString()));
    }

    static String getComponentConfigFile(BuildModel model){
        StringJoiner path = new StringJoiner(File.separator);
        return path.add(CONFIG_PATH).add(model.getProgLanguage().toString()).add(model.getArtifactPackaging().toString()).add(CONFIG_FILE).toString();
    }

    static String getComponentTemplatePath(BuildModel model, CodeGenerationComponent component){
        StringJoiner path = new StringJoiner(File.separator);
        return path
                .add(TEMPLATE_PATH)
                .add(model.getProgLanguage().toString())
                .add(model.getArtifactPackaging().toString())
                .add(component.getComponentName().toString().toLowerCase())
                .toString();
    }

    default String getComponentConfigFile(){
        StringJoiner path = new StringJoiner(File.separator);
        return path.add(CONFIG_PATH).add(getLanguage().toString()).add(getPackingSystem().toString()).add(CONFIG_FILE).toString();
    }

    default String getComponentTemplatePath(CodeGenerationComponent component){
        StringJoiner path = new StringJoiner(File.separator);
        return path
                .add(TEMPLATE_PATH)
                .add(getLanguage().toString())
                .add(getPackingSystem().toString())
                .add(component.getComponentName().toString().toLowerCase())
                .toString();
    }
}
