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
package de.rwth.idsg.steve.ocpp.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 10.05.2018
 */
@Slf4j
public final class WebSocketLogger {

    private WebSocketLogger() { }

    public static void connected(String chargeBoxId, WebSocketSession session) {
        log.info("[chargeBoxId={}, sessionId={}] Connection is established", chargeBoxId, session.getId());
    }

    public static void closed(String chargeBoxId, WebSocketSession session, CloseStatus closeStatus) {
        log.warn("[chargeBoxId={}, sessionId={}] Connection is closed, status: {}", chargeBoxId, session.getId(), closeStatus);
    }

    public static void sending(String chargeBoxId, WebSocketSession session, String msg) {
        log.info("[chargeBoxId={}, sessionId={}] Sending: {}", chargeBoxId, session.getId(), msg);
    }

    public static void sendingPing(String chargeBoxId, WebSocketSession session) {
        log.debug("[chargeBoxId={}, sessionId={}] Sending ping message", chargeBoxId, session.getId());
    }

    public static void receivedPong(String chargeBoxId, WebSocketSession session) {
        log.debug("[chargeBoxId={}, sessionId={}] Received pong message", chargeBoxId, session.getId());
    }

    public static void receivedText(String chargeBoxId, WebSocketSession session, String msg) {
        log.info("[chargeBoxId={}, sessionId={}] Received: {}", chargeBoxId, session.getId(), msg);
    }

    public static void receivedEmptyText(String chargeBoxId, WebSocketSession session) {
        log.warn("[chargeBoxId={}, sessionId={}] Received empty text message. Will pretend this never happened.", chargeBoxId, session.getId());
    }

    public static void pingError(String chargeBoxId, WebSocketSession session, Throwable t) {
        if (log.isErrorEnabled()) {
            log.error("[chargeBoxId=" + chargeBoxId + ", sessionId=" + session.getId() + "] Ping error", t);
        }
    }

    public static void transportError(String chargeBoxId, WebSocketSession session, Throwable t) {
        if (log.isErrorEnabled()) {
            log.error("[chargeBoxId=" + chargeBoxId + ", sessionId=" + session.getId() + "] Transport error", t);
        }
    }
}
