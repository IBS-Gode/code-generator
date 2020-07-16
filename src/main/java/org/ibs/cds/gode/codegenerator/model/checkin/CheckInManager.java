package org.ibs.cds.gode.codegenerator.model.checkin;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.tuple.Pair;
import org.ibs.cds.gode.codegenerator.entity.CodeApp;
import org.ibs.cds.gode.codegenerator.entity.CodeAppUtil;
import org.ibs.cds.gode.deployer.git.LocalGit;
import org.ibs.cds.gode.deployer.git.RemoteGit;
import org.ibs.cds.gode.deployer.git.RemoteGitUrl;
import org.ibs.cds.gode.entity.type.App;
import org.ibs.cds.gode.exception.KnownException;
import org.ibs.cds.gode.util.Assert;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 *
 * @author manugraj
 */
@Service
public class CheckInManager {

    private String remoteRepo;
    private final String remoteRepoAuth;
    private RemoteGit remoteGit;
    private String remoteGitOrg;
    private String remoteRepoApi;

    public CheckInManager(Environment environment) {
        this.remoteRepo = environment.getProperty("gode.code.repo.url");
        this.remoteRepoApi = environment.getProperty("gode.code.repo.api.url");
        this.remoteRepoAuth = environment.getProperty("gode.code.repo.auth.token");
        this.remoteGit = new RemoteGit("gitea", remoteRepoApi);
        this.remoteGitOrg = environment.getProperty("gode.code.repo.org");
    }

    public Optional<String> remoteRepoExist(String repoName) {
        String url = this.remoteGit.execute(RemoteGit.RequestType.CHECK_REPO,
                Map.of(
                        "org", remoteGitOrg,
                        "auth", remoteRepoAuth,
                        "repo", repoName
                ),
                RemoteGit.ResponseType.CLONE_URL).getKey().get(RemoteGit.ResponseType.CLONE_URL);
        return Optional.ofNullable(url);
    }

    public String createRemoteRepo(String repoName, String description) {
        Pair<Map<RemoteGit.ResponseType, String>, Integer> result = this.remoteGit.execute(RemoteGit.RequestType.CREATE_REPO, Map.of(
                "org", remoteGitOrg,
                "auth", remoteRepoAuth,
                "repo", repoName,
                "description", description
        ),
                RemoteGit.ResponseType.CLONE_URL);
        int value = result.getValue();
        if (value < 199 || value > 299) {
            throw KnownException.HTTP_EXCEPTION.provide("Remote repo for git access cannot be created | Http Status:" + value);
        }
        String status = result.getKey().get(RemoteGit.ResponseType.CLONE_URL);
        return status;
    }

    public String initialise(App app) {
        return localRepo(app.getName().toLowerCase(), app.getDescription(), CodeAppUtil.appPath(app));
    }

    private String localRepo(String repoName, String description, String path) {
        String cloneUrl = this.remoteRepoExist(repoName).orElseGet(() -> createRemoteRepo(repoName, description));
        Assert.notNull("Git Url cannot be empty", cloneUrl);
        LocalGit git = LocalGit.at(repoName, new File(path), new RemoteGitUrl(cloneUrl, remoteRepoAuth));
        git.addRemote(cloneUrl);
        git.pullMaster();
        return cloneUrl;
    }

    public boolean checkIn(CodeApp app, CheckInModel checkInModel) {
        return checkIn(app.getName().toLowerCase(), String.valueOf(app.getVersion()), CodeAppUtil.appPath(app), checkInModel);
    }

    private boolean checkIn(String repoName, String version, String path, CheckInModel checkInModel) {
        String cloneUrl = this.remoteRepoExist(repoName).map(repoUrl -> repoUrl).orElseThrow();
        Assert.notNull("Git Url cannot be empty", cloneUrl);
        LocalGit git = LocalGit.at(repoName, new File(path), new RemoteGitUrl(cloneUrl, remoteRepoAuth));
        git.checkout(version);
        git.add(".");
        git.commit(checkInModel.getMessage(), checkInModel.getUsername(), checkInModel.getEmail());
        return git.push();
    }
}
