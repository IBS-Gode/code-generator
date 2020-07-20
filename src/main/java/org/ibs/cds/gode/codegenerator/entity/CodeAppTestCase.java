package org.ibs.cds.gode.codegenerator.entity;

import lombok.Data;
import org.ibs.cds.gode.codegenerator.artefact.Buildable;
import org.ibs.cds.gode.codegenerator.config.CodeGenerationComponent;
import org.ibs.cds.gode.codegenerator.model.build.BuildModel;
import org.ibs.cds.gode.entity.type.Specification;

import java.util.Set;

@Data
public class CodeAppTestCase extends Specification implements Buildable, CodeGenerationComponent {

    private final CodeApp app;
    private Set<CodeEntity> entities;

    public CodeAppTestCase(CodeApp app, BuildModel buildModel){
        this.app = app;
        entities = app.getEntities();
        this.setName(app.getName().concat("-test"));
        this.setVersion(app.getVersion());
        this.setDescription("Test project for ".concat(app.getName()));
    }


    @Override
    public ComponentName getComponentName() {
        return ComponentName.TEST_CASE;
    }
}
