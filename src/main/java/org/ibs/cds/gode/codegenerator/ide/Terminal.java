package org.ibs.cds.gode.codegenerator.ide;

import lombok.extern.slf4j.Slf4j;
import org.ibs.cds.gode.codegenerator.entity.CodeApp;
import org.ibs.cds.gode.codegenerator.entity.CodeAppUtil;
import org.ibs.cds.gode.codegenerator.model.util.SystemRunner;
import org.ibs.cds.gode.entity.generic.AB;
import org.ibs.cds.gode.entity.generic.Try;
import org.ibs.cds.gode.exception.KnownException;
import org.ibs.cds.gode.util.NetworkUtil;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class Terminal {

    public String start(String path) {
        String port = String.valueOf(NetworkUtil.nextFreePort());
        log.info("Starting terminal @{} for{}", port, path);
        CompletableFuture.runAsync(() -> {
            Try
                    .code(
                            (AB<String, String> pathPort) -> {
                                File file = new File("terminal.sh");
                                System.out.println(SystemRunner.run(file.getAbsolutePath(), pathPort.getA(), pathPort.getB()));
                            }
                    )
                    .catchWith(KnownException.SYSTEM_FAILURE)
                    .run(AB.of(port, path));
        });
        return "http://localhost:" + port;
    }

    public String start(CodeApp app) {
        return start(CodeAppUtil.appPath(app));
    }

}
