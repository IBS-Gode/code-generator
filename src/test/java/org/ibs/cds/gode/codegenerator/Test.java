package org.ibs.cds.gode.codegenerator;

import java.io.IOException;
import org.ibs.cds.gode.codegenerator.bind.ArtifactPackaging;
import org.ibs.cds.gode.codegenerator.entity.AppCodeGenerator;
import org.ibs.cds.gode.codegenerator.model.build.BuildModel;
import org.ibs.cds.gode.codegenerator.spec.Level;
import org.ibs.cds.gode.codegenerator.spec.ProgLanguage;
import org.ibs.cds.gode.codegenerator.spec.StoreName;
import org.ibs.cds.gode.entity.relationship.RelationshipType;
import org.ibs.cds.gode.entity.type.*;

import java.util.List;
import org.ibs.cds.gode.entity.generic.AB;

public class Test {

    public static void main(String[] args) throws IOException {
//        CheckInManager checkInManager = new CheckInManager("http://localhost:3000","b23c7dd0a8e07d3e32f2adbf706ecef4472e3c2b", "ibs", "http://localhost:3000/api/v1");
//        App app = app().getA();
//        String path = "/Users/a-9023/Documents/work/code/code-generator/releases/5/app1-container";
////        System.out.println(checkInManager.createRemoteRepo("test4", "s"));
//        checkInManager.localRepo(app.getName().toLowerCase(), app.getDescription(), path);
        testGenerate(app());
//        checkInManager.checkIn(app.getName().toLowerCase(), path, new CheckInModel("manugraj", "grajmanu@gmail.com", "Test" ,"upgrade4"));
    }

    private static void testGenerate(AB<App, RelationshipEntitySpec> app ) {

        RelationshipStorePolicy policy = new RelationshipStorePolicy();
        policy.setStoreName(StoreName.MYSQL);
        policy.setRelationship(app.getB());
        policy.setAdditionalFields(List.of());

        BuildModel model = new BuildModel();
        model.setProgLanguage(ProgLanguage.JAVA);
        model.setArtifactPackaging(ArtifactPackaging.MAVEN);
        model.setApp(app.getA());
        model.setSecure(false);
        model.setPipelineGeneration(true);
        model.setRelationshipStorePolicy(List.of(policy));

        AppCodeGenerator appCodeGenerator = new AppCodeGenerator(app.getA(), model);
        appCodeGenerator.generate();
    }

    private static AB<App, RelationshipEntitySpec> app() {
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

        StatefulEntitySpec statefulEntitySpec4 = new StatefulEntitySpec();
        statefulEntitySpec4.setName("Entity4");
        statefulEntitySpec4.setDescription("Entity 4 descr");
        statefulEntitySpec4.setVersion(5L);
        statefulEntitySpec4.setFields(List.of(field));
        statefulEntitySpec4.setIdField(idField);
        statefulEntitySpec4.setState(state4);

        RelationshipEntitySpec relationshipEntitySpec = new RelationshipEntitySpec();
        relationshipEntitySpec.setName("ParentCustomerRelationship");
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

        App app = new App();
        app.setName("App1");
        app.setDescription("App1 description");
        app.setVersion(6L);
        app.setEntities(List.of(statefulEntitySpec, statefulEntitySpec2, statefulEntitySpec3, statefulEntitySpec4));
        app.setFunctions(List.of(function, function2));
        app.setRelationships(List.of(relationshipEntitySpec));
        return AB.of(app, relationshipEntitySpec);
    }
}
