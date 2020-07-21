package org.ibs.cds.gode.codegenerator.config;

import lombok.Getter;
import org.ibs.cds.gode.util.StringUtils;

public interface CodeGenerationComponent {
    String CUSTOMISE = "customise";
    String FRAMEWORK = "framework";

    enum ComponentName{
        APP("gode.properties", false),
        ENTITY(false),
        APP_FUNCTION(true),
        RELATIONSHIP(false),
        APP_MIGRATION("liquibase.properties", false),
        PIPELINE(true),
        TEST_CASE(true),
        ADMIN("application.properties", false);

        private @Getter final String controlFile;
        private @Getter final String nature;

        ComponentName( String controlFile, boolean customisable) {
            this.controlFile = controlFile;
            if(customisable){
                this.nature = CUSTOMISE.intern();
            }else{
                this.nature = FRAMEWORK.intern();
            }
        }
        ComponentName() {
            this.controlFile = StringUtils.EMPTY;
            this.nature = FRAMEWORK.intern();
        }
        ComponentName(boolean customisable) {
            this(StringUtils.EMPTY, customisable);
        }

    }

    ComponentName getComponentName();
    default String getNature(){
        return getComponentName().getNature();
    }
}
