package de.rwth.idsg.steve.utils;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

/**
 * Inspiration: http://stackoverflow.com/a/30817677
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 19.04.2016
 */
public final class InternetChecker {
    private InternetChecker() { }

    private static final int PORT = 80;

    private static final int CONNECT_TIMEOUT = 5_000;

    private static final List<String> HOST_LIST = Arrays.asList(
            "github.com",
            "google.com",
            "facebook.com",
            "amazon.com",
            "apple.com"
    );

    /**
     * We try every item in the list to compensate for the possibility that one of hosts might be down. If all these
     * big players are down at the same time, that's okay too, because the end of the world must have arrived,
     * obviously.
     */
    public static boolean isInternetAvailable() {
        for (String s : HOST_LIST) {
            if (isHostAvailable(s)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isHostAvailable(String host) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, PORT), CONNECT_TIMEOUT);
            if (socket.isConnected()) {
                return true;
            }
        } catch (Exception e) {
            // No-op
        }

        return false;
    }
}
