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
package de.rwth.idsg.steve.web.dto;

import lombok.Getter;

/**
 * @author Michael Heimpold <mhei@heimpold.de>
 */
@Getter
public final class WebEndpointInfo {
    private final ItemsWithInfo ocppSoap = new ItemsWithInfo("SOAP endpoint for OCPP", "/services/CentralSystemService");
    private final ItemsWithInfo ocppWebSocket = new ItemsWithInfo("WebSocket/JSON endpoint for OCPP", "/websocket/CentralSystemService/(chargeBoxId)");

    @Getter
    public static class ItemsWithInfo {
        private final String info;
        private final String path;
        private String url;

        private ItemsWithInfo(String info, String path) {
            this.info = info;
            this.path = path;
            this.url = "";
        }

        public synchronized void setUrlPrefix(String scheme, String serverName) {
            this.url = scheme + "://" + serverName + path;
        }
    }
}
