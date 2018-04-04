package de.rwth.idsg.steve.utils;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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

    private static final int CONNECT_TIMEOUT = 5_000;

    private static final List<String> HOST_LIST = Arrays.asList(
            "https://github.com",
            "https://www.wikipedia.org",
            "https://www.google.com",
            "https://www.apple.com",
            "https://www.facebook.com"
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

    private static boolean isHostAvailable(String str) {
        try {
            URL url = new URL(str);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            try {
                con.setConnectTimeout(CONNECT_TIMEOUT);
                con.connect();
                if (con.getResponseCode() == 200) {
                    return true;
                }
            } finally {
                con.disconnect();
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            // No-op
        }

        return false;
    }
}
