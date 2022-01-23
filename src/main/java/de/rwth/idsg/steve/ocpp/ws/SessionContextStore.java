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

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.Striped;
import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.ocpp.ws.custom.WsSessionSelectStrategy;
import de.rwth.idsg.steve.ocpp.ws.data.SessionContext;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;

import static de.rwth.idsg.steve.SteveConfiguration.CONFIG;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 17.03.2015
 */
@Slf4j
public class SessionContextStore {

    /**
     * Key   (String)                = chargeBoxId
     * Value (Deque<SessionContext>) = WebSocket session contexts
     */
    private final ConcurrentHashMap<String, Deque<SessionContext>> lookupTable = new ConcurrentHashMap<>();

    private final Striped<Lock> locks = Striped.lock(16);

    private final WsSessionSelectStrategy wsSessionSelectStrategy = CONFIG.getOcpp().getWsSessionSelectStrategy();

    public void add(String chargeBoxId, WebSocketSession session, ScheduledFuture pingSchedule) {
        Lock l = locks.get(chargeBoxId);
        l.lock();
        try {
            SessionContext context = new SessionContext(session, pingSchedule, DateTime.now());

            Deque<SessionContext> endpointDeque = lookupTable.computeIfAbsent(chargeBoxId, str -> new ArrayDeque<>());
            endpointDeque.addLast(context); // Adding at the end

            log.debug("A new SessionContext is stored for chargeBoxId '{}'. Store size: {}",
                    chargeBoxId, endpointDeque.size());
        } finally {
            l.unlock();
        }
    }

    public void remove(String chargeBoxId, WebSocketSession session) {
        Lock l = locks.get(chargeBoxId);
        l.lock();
        try {
            Deque<SessionContext> endpointDeque = lookupTable.get(chargeBoxId);
            if (endpointDeque == null) {
                log.debug("No session context to remove for chargeBoxId '{}'", chargeBoxId);
                return;
            }

            // Prevent "java.util.ConcurrentModificationException: null"
            // Reason: Cannot modify the set (remove the item) we are iterating
            // Solution: Iterate the set, find the item, remove the item after the for-loop
            //
            SessionContext toRemove = null;
            for (SessionContext context : endpointDeque) {
                if (context.getSession().getId().equals(session.getId())) {
                    toRemove = context;
                    break;
                }
            }

            if (toRemove != null) {
                // 1. Cancel the ping task
                toRemove.getPingSchedule().cancel(true);
                // 2. Delete from collection
                if (endpointDeque.remove(toRemove)) {
                    log.debug("A SessionContext is removed for chargeBoxId '{}'. Store size: {}",
                            chargeBoxId, endpointDeque.size());
                }
                // 3. Delete empty collection from lookup table in order to correctly calculate
                // the number of connected chargeboxes with getNumberOfChargeBoxes()
                if (endpointDeque.size() == 0) {
                    lookupTable.remove(chargeBoxId);
                }
            }
        } finally {
            l.unlock();
        }
    }

    public WebSocketSession getSession(String chargeBoxId) {
        Lock l = locks.get(chargeBoxId);
        l.lock();
        try {
            Deque<SessionContext> endpointDeque = lookupTable.get(chargeBoxId);
            if (endpointDeque == null) {
                throw new NoSuchElementException();
            }
            return wsSessionSelectStrategy.getSession(endpointDeque);
        } catch (NoSuchElementException e) {
            throw new SteveException("No session context for chargeBoxId '%s'", chargeBoxId, e);
        } finally {
            l.unlock();
        }
    }

    public int getSize(String chargeBoxId) {
        Deque<SessionContext> endpointDeque = lookupTable.get(chargeBoxId);
        if (endpointDeque == null) {
            return 0;
        } else {
            return endpointDeque.size();
        }
    }

    public int getNumberOfChargeBoxes() {
        return lookupTable.size();
    }

    public List<String> getChargeBoxIdList() {
        return Collections.list(lookupTable.keys());
    }

    public Map<String, Deque<SessionContext>> getACopy() {
        return ImmutableMap.copyOf(lookupTable);
    }
}
