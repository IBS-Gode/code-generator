package org.ibs.cds.gode.codegenerator.velocity;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.ibs.cds.gode.codegenerator.config.*;
import org.ibs.cds.gode.codegenerator.exception.CodeGenerationFailure;
import org.ibs.cds.gode.entity.type.Specification;
import org.ibs.cds.gode.codegenerator.spec.YamlReadWriteUtil;
import org.ibs.cds.gode.status.BinaryStatus;
import org.ibs.cds.gode.system.GodeConstant;
import org.ibs.cds.gode.util.CodeGenUtil;
import org.ibs.cds.gode.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Slf4j
@RequiredArgsConstructor
public class VelocityGeneratorEngine<T extends Specification & CodeGenerationComponent> implements CodeGeneratorEngine<T> {

    private final EngineConfiguration engineConfiguration;
    private final VelocityContext context;
    private final Set<String> buildable;
    private final String repo;

    public VelocityGeneratorEngine(EngineConfiguration engineConfiguration, String repo) {
        this.engineConfiguration = engineConfiguration;
        this.context = new VelocityContext();
        context.put("StringUtils", StringUtils.class);
        context.put("GodeConstant", GodeConstant.class);
        context.put("Channel",VelocityChannel.class);
        context.put("CodeGenUtil", CodeGenUtil.class);
        this.buildable = new HashSet();
        this.repo = repo;
    }

    @Override
    public EngineConfiguration getConfiguration() {
        return this.engineConfiguration;
    }

    @Override
    public boolean addToContext(String name, Object input) {
        return context.put(name, input) != null;
    }

    private GenerateComponentConfiguration process(T component) throws IOException {
        log.info("Processing Component:{} version:{} Type: {}", component.getName(), component.getVersion(), component.getComponentName());
        ComponentConfiguration componentConfiguration = this.engineConfiguration.getComponentConfiguration().get(component.getComponentName());
        Reader template = getReader(this.engineConfiguration.getTemplatePath(component), componentConfiguration.getVmLocation());
        String processPath = getProcessPath(component);
        build("Process ".concat(component.getComponentName().toString().toLowerCase()),template, processPath);
        log.info("Processed Component:{} version:{} Type: {}", component.getName(), component.getVersion(), component.getComponentName());
        return YamlReadWriteUtil.readFile(new File(processPath), GenerateComponentConfiguration.class);
    }

    @NotNull
    private Reader getReader(String templatePath, String vmLocation) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource resource = resolver.getResource(templatePath.concat(File.separator).concat(vmLocation));
        return new BufferedReader(new InputStreamReader(resource.getInputStream()));
    }

    private void build(String tag, Reader template, String processPath) throws IOException {
        File outFile = new File(processPath);
        if(outFile.exists()){
            outFile.delete();
        }
        outFile.getParentFile().mkdirs();
        Writer writer = new FileWriter(outFile);
        Velocity.evaluate(this.context, writer, tag, template);
        writer.flush();
        writer.close();
    }


    private boolean generate(T component, GenerateComponentConfiguration generateComponent) throws IOException {
        log.info("Generating Component:{} version:{} Type: {}", component.getName(), component.getVersion(), component.getComponentName());
        List<GenerateComponent> configurations = generateComponent.getConfiguration();
        for (GenerateComponent generationSpec: configurations) {
            Reader templateReader = getReader(this.engineConfiguration.getBuildConfiguration().getComponentTemplatePath(component), generationSpec.getTemplate());
            this.context.put("key",generationSpec.getKey());
            String fileOut = this.engineConfiguration.getCodeGenPath().concat(File.separator).concat(File.separator).concat(getRepo()).concat(File.separator).concat(generationSpec.getPath()).concat(File.separator).concat(generationSpec.getName());
            build("Generate ".concat(component.getComponentName().toString().toLowerCase()),templateReader, fileOut);
            if(generationSpec.isBuildable()){
                buildable.add(fileOut);
            }
            log.info("Generated  Element:{}", generationSpec.getName());
        }
        log.info("Generated Component:{} version:{} Type: {}", component.getName(), component.getVersion(), component.getComponentName());
        return true;
    }

    @Override
    public BinaryStatus run(T component) {
        try {
            this.context.put(component.getComponentName().toString(), component);
            return BinaryStatus.valueOf(this.generate(component, process(component)));
        } catch (IOException e) {
            throw CodeGenerationFailure.SYSTEM_ERROR.provide(e);
        }
    }
}
