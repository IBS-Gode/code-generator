package org.ibs.cds.gode.codegenerator.entity;

import com.google.inject.internal.asm.$Type;
import lombok.Data;
import org.ibs.cds.gode.entity.type.Pipeline;
import org.ibs.cds.gode.entity.type.PipelineNode;
import org.ibs.cds.gode.entity.type.PipelineSink;
import org.ibs.cds.gode.entity.type.PipelineSourceType;
import org.ibs.cds.gode.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
@Data
public class CodeDataPipeline {

    private String name;
    private Pipeline config;
    private String supplierEntity;
    private CodePiplineSynchroniser stateSynchroniser;
    private List<CodeDataPipelineProcessor> processors;

    public CodeDataPipeline(Pipeline config) {
        this.name = config.getName();
        this.config = config;
        this.processors = new ArrayList();
        populatePublisherTypes(config);
        populateProcessors(config.getSource().getEntity(), config.getSource().getNext());
    }

    private void populatePublisherTypes(Pipeline config) {
        PipelineSourceType type = config.getSource().getType();
        switch (type){
            case SUPPLIER:
                supplierEntity = config.getSource().getEntity();
                break;
        }
    }

    private void populateProcessors(String from , PipelineNode next) {
        String to = next.getMapTo();
        processors.add(new CodeDataPipelineProcessor(next.getName(), from, to));
        if(next.getNext() != null){
            populateProcessors(to, next.getNext());
            return;
        }
        if(next.getSink() != null){
            populateSynchroniserTypes(next.getSink());
            return;
        }
    }

    private void populateSynchroniserTypes(PipelineSink sink) {
        stateSynchroniser = new CodePiplineSynchroniser(sink.getName(), sink.getEntity());
    }

    public boolean isSuppliedFromEntity(){
        return StringUtils.isNotBlank(this.supplierEntity);
    }
}
