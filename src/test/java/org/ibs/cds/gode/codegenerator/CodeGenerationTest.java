package org.ibs.cds.gode.codegenerator;

import org.ibs.cds.gode.codegenerator.bind.ArtifactPackaging;
import org.ibs.cds.gode.codegenerator.entity.AppCodeGenerator;
import org.ibs.cds.gode.codegenerator.model.build.BuildModel;
import org.ibs.cds.gode.codegenerator.spec.Level;
import org.ibs.cds.gode.codegenerator.spec.ProgLanguage;
import org.ibs.cds.gode.codegenerator.spec.StoreName;
import org.ibs.cds.gode.entity.generic.AB;
import org.ibs.cds.gode.entity.relationship.RelationshipType;
import org.ibs.cds.gode.entity.type.*;
import org.ibs.cds.gode.util.RandomUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

public class CodeGenerationTest {

    @Test
    public void basicTest(){
        Assert.assertTrue("Code generation failed",testGenerate(app()));
    }


    public static void main(String[] args) {
        new CodeGenerationTest().basicTest();
    }
    private static boolean testGenerate(AB<App, List<RelationshipEntitySpec>> app ) {

        List<RelationshipStorePolicy> policies = app.getB().stream().map(k->{
            RelationshipStorePolicy policy = new RelationshipStorePolicy();
            policy.setStoreName(StoreName.MYSQL);
            policy.setRelationship(k);
            policy.setAdditionalFields(List.of());
            return policy;
        }).collect(Collectors.toList());


        BuildModel model = new BuildModel();
        model.setProgLanguage(ProgLanguage.JAVA);
        model.setArtifactPackaging(ArtifactPackaging.MAVEN);
        model.setApp(app.getA());
        model.setSecure(false);
        model.setPipelineGeneration(true);
        model.setRelationshipStorePolicy(policies);

        AppCodeGenerator appCodeGenerator = new AppCodeGenerator(app.getA(), model);
        return appCodeGenerator.generate();
    }

