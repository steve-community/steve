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
package de.rwth.idsg.steve.ocpp.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 10.05.2018
 */
@Slf4j
@Component
public final class WebSocketLogger {

    public void connected(String chargeBoxId, WebSocketSession session) {
        log.info("[chargeBoxId={}, sessionId={}] Connection is established", chargeBoxId, session.getId());
    }

    public void closed(String chargeBoxId, WebSocketSession session, CloseStatus closeStatus) {
        log.warn(
                "[chargeBoxId={}, sessionId={}] Connection is closed, status: {}",
                chargeBoxId,
                session.getId(),
                closeStatus);
    }

    public void sending(String chargeBoxId, WebSocketSession session, String msg) {
        log.info("[chargeBoxId={}, sessionId={}] Sending: {}", chargeBoxId, session.getId(), msg);
    }

    public void sendingPing(String chargeBoxId, WebSocketSession session) {
        log.debug("[chargeBoxId={}, sessionId={}] Sending ping message", chargeBoxId, session.getId());
    }

    public void receivedPong(String chargeBoxId, WebSocketSession session) {
        log.debug("[chargeBoxId={}, sessionId={}] Received pong message", chargeBoxId, session.getId());
    }

    public void receivedText(String chargeBoxId, WebSocketSession session, String msg) {
        log.info("[chargeBoxId={}, sessionId={}] Received: {}", chargeBoxId, session.getId(), msg);
    }

    public void receivedEmptyText(String chargeBoxId, WebSocketSession session) {
        log.warn(
                "[chargeBoxId={}, sessionId={}] Received empty text message. Will pretend this never happened.",
                chargeBoxId,
                session.getId());
    }

    public void pingError(String chargeBoxId, WebSocketSession session, Throwable t) {
        log.error("[chargeBoxId={}, sessionId={}] Ping error", chargeBoxId, session.getId(), t);
    }

    public void transportError(String chargeBoxId, WebSocketSession session, Throwable t) {
        log.error("[chargeBoxId={}, sessionId={}] Transport error", chargeBoxId, session.getId(), t);
    }
}
