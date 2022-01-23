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

/**
 * ApplicationStarter for DEV profile
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 05.11.2015
 */
public class SteveDevStarter implements ApplicationStarter {

    private final JettyServer jettyServer;

    SteveDevStarter() {
        this.jettyServer = new JettyServer();
    }

    @Override
    public void start() throws Exception {
        jettyServer.start();
    }

    @Override
    public void stop() throws Exception {
        jettyServer.stop();
    }

    @Override
    public void join() throws Exception {
        jettyServer.join();
    }
}
