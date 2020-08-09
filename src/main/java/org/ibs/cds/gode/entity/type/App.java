package org.ibs.cds.gode.entity.type;

import lombok.Data;
import org.ibs.cds.gode.codegenerator.spec.UsageSpec;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class App extends Specification {
    private @Embedded UsageSpec usage;
    @ManyToMany
    @JoinTable(name = "AppEntities")
    private List<StatefulEntitySpec> entities;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "AppFunctions")
    private List<AppFunction> functions;
    @ManyToMany
    @JoinTable(name = "AppDependencies")
    private List<StatefulEntitySpec> dependencies;
    @ManyToMany
    @JoinTable(name = "AppRelationships")
    private List<RelationshipEntitySpec> relationships;
    @OneToOne
    @JoinTable(name = "AppDataPipeline")
    private DataPipelineConf dataPipeline;

    private String codeUrl;
    public App() {
       this.entities = new ArrayList<>();
       this.functions = new ArrayList<>();
       this.dependencies = new ArrayList<>();
    }
}
