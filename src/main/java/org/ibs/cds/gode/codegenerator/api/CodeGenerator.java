package org.ibs.cds.gode.codegenerator.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.ibs.cds.gode.codegenerator.api.usage.CodeGeneratorApi;
import org.ibs.cds.gode.codegenerator.entity.CodeApp;
import org.ibs.cds.gode.codegenerator.entity.StorePolicy;
import org.ibs.cds.gode.codegenerator.model.build.BuildComplete;
import org.ibs.cds.gode.codegenerator.model.build.BuildModel;
import org.ibs.cds.gode.codegenerator.model.build.Builder;
import org.ibs.cds.gode.codegenerator.model.checkin.CheckInComplete;
import org.ibs.cds.gode.codegenerator.model.deploy.*;
import org.ibs.cds.gode.codegenerator.spec.StoreName;
import org.ibs.cds.gode.entity.generic.DataMap;
import org.ibs.cds.gode.entity.manager.AppManager;
import org.ibs.cds.gode.entity.manager.BuildDataManager;
import org.ibs.cds.gode.entity.operation.Executor;
import org.ibs.cds.gode.entity.operation.Logic;
import org.ibs.cds.gode.entity.operation.Processor;
import org.ibs.cds.gode.entity.store.StoreType;
import org.ibs.cds.gode.entity.type.App;
import org.ibs.cds.gode.entity.type.BuildData;
import org.ibs.cds.gode.entity.type.Specification;
import org.ibs.cds.gode.exception.KnownException;
import org.ibs.cds.gode.web.Request;
import org.ibs.cds.gode.web.Response;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import org.h2.store.fs.FileUtils;
import org.ibs.cds.gode.codegenerator.entity.CodeAppUtil;
import org.ibs.cds.gode.codegenerator.model.checkin.CheckInManager;
import org.ibs.cds.gode.codegenerator.model.checkin.CheckInModel;
import org.ibs.cds.gode.util.Assert;

@CodeGeneratorApi
@RequestMapping("/generator")
@Api(tags = {"Gode(e) build endpoints"})
public class CodeGenerator {

    private AppManager appManager;

    private BuildDataManager buildDataManager;

    private CheckInManager checkInManager;

    private Builder builder;

    @Autowired
    public CodeGenerator(AppManager appManager, BuildDataManager buildDataManager, Builder builder, CheckInManager checkInManager) {
        this.appManager = appManager;
        this.buildDataManager = buildDataManager;
        this.builder = builder;
        this.checkInManager = checkInManager;
    }

    @PostMapping(path = "/design")
    @ApiOperation(value = "Operation to design App")
    public Response<App> design(@RequestBody Request<App> appRequest) {
        App app = appRequest.getData();
        FileUtils.deleteRecursive(CodeAppUtil.appPath(app), true);
        app.setCodeUrl(checkInManager.initialise(app));
        return Executor.run(Logic.savePure(), appRequest, appManager, KnownException.SAVE_FAILED, "/design");
    }

    @PostMapping(path = "/build")
    @ApiOperation(value = "Operation to build App")
    public Response<BuildComplete> build(@RequestBody Request<BuildModel> buildModelRequest) {
        BuildModel data = buildModelRequest.getData();
        Assert.notNull("Build information is mandatory", data);
        Specification app = data.getApp();
        App foundApp = app.getArtifactId() == null ? appManager.find(app.getName(), app.getVersion()) : appManager.find(app.getArtifactId());
        Assert.notNull("No app information is available", foundApp);
        return Processor.successResponse(builder.build(data, foundApp), buildModelRequest, "/build");
    }

    @PostMapping(path = "/checkin")
    @ApiOperation(value = "Operation to checkin App")
    public Response<CheckInComplete> checkin(@RequestBody Request<CheckInModel> checkInModelRequest) {
        CheckInModel model = checkInModelRequest.getData();
        Specification app = model.getApp();
        CodeApp codedApp = getCodeApp(app);
        return Processor.successResponse(checkInManager.checkIn(codedApp, model), checkInModelRequest, "/checkin");
    }

    @PostMapping(path = "/deploy")
    @ApiOperation(value = "Operation to deploy App")
    public Response<DeploymentComplete> deploy(@RequestBody Request<DeploymentModel> deploymentModelRequest) {
        DeploymentModel model = deploymentModelRequest.getData();
        CodeApp app = getCodeApp(deploymentModelRequest.getData().getApp());
        return Processor.successResponse(Deployer.doDeployment(model, app), deploymentModelRequest, "/deploy");
    }

    @PostMapping(path = "/deploy/requirement")
    @ApiOperation(value = "Operation to deployment properties for deploying App")
    public Map<String, String> deployRequirements(@RequestBody Request<DeploymentModel> deploymentModelRequest) {
        DeploymentModel model = deploymentModelRequest.getData();
        CodeApp app = getCodeApp(deploymentModelRequest.getData().getApp());
        switch (model.getType()) {
            case LOCAL:
                return LocalDeploymentRequirement.values(app);
        }
        return Collections.emptyMap();
    }

    @PostMapping(path = "/deploy/store")
    @ApiOperation(value = "Operation to get store requirements for deploying App")
    public DataMap deploymentRequirements(@RequestBody Request<Specification> appData) {
        DataMap map = new DataMap();
        Specification model = appData.getData();
        CodeApp codeApp = getCodeApp(model);
        Map<StoreType, Set<StorePolicy>> req = DeploymentRequirement.getStoreRequirements(codeApp);
        List<StoreName> storeNames = req.entrySet().stream().flatMap(k -> k.getValue().stream().map(d -> d.getStoreName())).collect(Collectors.toUnmodifiableList());
        map.put("stores", new ArrayList(storeNames));
        map.put("cacheRequired", DeploymentRequirement.isCacheNeeded(req));
        return map;
    }

    @NotNull
    public CodeApp getCodeApp(Specification model) {
        Long artifactId = model.getArtifactId();
        App foundApp = artifactId == null ? appManager.find(model.getName(), model.getVersion()) : appManager.find(artifactId);
        Assert.notNull("No app available for given name and version", foundApp);
        BuildData lastBuild = artifactId == null ? buildDataManager.findLatestBuild(foundApp.getName(), foundApp.getVersion()) : buildDataManager.findLatestBuild(artifactId);
        Assert.notNull("No build information available for given name and version", lastBuild);
        return new CodeApp(foundApp, lastBuild.toBuildModel());
    }
}
