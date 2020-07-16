package org.ibs.cds.gode.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import org.springframework.util.SocketUtils;

public class NetworkUtil {

    public static int nextFreePort() {
        return SocketUtils.findAvailableTcpPort();
    }

    public static String publicIP() throws IOException {
        URL ipPinger = new URL("http://checkip.amazonaws.com");
        BufferedReader in = new BufferedReader(new InputStreamReader(ipPinger.openStream()));
        String ip = in.readLine();
        in.close();
        return ip;
    }
}
