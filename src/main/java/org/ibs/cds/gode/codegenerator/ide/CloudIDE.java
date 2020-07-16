package org.ibs.cds.gode.codegenerator.ide;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import org.ibs.cds.gode.codegenerator.exception.CodeGenerationFailure;
import org.ibs.cds.gode.codegenerator.model.util.SystemRunner;
import org.ibs.cds.gode.util.NetworkUtil;
import org.springframework.stereotype.Service;

/**
 *
 * @author manugraj
 */
@Service
public class CloudIDE {

    private final String publicIp;
    
    public CloudIDE() throws IOException{
        this.publicIp = "http://"+ NetworkUtil.publicIP() + ":";
    }
    
    public String runIDE(String path, String port) {
        nodeVersion();
        CompletableFuture.runAsync(() -> {
            try {
                File file = new File("ide.sh");
                System.out.println(SystemRunner.run(file.getAbsolutePath(), path, port));
            } catch (IOException | InterruptedException e) {
                throw CodeGenerationFailure.SYSTEM_ERROR.provide(e);
            }
        });
        return url(port);
    }

    private String url(String port){
        return publicIp.concat(port);
    }

    private static void nodeVersion() {
        try {
            String version = SystemRunner.run("node", "-v");
            if (!(version.startsWith("v11") || version.startsWith("11"))) {
                throw CodeGenerationFailure.SYSTEM_ERROR.provide("Node version should be 11, but currently it is ".concat(version));
            }
        } catch (IOException|InterruptedException ex) {
            throw CodeGenerationFailure.SYSTEM_ERROR.provide(ex);
        }
    }
}
