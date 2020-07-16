package org.ibs.cds.gode.codegenerator.entity;

import lombok.Data;
import org.ibs.cds.gode.codegenerator.artefact.Buildable;
import org.ibs.cds.gode.codegenerator.config.CodeGenerationComponent;
import org.ibs.cds.gode.codegenerator.model.build.BuildModel;
import org.ibs.cds.gode.entity.type.Specification;

/**
 *
 * @author manugraj
 */
@Data
public class CodeAppPipeline extends Specification implements Buildable, CodeGenerationComponent{
    
     public CodeAppPipeline(CodeApp app, BuildModel buildModel) {
        super();
        this.setName(app.getName().concat("-").concat("Pipeline"));
        this.buildModel = buildModel;
        this.app = app;
    }

    private final BuildModel buildModel; 
    private final CodeApp app;
    
    @Override
    public ComponentName getComponentName() {
        return ComponentName.PIPELINE;
    }

}
