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
package de.rwth.idsg.steve.ocpp.ws.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

/**
 * Default holder/context of incoming and outgoing messages.
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 23.03.2015
 */
@RequiredArgsConstructor
@Getter
public class CommunicationContext {

    private final WebSocketSession session;
    private final String chargeBoxId;

    @Setter private String incomingString;
    @Setter private String outgoingString;

    @Setter private OcppJsonMessage incomingMessage;
    @Setter private OcppJsonMessage outgoingMessage;

    /**
     * This is only relevant for requests CSMS sends.
     * During the outgoing pipeline, we create an instance of this and store it.
     * During the incoming pipeline (response from station to request), we restore and reference it.
     */
    @Setter private FutureResponseContext futureResponseContext;

    public boolean isSetOutgoingError() {
        return (outgoingMessage != null) && (outgoingMessage instanceof OcppJsonError);
    }
}