    private static AB<App, List<RelationshipEntitySpec>> app() {
        EntityField field = new EntityField();
        field.setName("name");
        field.setType(FieldType.TEXT);

        EntityField of = new EntityField();
        of.setName("test1");
        of.setType(FieldType.TEXT);

        EntityField of3 = new EntityField();
        of3.setName("test3");
        of3.setType(FieldType.TEXT);

        EntityField of2 = new EntityField();
        of2.setName("test2");
        of2.setType(FieldType.TEXT);

        EntityField obj = new EntityField();
        obj.setType(FieldType.OBJECT);
        obj.setName("object1");
        ObjectType type = new ObjectType();
        type.setName("ASample");
        type.setFields(List.of(of));
        obj.setObjectType(type);

        EntityField obj2 = new EntityField();
        obj2.setType(FieldType.OBJECT);
        obj2.setName("object2");
        ObjectType type2 = new ObjectType();
        type2.setName("BSample");
        type2.setFields(List.of(of2));
        obj2.setObjectType(type2);

        IdField idField = new IdField();
        idField.setName("eid");
        idField.setType(FieldType.TEXT);

        EntityState state = new EntityState();
        state.setOpsLevel(new OperationLevel(Level.HIGH, Level.MEDIUM, Level.LOW, false, false));
        EntityStateStore test = new EntityStateStore();
        test.setStoreName(StoreName.MYSQL);
        state.setEntityStateStore(test);

        EntityState state2 = new EntityState();
        state2.setOpsLevel(new OperationLevel(Level.HIGH, Level.MEDIUM, Level.LOW, false, false));
        EntityStateStore test2 = new EntityStateStore();
        test2.setStoreName(StoreName.MONGODB);
        test2.setAsyncStoreSync(true);
        test2.setCached(true);
        state2.setEntityStateStore(test2);

        EntityState state3 = new EntityState();
        state3.setVolatileEntity(true);

        StatefulEntitySpec statefulEntitySpec = new StatefulEntitySpec();
        statefulEntitySpec.setName("Entity1");
        statefulEntitySpec.setDescription("Entity 1 descr");
        statefulEntitySpec.setVersion(2L);
        statefulEntitySpec.setFields(List.of(field, obj, obj2));
        statefulEntitySpec.setIdField(idField);
        statefulEntitySpec.setState(state);

        StatefulEntitySpec statefulEntitySpec2 = new StatefulEntitySpec();
        statefulEntitySpec2.setName("Entity2");
        statefulEntitySpec2.setDescription("Entity 2 descr");
        statefulEntitySpec2.setVersion(3L);
        statefulEntitySpec2.setFields(List.of(field));
        statefulEntitySpec2.setIdField(idField);
        statefulEntitySpec2.setState(state2);

        StatefulEntitySpec statefulEntitySpec3 = new StatefulEntitySpec();
        statefulEntitySpec3.setName("Entity3");
        statefulEntitySpec3.setDescription("Entity 3 descr");
        statefulEntitySpec3.setVersion(4L);
        statefulEntitySpec3.setFields(List.of(field));
        statefulEntitySpec3.setIdField(idField);
        statefulEntitySpec3.setState(state3);

        EntityState state4 = new EntityState();
        state4.setOpsLevel(new OperationLevel(Level.HIGH, Level.MEDIUM, Level.LOW, false, false));
        EntityStateStore test3 = new EntityStateStore();
        test3.setStoreName(StoreName.CASSANDRA);
        test3.setAsyncStoreSync(true);
        test3.setCached(true);
        state4.setEntityStateStore(test3);

        EntityState state5 = new EntityState();
        state5.setOpsLevel(new OperationLevel(Level.HIGH, Level.MEDIUM, Level.LOW, false, false));
        EntityStateStore test5 = new EntityStateStore();
        test5.setStoreName(StoreName.ELASTICSEARCH);
        test5.setAsyncStoreSync(true);
        test5.setCached(true);
        state5.setEntityStateStore(test5);

        StatefulEntitySpec statefulEntitySpec4 = new StatefulEntitySpec();
        statefulEntitySpec4.setName("Entity4");
        statefulEntitySpec4.setDescription("Entity 4 descr");
        statefulEntitySpec4.setVersion(5L);
        statefulEntitySpec4.setFields(List.of(field));
        statefulEntitySpec4.setIdField(idField);
        statefulEntitySpec4.setState(state4);

        StatefulEntitySpec statefulEntitySpec5 = new StatefulEntitySpec();
        statefulEntitySpec5.setName("Entity5");
        statefulEntitySpec5.setDescription("Entity 5 descr");
        statefulEntitySpec5.setVersion(7L);
        statefulEntitySpec5.setFields(List.of(field));
        statefulEntitySpec5.setIdField(idField);
        statefulEntitySpec5.setState(state5);

        RelationshipEntitySpec relationshipEntitySpec = new RelationshipEntitySpec();
        relationshipEntitySpec.setArtifactId(RandomUtils.unique());
        relationshipEntitySpec.setName("Entity1Entity2Relationship");
        relationshipEntitySpec.setType(RelationshipType.ONE_TO_MANY);
        RelationshipNode startNode = new RelationshipNode();
        startNode.setEntity(statefulEntitySpec);
        startNode.setRole("parent");
        RelationshipNode endNode = new RelationshipNode();
        endNode.setEntity(statefulEntitySpec2);
        endNode.setRole("child");
        relationshipEntitySpec.setStartNode(startNode);
        relationshipEntitySpec.setEndNode(endNode);
        relationshipEntitySpec.setFields(List.of(of));
        relationshipEntitySpec.setVersion(8L);

        RelationshipEntitySpec relationshipEntitySpec2 = new RelationshipEntitySpec();
        relationshipEntitySpec2.setArtifactId(RandomUtils.unique());
        relationshipEntitySpec2.setName("EntityToRelatnRelationship");
        relationshipEntitySpec2.setType(RelationshipType.ONE_TO_MANY);
        RelationshipNode startNode2 = new RelationshipNode();
        startNode2.setEntity(statefulEntitySpec);
        startNode2.setRole("entitySide");
        RelationshipNode endNode2 = new RelationshipNode();
        endNode2.setEntity(relationshipEntitySpec);
        endNode2.setRole("relatnSide");
        relationshipEntitySpec2.setStartNode(startNode2);
        relationshipEntitySpec2.setEndNode(endNode2);
        relationshipEntitySpec2.setFields(List.of(of));
        relationshipEntitySpec2.setVersion(9L);

        AppFunction function = new AppFunction();
        function.setMethodName("method1");
        function.setInput(List.of(new AppFuncArgument(statefulEntitySpec, "arg1")));
        function.setOutput(List.of(
                new AppFuncArgument(statefulEntitySpec, "arg1"),
                new AppFuncArgument(statefulEntitySpec2, "arg2")
        ));

        AppFunction function2 = new AppFunction();
        function2.setMethodName("method2");
        function2.setInput(List.of(new AppFuncArgument(statefulEntitySpec, "arg1")));
        function2.setOutput(List.of(
                new AppFuncArgument(statefulEntitySpec, "arg1"),
                new AppFuncArgument(statefulEntitySpec2, "arg2")
        ));

        DataPipeline dataPipeline = new DataPipeline();
        Pipeline pipeline = new Pipeline();
        pipeline.setName("TestPipeline");

        PipelineSource source = new PipelineSource();
        source.setEntity("Entity2");
        source.setType(PipelineSourceType.SUPPLIER);

        PipelineNode node1 = new PipelineNode();
        node1.setMapTo("Entity4");
        node1.setName("E2ToE4");

        PipelineSink sink = new PipelineSink();
        sink.setEntity("Entity1");
        sink.setName("Entity1Sink");

        PipelineNode node2 = new PipelineNode();
        node2.setMapTo("Entity1");
        node2.setName("E4ToE1");
        node2.setSink(sink);

        node1.setNext(node2);

        source.setNext(node1);

        pipeline.setSource(source);
        dataPipeline.setPipelines(List.of(pipeline));

        App app = new App();
        app.setDatapipeline(dataPipeline);
        app.setName("App1");
        app.setDescription("App1 description");
        app.setVersion(10L);
        app.setEntities(List.of(statefulEntitySpec, statefulEntitySpec2, statefulEntitySpec3, statefulEntitySpec4, statefulEntitySpec5));
        app.setFunctions(List.of(function, function2));
        List<RelationshipEntitySpec> relationshipEntitySpecs = List.of(relationshipEntitySpec, relationshipEntitySpec2);
        app.setRelationships(relationshipEntitySpecs);
        return AB.of(app, relationshipEntitySpecs);
    }
}
