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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 06.08.2018
 */
@Getter
public enum EndpointInfo {
    INSTANCE;

    private final ItemsWithInfo webInterface = new ItemsWithInfo("Access the web interface using", "/manager/home");
    private final ItemsWithInfo ocppSoap = new ItemsWithInfo("SOAP endpoint for OCPP", "/services/CentralSystemService");
    private final ItemsWithInfo ocppWebSocket = new ItemsWithInfo("WebSocket/JSON endpoint for OCPP", "/websocket/CentralSystemService/(chargeBoxId)");

    @Getter
    public static class ItemsWithInfo {
        private final String info;
        private final String dataElementPostFix;
        private List<String> data;

        private ItemsWithInfo(String info, String dataElementPostFix) {
            this.info = info;
            this.dataElementPostFix = dataElementPostFix;
            this.data = Collections.emptyList();
        }

        public synchronized void setData(List<String> data) {
            this.data = data.stream()
                            .map(s -> s + dataElementPostFix)
                            .collect(Collectors.toList());
        }
    }
}
