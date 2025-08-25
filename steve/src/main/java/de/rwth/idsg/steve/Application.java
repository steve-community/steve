/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2025 SteVe Community Team
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
package de.rwth.idsg.steve;

import de.rwth.idsg.steve.utils.LogFileRetriever;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.TimeZone;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 14.01.2015
 */
@Slf4j
public class Application {

    private final JettyServer server;

    public Application(SteveConfiguration config) {
        server = new JettyServer(config);
    }

    public static void main(String[] args) throws Exception {
        // For Hibernate validator
        System.setProperty("org.jboss.logging.provider", "slf4j");

        SteveConfiguration sc = SteveConfigurationReader.readSteveConfiguration("main.properties");
        log.info("Loaded the properties. Starting with the '{}' profile", sc.getProfile());

        var zoneId = ZoneId.of(sc.getTimeZoneId());
        TimeZone.setDefault(TimeZone.getTimeZone(zoneId));
        log.info("Date/time zone of the application is set to {}. Current date/time: {}",
                sc.getTimeZoneId(), ZonedDateTime.now(zoneId));

        Optional<Path> path = LogFileRetriever.INSTANCE.getPath();
        boolean loggingToFile = path.isPresent();
        if (loggingToFile) {
            System.out.println("Log file: " + path.get().toAbsolutePath());
        }

        Application app = new Application(sc);

        try {
            app.start();
            app.join();
        } catch (Exception e) {
            log.error("Application failed to start", e);

            if (loggingToFile) {
                System.err.println("Application failed to start");
                e.printStackTrace();
            }

            app.stop();
        }
    }

    public void start() throws Exception {
        server.start();
    }

    public void join() throws Exception {
        server.join();
    }

    public void stop() throws Exception {
        server.stop();
    }
}
