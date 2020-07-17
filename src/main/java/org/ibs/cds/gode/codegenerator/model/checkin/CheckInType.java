package org.ibs.cds.gode.codegenerator.model.checkin;

import lombok.Getter;

public enum CheckInType {
    FEATURE("feat"),
    BUGFIX("fix"),
    GENERIC("generic"),
    HOTFIX("hotfix");

    private @Getter final String branchPrefix;

    CheckInType(String branchPrefix){
        this.branchPrefix = branchPrefix;
    }

}
