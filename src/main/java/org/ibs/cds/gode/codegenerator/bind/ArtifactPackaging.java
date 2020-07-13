package org.ibs.cds.gode.codegenerator.bind;

public enum ArtifactPackaging {
    MAVEN,
    GRADLE;


    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
