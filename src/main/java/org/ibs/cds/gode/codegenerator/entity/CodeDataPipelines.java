package org.ibs.cds.gode.codegenerator.entity;

import lombok.Data;
import lombok.SneakyThrows;
import org.ibs.cds.gode.entity.type.DataPipeline;
import org.ibs.cds.gode.util.YamlReadWriteUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
@Data
public class CodeDataPipelines{

    private DataPipeline config;
    private String pipelineSettings;
    private List<CodeDataPipeline> pipelines;

    @SneakyThrows
    public CodeDataPipelines(Set<CodeEntity> entitySet, DataPipeline config) {
        if(config != null){
            this.config = config;
            Map<String, CodeEntity> entityMap = entitySet.stream().collect(Collectors.toMap(s->s.getName(), s->s));
            this.pipelineSettings = YamlReadWriteUtil.toString(config);
            this.pipelines = config.getPipelines().stream().map(k->new CodeDataPipeline(entityMap, k)).collect(Collectors.toList());
        }
    }
}
