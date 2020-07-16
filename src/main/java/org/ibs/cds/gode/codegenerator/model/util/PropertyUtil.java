package org.ibs.cds.gode.codegenerator.model.util;

import lombok.extern.slf4j.Slf4j;
import org.ibs.cds.gode.codegenerator.entity.CodeApp;
import org.ibs.cds.gode.codegenerator.entity.CodeAppUtil;
import org.ibs.cds.gode.codegenerator.exception.CodeGenerationFailure;
import org.ibs.cds.gode.codegenerator.model.deploy.Action;
import org.ibs.cds.gode.codegenerator.model.deploy.LocalDeploymentRequirement;
import org.ibs.cds.gode.status.BinaryStatus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Slf4j
public class PropertyUtil {

    public static boolean update( String probablePath, List<LocalDeploymentRequirement> requirements, CodeApp app) {
        try {
            return requirements.stream().map(deploymentRequirement ->
                            deploymentRequirement.getActions().stream().map(action -> {
                                String controlFile = action.getComponentName().getControlFile();
                                return getFile(probablePath)
                                        .map(Path::toFile)
                                        .filter(isResource())
                                        .filter(k -> k.getName().equals(controlFile))
                                        .peek(k->log.info("Found {} for updating {}", k,deploymentRequirement.getPropertyName()))
                                        .findAny()
                                        .map(propertyFileLocation -> update(action, deploymentRequirement, app, propertyFileLocation))
                                        .orElse(BinaryStatus.FAILURE);
                            })).flatMap(k->k).allMatch(k->k == BinaryStatus.SUCCESS);

        } catch (Exception e) {
            return false;
        }
    }

    public static Stream<Path> getFile(String probablePath) {
        try {
            return Files.walk(Paths.get(probablePath));
        } catch (Exception e) {
            throw CodeGenerationFailure.DEPLOYMENT_FAILURE.provide(e);
        }
    }

    @NotNull
    public static Predicate<File> isResource() {
        return k->k.isFile() && k.getAbsolutePath().contains(CodeAppUtil.javaResourcePath());
    }

    @NotNull
    public static BinaryStatus update(Action action, LocalDeploymentRequirement requirement, CodeApp app, File propertyFileLocation) {
        try {
            if (requirement == null) return BinaryStatus.FAILURE;
            FileInputStream in = new FileInputStream(propertyFileLocation);
            Properties props = new Properties();
            props.load(in);
            in.close();
            FileOutputStream out = new FileOutputStream(propertyFileLocation);
             props.setProperty(action.getProperty(), action.getValue(requirement, app));
            props.store(out, "Deployment changes applied: ".concat(requirement.getPropertyName()));
            out.close();
            return BinaryStatus.SUCCESS;
        } catch (Exception e) {
            return BinaryStatus.FAILURE;
        }
    }
}
