package org.ibs.cds.gode.codegenerator.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeDataPipelineEntitySupplier {
    private String pipelineName;
    private String entityName;
    private String idType;
}
