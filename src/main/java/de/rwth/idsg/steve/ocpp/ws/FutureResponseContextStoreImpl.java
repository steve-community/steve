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

import de.rwth.idsg.steve.ocpp.ws.data.FutureResponseContext;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Presumption: The responses must be sent using the same connection as the requests!
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 21.03.2015
 */
@Slf4j
@Service
public class FutureResponseContextStoreImpl implements FutureResponseContextStore {

    // We store for each chargeBox connection, multiple pairs of (messageId, context)
    // (webSocketSessionId, (messageId, context))
    private final Map<String, Map<String, FutureResponseContext>> lookupTable = new ConcurrentHashMap<>();

    @Override
    public void addSession(WebSocketSession session) {
        addIfAbsent(session.getId());
    }

    @Override
    public void removeSession(WebSocketSession session) {
        log.debug("Deleting the store for sessionId '{}'", session.getId());
        lookupTable.remove(session.getId());
    }

    /**
     * Adds/updates a correlation entry and performs opportunistic stale-entry cleanup on the write path.
     */
    @Override
    public void add(String webSocketSessionId, String messageId, FutureResponseContext context) {
        var map = addIfAbsent(webSocketSessionId);
        evictTimedOutEntries(map);
        map.put(messageId, context);
        log.debug("Store size for sessionId '{}': {}", webSocketSessionId, map.size());
    }

    @Nullable
    @Override
    public FutureResponseContext poll(String webSocketSessionId, String messageId) {
        var map = lookupTable.get(webSocketSessionId);
        if (map == null) {
            return null;
        }
        FutureResponseContext removedContext = map.remove(messageId);
        log.debug("Store size for sessionId '{}': {}", webSocketSessionId, map.size());
        return removedContext;
    }

    private Map<String, FutureResponseContext> addIfAbsent(String webSocketSessionId) {
        return lookupTable.computeIfAbsent(webSocketSessionId, innerSessionId -> {
            log.debug("Creating new store for sessionId '{}'", innerSessionId);
            return new ConcurrentHashMap<>();
        });
    }

    /**
     * Removes stale correlation entries to keep the lookup bounded when peers stop responding.
     */
    private static void evictTimedOutEntries(Map<String, FutureResponseContext> map) {
        Instant now = Instant.now();
        map.entrySet().removeIf(entry -> entry.getValue().hasTimedOut(now));
    }
}
