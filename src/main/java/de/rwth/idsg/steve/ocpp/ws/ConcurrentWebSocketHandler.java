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

import de.rwth.idsg.steve.config.WebSocketConfiguration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 07.05.2019
 */
public abstract class ConcurrentWebSocketHandler implements WebSocketHandler {

    private static final int sendTimeLimit = (int) TimeUnit.SECONDS.toMillis(10);
    private static final int bufferSizeLimit = 5 * WebSocketConfiguration.MAX_MSG_SIZE;

    private final Map<String, ConcurrentWebSocketSessionDecorator> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        this.onOpen(internalGet(session));
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        this.onMessage(internalGet(session), message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        this.onError(internalGet(session), exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        this.onClose(sessions.remove(session.getId()), closeStatus);
    }

    private ConcurrentWebSocketSessionDecorator internalGet(WebSocketSession session) {
        return sessions.computeIfAbsent(session.getId(), s -> new ConcurrentWebSocketSessionDecorator(session, sendTimeLimit, bufferSizeLimit));
    }

    // -------------------------------------------------------------------------
    // Implement in extending classes
    // -------------------------------------------------------------------------

    abstract void onMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception;
    abstract void onOpen(WebSocketSession session) throws Exception;
    abstract void onClose(WebSocketSession session, CloseStatus closeStatus) throws Exception;
    abstract void onError(WebSocketSession session, Throwable throwable) throws Exception;
}
