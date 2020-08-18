package org.ibs.cds.gode.codegenerator.model.deploy;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.ibs.cds.gode.codegenerator.config.CodeGenerationComponent;
import org.ibs.cds.gode.codegenerator.entity.CodeApp;

import java.util.function.Function;

@Data
@RequiredArgsConstructor
public class Action {
    private final CodeGenerationComponent.ComponentName componentName;
    private final String property;
    private final Function<DeploymentRequirements, Function<CodeApp,String>> valueFunction;
    private final Function<DeploymentRequirements, Function<CodeApp,String>> propertyFunction;

    public String getValue(DeploymentRequirements requirement, CodeApp app){
        return valueFunction == null ? requirement.getValue() : valueFunction.apply(requirement).apply(app);
    }
    public String getProperty(DeploymentRequirements requirement, CodeApp app){
        return propertyFunction == null ? property : propertyFunction.apply(requirement).apply(app);
    }
    public static Action of(CodeGenerationComponent.ComponentName componentName, String property, Function<DeploymentRequirements, Function<CodeApp,String>> valueFunction){
        return new Action(componentName, property, valueFunction, null);
    }
    public static Action of(CodeGenerationComponent.ComponentName componentName, String property, Function<DeploymentRequirements, Function<CodeApp,String>> valueFunction, Function<DeploymentRequirements, Function<CodeApp,String>> propertyFunction){
        return new Action(componentName, property, valueFunction,propertyFunction);
    }

    public static Action of(CodeGenerationComponent.ComponentName componentName, String property){
        return new Action(componentName, property, null, null);
    }

    public CodeGenerationComponent.ComponentName getComponentName(){
        return componentName;
    }

}
