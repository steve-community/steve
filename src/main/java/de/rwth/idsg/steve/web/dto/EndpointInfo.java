/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
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

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @author Michael Heimpold <mhei@heimpold.de>
 * @since 06.08.2018
 */
@Getter
@ToString
@Builder
public class EndpointInfo {

    private final String ocppSoap;
    private final String ocppWebSocket;

    public static EndpointInfo fromRequest(String httpScheme, String host, String contextPath) {
        String webSocketScheme = httpScheme.equals("https") ? "wss" : "ws";

        return EndpointInfo.builder()
            .ocppSoap(httpScheme + "://" + host + contextPath + "/services/CentralSystemService")
            .ocppWebSocket(webSocketScheme + "://" + host + contextPath + "/websocket/CentralSystemService/(chargeBoxId)")
            .build();
    }
}
