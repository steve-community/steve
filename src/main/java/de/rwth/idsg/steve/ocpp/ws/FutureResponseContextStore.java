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

import de.rwth.idsg.steve.ocpp.ws.data.FutureResponseContext;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/**
 * Presumption: The responses must be sent using the same connection as the requests!
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 21.03.2015
 */
@Slf4j
@Service
public class FutureResponseContextStore {

    // We store for each chargeBox connection, multiple pairs of (messageId, context)
    // (session, (messageId, context))
    private final Map<WebSocketSession, Map<String, FutureResponseContext>> lookupTable = new ConcurrentHashMap<>();

    public void addSession(WebSocketSession session) {
        addIfAbsent(session);
    }

    public void removeSession(WebSocketSession session) {
        log.debug("Deleting the store for sessionId '{}'", session.getId());
        lookupTable.remove(session);
    }

    public void add(WebSocketSession session, String messageId, FutureResponseContext context) {
        Map<String, FutureResponseContext> map = addIfAbsent(session);
        map.put(messageId, context);
        log.debug("Store size for sessionId '{}': {}", session.getId(), map.size());
    }

    @Nullable
    public FutureResponseContext get(WebSocketSession session, String messageId) {
        RemoveFunction removeFunction = new RemoveFunction(messageId);
        lookupTable.computeIfPresent(session, removeFunction);
        return removeFunction.removedContext;
    }

    private Map<String, FutureResponseContext> addIfAbsent(WebSocketSession session) {
        return lookupTable.computeIfAbsent(session, innerSession -> {
            log.debug("Creating new store for sessionId '{}'", innerSession.getId());
            return new ConcurrentHashMap<>();
        });
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static class RemoveFunction implements
            BiFunction<WebSocketSession, Map<String, FutureResponseContext>, Map<String, FutureResponseContext>> {

        private final String messageId;
        @Nullable private FutureResponseContext removedContext;

        @Override
        public Map<String, FutureResponseContext> apply(WebSocketSession session,
                                                        Map<String, FutureResponseContext> map) {
            removedContext = map.remove(messageId);
            log.debug("Store size for sessionId '{}': {}", session.getId(), map.size());
            return map;
        }
    }
}
