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
package de.rwth.idsg.steve.web.dto;

import de.rwth.idsg.steve.SteveConfiguration;
import lombok.Getter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 06.08.2018
 */
@Getter
@ToString
@Component
public class EndpointInfo {

    private final ItemsWithInfo webInterface;
    private final ItemsWithInfo ocppSoap;
    private final ItemsWithInfo ocppWebSocket;

    public EndpointInfo(SteveConfiguration config) {
        this.webInterface = new ItemsWithInfo(
                "Access the web interface using", config.getPaths().getManagerMapping() + "/home");
        this.ocppSoap = new ItemsWithInfo(
                "SOAP endpoint for OCPP",
                config.getPaths().getSoapMapping() + config.getPaths().getRouterEndpointPath());
        this.ocppWebSocket = new ItemsWithInfo(
                "WebSocket/JSON endpoint for OCPP",
                config.getPaths().getWebsocketMapping() + config.getPaths().getRouterEndpointPath() + "/(chargeBoxId)");
    }

    @Getter
    @ToString
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
            this.data = data.stream().map(s -> s + dataElementPostFix).toList();
        }
    }
}
