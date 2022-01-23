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
package de.rwth.idsg.steve;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.TimeZone;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 14.01.2015
 */
@Slf4j
public class Application implements ApplicationStarter, AutoCloseable {

    private final ApplicationStarter delegate;

    public Application() {
        // For Hibernate validator
        System.setProperty("org.jboss.logging.provider", "slf4j");

        SteveConfiguration sc = SteveConfiguration.CONFIG;
        log.info("Loaded the properties. Starting with the '{}' profile", sc.getProfile());

        TimeZone.setDefault(TimeZone.getTimeZone(sc.getTimeZoneId()));
        DateTimeZone.setDefault(DateTimeZone.forID(sc.getTimeZoneId()));
        log.info("Date/time zone of the application is set to {}. Current date/time: {}", sc.getTimeZoneId(), DateTime.now());

        switch (sc.getProfile()) {
            case DEV:
                delegate = new SteveDevStarter();
                break;
            case TEST:
            case PROD:
                delegate = new SteveProdStarter();
                break;
            default:
                throw new RuntimeException("Unexpected profile");
        }
    }

    public static void main(String[] args) throws Exception {
        Application app = null;
        try {
            app = new Application();
            app.start();
            app.join();
        } catch (Exception e) {
            if (app != null) {
                app.stop();
            }
        }
    }

    @Override
    public void start() throws Exception {
        delegate.start();
    }

    @Override
    public void join() throws Exception {
        delegate.join();
    }

    @Override
    public void stop() throws Exception {
        delegate.stop();
    }

    @Override
    public void close() throws Exception {
        stop();
    }
}
