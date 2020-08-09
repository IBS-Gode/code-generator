package org.ibs.cds.gode.codegenerator.entity;

import lombok.Data;
import org.ibs.cds.gode.entity.generic.Try;
import org.ibs.cds.gode.exception.KnownException;
import org.ibs.cds.gode.stream.config.DataPipelineConf;
import org.ibs.cds.gode.util.YamlReadWriteUtil;

import java.util.List;
import java.util.stream.Collectors;
@Data
public class CodeDataPipelines{

    private DataPipelineConf config;
    private String pipelineSettings;
    private List<CodeDataPipeline> pipelines;

    public CodeDataPipelines(DataPipelineConf config) {
        this.config = config;
        this.pipelineSettings = Try
                .code((DataPipelineConf c) -> YamlReadWriteUtil.toString(c))
                .catchWith(KnownException.INVALID_CONFIG_EXCPETION.provide("Pipelineconfiguration cannot be read"))
                .run(config).orElse(null);
        this.pipelines = config.getPipelines().stream().map(CodeDataPipeline::new).collect(Collectors.toList());
    }
}
