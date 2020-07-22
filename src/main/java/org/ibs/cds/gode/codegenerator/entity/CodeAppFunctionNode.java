package org.ibs.cds.gode.codegenerator.entity;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.ibs.cds.gode.codegenerator.artefact.Buildable;
import org.ibs.cds.gode.codegenerator.config.CodeGenerationComponent;
import org.ibs.cds.gode.codegenerator.model.build.BuildModel;
import org.ibs.cds.gode.entity.type.App;
import org.ibs.cds.gode.entity.type.Specification;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class CodeAppFunctionNode extends Specification implements Buildable, CodeGenerationComponent {

    private BuildModel buildModel;
    private Set<CodeAppFunction> functions;
    private Set<CodeEntityFunction> entityFunctions;


    public CodeAppFunctionNode(App app, BuildModel buildModel, Set<CodeEntityFunction> entityFunctions) {
        this.buildModel = buildModel;
        this.setName(app.getName().concat("-").concat("functions").toLowerCase());
        this.setVersion(app.getVersion());
        var functions = app.getFunctions();
        this.functions = CollectionUtils.isEmpty(functions) ?
                Set.of() :
                functions.stream().map(CodeAppFunction::new).collect(Collectors.toSet());
        this.entityFunctions = entityFunctions;
    }

    @Override
    public ComponentName getComponentName() {
        return ComponentName.APP_FUNCTION;
    }
}
