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
package de.rwth.idsg.steve.ocpp.ws;

import de.rwth.idsg.steve.ocpp.ws.data.SessionContext;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 08.02.2025
 */
public interface SessionContextStore {

    /**
     * @return whether this is the first session for this chargeBoxId.
     */
    boolean add(String chargeBoxId, WebSocketSession session);

    /**
     * @return whether this was the last session for this chargeBoxId.
     */
    boolean remove(String chargeBoxId, WebSocketSession session);

    WebSocketSession getSession(String chargeBoxId);

    Boolean registerIncomingCallId(String chargeBoxId, WebSocketSession session, String messageId);

    void closeSessions(String chargeBoxId);

    int getSize(String chargeBoxId);

    int getNumberOfChargeBoxes();

    List<String> getChargeBoxIdList();

    Map<String, Collection<SessionContext>> getReadOnlyMap();
}
