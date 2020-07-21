package org.ibs.cds.gode.codegenerator.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.ibs.cds.gode.codegenerator.api.usage.CodeGeneratorApi;
import org.ibs.cds.gode.codegenerator.entity.CodeApp;
import org.ibs.cds.gode.codegenerator.entity.CodeAppUtil;
import org.ibs.cds.gode.codegenerator.ide.CloudIDE;
import org.ibs.cds.gode.codegenerator.ide.Terminal;
import org.ibs.cds.gode.codegenerator.model.checkin.CheckInComplete;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@CodeGeneratorApi
@RequestMapping("/utility")
@Api(tags = {"Gode(e) Utility endpoints"})
public class CodeUtility {

    private AppManager appManager;
    private Terminal terminal;
    private CloudIDE ide;
    private BuildDataManager buildDataManager;
    private CheckInManager checkInManager;

    @Autowired
    public CodeUtility(AppManager appManager, BuildDataManager buildDataManager, CheckInManager checkInManager, Terminal terminal, CloudIDE ide) {
        this.terminal = terminal;
        this.ide = ide;
        this.appManager = appManager;
        this.buildDataManager = buildDataManager;
        this.checkInManager = checkInManager;
    }


    @PostMapping(path = "/terminal")
    @ApiOperation(value = "Operation to open terminal")
    public Response<String> terminal(@RequestBody Request<Specification> appRequest) {
        CodeApp codedApp = getCodeApp(appRequest.getData());
        return Processor.successResponse(terminal.start(codedApp), appRequest, "/terminal");
    }

    @PostMapping(path = "/ide")
    @ApiOperation(value = "Operation to open terminal")
    public Response<String> ide(@RequestBody Request<Specification> appRequest) {
        CodeApp codedApp = getCodeApp(appRequest.getData());
        String port = String.valueOf(NetworkUtil.nextFreePort());
        return Processor.successResponse(ide.runIDE(CodeAppUtil.customisableApp(codedApp), port), appRequest, "/ide");
    }

    @PostMapping(path = "/checkin")
    @ApiOperation(value = "Operation to checkin App")
    public Response<CheckInComplete> checkin(@RequestBody Request<CheckInModel> checkInModelRequest) {
        CheckInModel model = checkInModelRequest.getData();
        Specification app = model.getApp();
        CodeApp codedApp = getCodeApp(app);
        return Processor.successResponse(checkInManager.checkIn(codedApp, model), checkInModelRequest, "/checkin");
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
