package org.ibs.cds.gode.codegenerator.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.ibs.cds.gode.codegenerator.api.usage.CodeGeneratorApi;
import org.ibs.cds.gode.codegenerator.entity.CodeApp;
import org.ibs.cds.gode.codegenerator.entity.CodeAppUtil;
import org.ibs.cds.gode.codegenerator.ide.CloudIDE;
import org.ibs.cds.gode.codegenerator.model.checkin.CheckInManager;
import org.ibs.cds.gode.codegenerator.model.checkin.CheckInModel;
import org.ibs.cds.gode.entity.manager.AppManager;
import org.ibs.cds.gode.entity.manager.BuildDataManager;
import org.ibs.cds.gode.entity.operation.Processor;
import org.ibs.cds.gode.entity.type.App;
import org.ibs.cds.gode.entity.type.BuildData;
import org.ibs.cds.gode.entity.type.Specification;
import org.ibs.cds.gode.util.Assert;
import org.ibs.cds.gode.util.NetworkUtil;
import org.ibs.cds.gode.web.Request;
import org.ibs.cds.gode.web.Response;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author manugraj
 */
@CodeGeneratorApi
@RequestMapping("/pipeline")
@Api(tags = {"Gode(e) Artifact Checkin endpoint"})
public class PipelineEndpoint {

    
    private AppManager appManager;
    private BuildDataManager buildDataManager;
    private CheckInManager checkInManager;
    private CloudIDE ide;

    public PipelineEndpoint(AppManager appManager, BuildDataManager buildDataManager, CheckInManager checkInManager, CloudIDE ide) {
        this.checkInManager = checkInManager;
        this.ide = ide;
        this.buildDataManager = buildDataManager;
        this.appManager = appManager;
    }

    @PostMapping(path = "/edit")
    @ApiOperation(value = "Operation to edit pipeline")
    public Response<String> edit(@RequestBody Request<Specification> pipelineRequest) {
        CodeApp app = getCodeApp(pipelineRequest.getData());
        String path = CodeAppUtil.pipelinePath(app);
        return Processor.successResponse(ide.runIDE(path, String.valueOf(NetworkUtil.nextFreePort())), pipelineRequest, "/edit");
    }

    @PostMapping(path = "/checkin")
    @ApiOperation(value = "Operation to checkin pipeline")
    public Response<Boolean> savePipeline(@RequestBody Request<CheckInModel> checkInPipelineRequest) {
        return Processor.successResponse(this.checkInManager.checkIn(getCodeApp(checkInPipelineRequest.getData().getApp()), checkInPipelineRequest.getData()), checkInPipelineRequest, "/checkin");
    }
    
    @NotNull
    public CodeApp getCodeApp(Specification model) {
        Assert.notNull("No app specification provided by the user",model);
        Long artifactId = model.getArtifactId();
        App foundApp = artifactId == null ? appManager.find(model.getName(), model.getVersion()) : appManager.find(artifactId);
        Assert.notNull("No app available for given name and version", foundApp);
        BuildData lastBuild = artifactId == null ? buildDataManager.findLatestBuild(foundApp.getName(), foundApp.getVersion()) : buildDataManager.findLatestBuild(artifactId);
        Assert.notNull("No build information available for given name and version", lastBuild);
        return new CodeApp(foundApp, lastBuild.toBuildModel());
    }

}
