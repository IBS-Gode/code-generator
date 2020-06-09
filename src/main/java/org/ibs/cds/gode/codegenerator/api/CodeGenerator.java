package org.ibs.cds.gode.codegenerator.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.ibs.cds.gode.codegenerator.entity.AppCodeGenerator;
import org.ibs.cds.gode.codegenerator.entity.CodeApp;
import org.ibs.cds.gode.codegenerator.entity.StorePolicy;
import org.ibs.cds.gode.codegenerator.model.deploy.DeploymentRequirement;
import org.ibs.cds.gode.codegenerator.model.deploy.LocalDeployment;
import org.ibs.cds.gode.codegenerator.spec.StoreName;
import org.ibs.cds.gode.entity.generic.DataMap;
import org.ibs.cds.gode.entity.manager.AppManager;
import org.ibs.cds.gode.entity.manager.BuildDataManager;
import org.ibs.cds.gode.entity.store.StoreType;
import org.ibs.cds.gode.entity.type.App;
import org.ibs.cds.gode.codegenerator.model.build.BuildModel;
import org.ibs.cds.gode.codegenerator.model.deploy.DeploymentModel;
import org.ibs.cds.gode.entity.type.BuildData;
import org.ibs.cds.gode.entity.type.Specification;
import org.ibs.cds.gode.entity.operation.Executor;
import org.ibs.cds.gode.entity.operation.Logic;
import org.ibs.cds.gode.exception.KnownException;
import org.ibs.cds.gode.web.Request;
import org.ibs.cds.gode.web.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/generator")
@Api(tags={"Gode(e) build endpoints"})
public class CodeGenerator {

    @Autowired
    private AppManager appManager;

    @Autowired
    private BuildDataManager buildDataManager;

    @PostMapping(path="/design")
    @ApiOperation(value = "Operation to design App")
    public Response<App> design(@RequestBody Request<App> appRequest){
        return Executor.run(Logic.save(), appRequest, appManager,KnownException.SAVE_FAILED, "/design");
    }

    @PostMapping(path="/build")
    @ApiOperation(value = "Operation to build App")
    public boolean build(@RequestBody Request<BuildModel> buildModelRequest){
        BuildModel data = buildModelRequest.getData();
        Specification app = data.getApp();
        App foundApp = appManager.find(app.getName(), app.getVersion());
        if(foundApp == null) throw KnownException.OBJECT_NOT_FOUND.provide("No app available for given name and version");
        return build(data, foundApp);
    }

    public boolean build(BuildModel data, App foundApp) {
        AppCodeGenerator appCodeGenerator = new AppCodeGenerator(foundApp, data);
        boolean generate = appCodeGenerator.generate();
        if(generate){
            buildDataManager.save(BuildData.fromModel(data, foundApp));
        }
        return generate;
    }

    @PostMapping(path="/deploy")
    @ApiOperation(value = "Operation to deploy App")
    public boolean deploy(@RequestBody Request<DeploymentModel> deploymentModelRequest){
        DeploymentModel model = deploymentModelRequest.getData();
        LocalDeployment deployment = model.getLocalDeployment();

        return true;
    }

    @PostMapping(path="/deploy/requirement")
    @ApiOperation(value = "Operation to get requirements for deploying App")
    public DataMap deploymentRequirements(@RequestBody Request<Specification> appData){
        DataMap map = new DataMap();
        Specification model = appData.getData();
        App foundApp = appManager.find(model.getName(), model.getVersion());
        if(foundApp == null) throw KnownException.OBJECT_NOT_FOUND.provide("No app available for given name and version");
        BuildData lastBuild = buildDataManager.findLatestBuild(foundApp.getName(), foundApp.getVersion());
        if(lastBuild == null) throw KnownException.OBJECT_NOT_FOUND.provide("No build information available for given name and version");
        CodeApp codeApp = new CodeApp(foundApp , lastBuild.toBuildModel());
        Map<StoreType, Set<StorePolicy>> req = DeploymentRequirement.getStoreRequirements(codeApp);
        List<StoreName> storeNames = req.entrySet().stream().flatMap(k -> k.getValue().stream().map(d -> d.getStoreName())).collect(Collectors.toUnmodifiableList());
        map.put("stores", new ArrayList(storeNames));
        map.put("cacheRequired",DeploymentRequirement.isCacheNeeded(req));
        return map;
    }
}
