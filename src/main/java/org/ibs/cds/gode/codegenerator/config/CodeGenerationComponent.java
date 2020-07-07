package org.ibs.cds.gode.codegenerator.config;

import lombok.Getter;
import org.ibs.cds.gode.util.StringUtils;

public interface CodeGenerationComponent {

    enum ComponentName{
        APP("gode.properties"),
        ENTITY,
        APP_FUNCTION,
        RELATIONSHIP,
        APP_MIGRATION("liquibase.properties"),
        ADMIN("application.properties");

        private @Getter final String controlFile;
        ComponentName( String controlFile) {
            this.controlFile = controlFile;
        }
        ComponentName() {
            this.controlFile = StringUtils.EMPTY;
        }
    }

    ComponentName getComponentName();
}
