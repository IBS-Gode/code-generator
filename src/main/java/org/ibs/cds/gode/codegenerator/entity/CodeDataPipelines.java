package org.ibs.cds.gode.codegenerator.entity;

import lombok.Data;
import lombok.SneakyThrows;
import org.ibs.cds.gode.entity.type.DataPipeline;
import org.ibs.cds.gode.util.YamlReadWriteUtil;

import java.util.List;
import java.util.stream.Collectors;
@Data
public class CodeDataPipelines{

    private DataPipeline config;
    private String pipelineSettings;
    private List<CodeDataPipeline> pipelines;

    @SneakyThrows
    public CodeDataPipelines(DataPipeline config) {
        if(config != null){
            this.config = config;
            this.pipelineSettings = YamlReadWriteUtil.toString(config);
            this.pipelines = config.getPipelines().stream().map(CodeDataPipeline::new).collect(Collectors.toList());
        }
    }
}
