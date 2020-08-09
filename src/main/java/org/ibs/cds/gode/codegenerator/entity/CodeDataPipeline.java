package org.ibs.cds.gode.codegenerator.entity;

import lombok.Data;
import org.ibs.cds.gode.stream.config.Node;
import org.ibs.cds.gode.stream.config.Pipeline;
import org.ibs.cds.gode.stream.config.Sink;
import org.ibs.cds.gode.stream.config.StreamSourceType;
import org.ibs.cds.gode.util.StringUtils;

import java.util.List;
@Data
public class CodeDataPipeline {

    private Pipeline config;
    private String supplierEntity;
    private List<CodePiplineSynchroniser> stateSynchronisers;
    private List<CodeDataPipelineProcessor> processors;

    public CodeDataPipeline(Pipeline config) {
        this.config = config;
        populatePublisherTypes(config);
        populateProcessors(config.getSource().getEntity(), config.getSource().getNext());
    }

    private void populatePublisherTypes(Pipeline config) {
        StreamSourceType type = config.getSource().getType();
        switch (type){
            case SUPPLIER:
                supplierEntity = config.getSource().getEntity();
                break;
        }
    }

    private void populateProcessors(String from , Node next) {
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

    private void populateSynchroniserTypes(Sink sink) {
        stateSynchronisers.add(new CodePiplineSynchroniser(sink.getName(), sink.getEntity()));
    }

    public boolean isSuppliedFromEntity(){
        return StringUtils.isNotBlank(this.supplierEntity);
    }
}
