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

import com.google.common.util.concurrent.Striped;
import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.config.WebSocketConfiguration;
import de.rwth.idsg.steve.ocpp.ws.custom.WsSessionSelectStrategy;
import de.rwth.idsg.steve.ocpp.ws.data.SessionContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 17.03.2015
 */
@Slf4j
@RequiredArgsConstructor
public class SessionContextStoreImpl implements SessionContextStore {

    /**
     * Key   (String)                = chargeBoxId
     * Value (Deque<SessionContext>) = WebSocket session contexts
     */
    private final ConcurrentHashMap<String, Deque<SessionContext>> lookupTable = new ConcurrentHashMap<>();
    private final IncomingMessageIdStore messageIdStore = new IncomingMessageIdStore();

    private final Striped<Lock> locks = Striped.lock(128);

    private final WsSessionSelectStrategy wsSessionSelectStrategy;
    private final TaskScheduler taskScheduler;
    private final FutureResponseContextStore futureResponseContextStore;

    @Override
    public boolean add(String chargeBoxId, WebSocketSession session) {
        Lock l = locks.get(chargeBoxId);
        l.lock();
        try {
            if (!session.isOpen()) {
                log.warn("Session closed. Skipping add for chargeBoxId '{}' and session '{}'", chargeBoxId, session.getId());
                return false; // we dont want to trigger any action based on this 'bad' session which we did not process anyway
            }

            // Just to keep the connection alive, such that the servers do not close
            // the connection because of a idle timeout, we ping-pong at fixed intervals.
            ScheduledFuture<?> pingSchedule = taskScheduler.scheduleAtFixedRate(
                new PingTask(chargeBoxId, session),
                Instant.now().plus(WebSocketConfiguration.PING_INTERVAL),
                WebSocketConfiguration.PING_INTERVAL
            );

            SessionContext context = new SessionContext(session, pingSchedule, DateTime.now());

            Deque<SessionContext> endpointDeque = lookupTable.computeIfAbsent(chargeBoxId, str -> new ArrayDeque<>());
            endpointDeque.addLast(context); // Adding at the end

            messageIdStore.addSession(session);
            futureResponseContextStore.addSession(session);

            int size = endpointDeque.size();
            log.debug("A new SessionContext is stored for chargeBoxId '{}'. Store size: {}", chargeBoxId, size);
            return size == 1;
        } finally {
            l.unlock();
        }
    }

    @Override
    public boolean remove(String chargeBoxId, WebSocketSession session) {
        Lock l = locks.get(chargeBoxId);
        l.lock();
        try {
            Deque<SessionContext> endpointDeque = lookupTable.get(chargeBoxId);
            if (endpointDeque == null) {
                log.debug("No session context to remove for chargeBoxId '{}'", chargeBoxId);
                return false; // we did not have any session anyway
            }

            for (var it = endpointDeque.iterator(); it.hasNext();) {
                SessionContext context = it.next();
                if (context.getSession().getId().equals(session.getId())) {
                    context.getPingSchedule().cancel(true);
                    it.remove();
                    log.debug("A SessionContext is removed for chargeBoxId '{}'. Store size: {}", chargeBoxId, endpointDeque.size());
                    break;
                }
            }

            // Delete empty collection from lookup table in order to correctly calculate
            // the number of connected chargeboxes with getNumberOfChargeBoxes()
            if (endpointDeque.isEmpty()) {
                lookupTable.remove(chargeBoxId);
            }

            messageIdStore.removeSession(session);
            futureResponseContextStore.removeSession(session);

            return endpointDeque.isEmpty();
        } finally {
            l.unlock();
        }
    }

    @Override
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

    @Override
    public Boolean registerIncomingCallId(String chargeBoxId, WebSocketSession session, String messageId) {
        Lock l = locks.get(chargeBoxId);
        l.lock();
        try {
            return messageIdStore.registerIncomingCallId(session, messageId);
        } finally {
            l.unlock();
        }
    }

    @Override
    public void closeSessions(String chargeBoxId) {
        Lock l = locks.get(chargeBoxId);
        l.lock();
        try {
            Deque<SessionContext> endpointDeque = lookupTable.get(chargeBoxId);
            if (endpointDeque == null) {
                return;
            }

            // To prevent a ConcurrentModificationException, iterate over a copy of
            // endpointDeque when closing sessions. The close() operation can trigger an event
            // that modifies the deque, causing an error.
            for (SessionContext sessionContext : new ArrayDeque<>(endpointDeque)) {
                try {
                    sessionContext.getSession().close();
                } catch (IOException e) {
                    log.error("Error while closing web socket session for chargeBoxId '{}'", chargeBoxId, e);
                }
            }
        } finally {
            l.unlock();
        }
    }

    @Override
    public int getSize(String chargeBoxId) {
        Deque<SessionContext> endpointDeque = lookupTable.get(chargeBoxId);
        return endpointDeque == null ? 0 : endpointDeque.size();
    }

    @Override
    public int getNumberOfChargeBoxes() {
        return lookupTable.size();
    }

    @Override
    public List<String> getChargeBoxIdList() {
        return Collections.list(lookupTable.keys());
    }

    @Override
    public Map<String, Collection<SessionContext>> getReadOnlyMap() {
        // we just want an immutable view of the map without copying the underlying data
        return Collections.unmodifiableMap(lookupTable);
    }
}
