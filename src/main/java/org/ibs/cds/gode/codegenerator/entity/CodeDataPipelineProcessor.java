package org.ibs.cds.gode.codegenerator.entity;

import lombok.Data;

@Data
public class CodeDataPipelineProcessor {
    private final String name;
    private final String from;
    private final String to;
}
