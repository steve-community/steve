/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
 * All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.rwth.idsg.steve.utils;

import de.rwth.idsg.steve.SteveConfiguration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Inspiration: http://stackoverflow.com/a/30817677
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 19.04.2016
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InternetChecker {

    private static final int CONNECT_TIMEOUT = 5_000;

    private static final List<String> HOST_LIST = Arrays.asList(
            "https://treibhaus.informatik.rwth-aachen.de/heartbeat/",
            "https://github.com",
            "https://www.wikipedia.org",
            "https://www.google.com",
            "https://www.apple.com",
            "https://www.facebook.com"
    );

    static {
        System.setProperty("http.agent", "SteVe/" + SteveConfiguration.CONFIG.getSteveCompositeVersion());
    }

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
            con.setRequestProperty("Connection", "close");  // otherwise, default setting is "keep-alive"
            try {
                con.setConnectTimeout(CONNECT_TIMEOUT);
                con.connect();
                if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
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
