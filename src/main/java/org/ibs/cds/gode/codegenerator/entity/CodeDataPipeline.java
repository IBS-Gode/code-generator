package org.ibs.cds.gode.codegenerator.entity;

import lombok.Data;
import org.ibs.cds.gode.entity.type.Pipeline;
import org.ibs.cds.gode.entity.type.PipelineNode;
import org.ibs.cds.gode.entity.type.PipelineSink;
import org.ibs.cds.gode.entity.type.PipelineSourceType;
import org.ibs.cds.gode.util.Assert;
import org.ibs.cds.gode.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class CodeDataPipeline {

    private String name;
    private Pipeline config;
    private CodeDataPipelineEntitySupplier supplierEntity;
    private CodePiplineSynchroniser stateSynchroniser;
    private List<CodeDataPipelineProcessor> processors;

    public CodeDataPipeline(Map<String, CodeEntity> entityMap, Pipeline config) {
        this.name = config.getName();
        this.config = config;
        this.processors = new ArrayList();
        populatePublisherTypes(entityMap, config);
        populateProcessors(config.getSource().getEntity(), config.getSource().getNext());
    }

    private void populatePublisherTypes(Map<String, CodeEntity> entityMap, Pipeline config) {
        PipelineSourceType type = config.getSource().getType();
        switch (type){
            case SUPPLIER:
                CodeEntity entity = entityMap.get(config.getSource().getEntity());
                Assert.notNull("Data pipeline source entity cannot be empty", entity);
                supplierEntity = new CodeDataPipelineEntitySupplier(config.getName(), entity.getName(), entity.getIdField().getObject().getFQN());
                break;
        }
    }

    private void populateProcessors(String from , PipelineNode next) {
        String to = next.getMapTo();
        processors.add(new CodeDataPipelineProcessor(next.getName(), name, from, to));
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
        return null != this.supplierEntity;
    }
}
