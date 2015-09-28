package de.rwth.idsg.steve.ocpp.ws;

import com.google.common.collect.ImmutableMap;
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

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 17.03.2015
 */
@Slf4j
public class SessionContextStoreImpl implements SessionContextStore {

    /**
     * Key   (String)                = chargeBoxId
     * Value (Deque<SessionContext>) = WebSocket session contexts
     */
    private final ConcurrentHashMap<String, Deque<SessionContext>> lookupTable = new ConcurrentHashMap<>();

    private final WsSessionSelectStrategy wsSessionSelectStrategy;

    public SessionContextStoreImpl(WsSessionSelectStrategy wsSessionSelectStrategy) {
        this.wsSessionSelectStrategy = wsSessionSelectStrategy;
    }

    @Override
    public void add(String chargeBoxId, WebSocketSession session, ScheduledFuture pingSchedule) {
        SessionContext context = new SessionContext(session, pingSchedule, new DateTime());

        Deque<SessionContext> endpointDeque = lookupTable.get(chargeBoxId);
        if (endpointDeque == null) {
            final Deque<SessionContext> emptyDeque = new ArrayDeque<>();
            endpointDeque = lookupTable.putIfAbsent(chargeBoxId, emptyDeque);
            if (endpointDeque == null) {
                endpointDeque = emptyDeque;
            }
        }
        endpointDeque.addLast(context); // Adding at the end
        log.debug("A new SessionContext is stored for chargeBoxId '{}'. Store size: {}",
                chargeBoxId, endpointDeque.size());
    }

    @Override
    public void remove(String chargeBoxId, WebSocketSession session) {
        Deque<SessionContext> endpointDeque = lookupTable.get(chargeBoxId);
        if (endpointDeque == null) {
            log.debug("No session context to remove for chargeBoxId '{}'", chargeBoxId);
            return;
        }

        for (SessionContext context : endpointDeque) {
            if (context.getSession() == session) {
                // 1. Cancel the ping task
                context.getPingSchedule().cancel(true);
                // 2. Delete from collection
                if (endpointDeque.remove(context)) {
                    log.debug("A SessionContext is removed for chargeBoxId '{}'. Store size: {}",
                            chargeBoxId, endpointDeque.size());
                }
                // 3. Delete empty collection from lookup table in order to correctly calculate
                // the number of connected chargeboxes with getNumberOfChargeBoxes()
                if (endpointDeque.size() == 0) {
                    lookupTable.remove(chargeBoxId);
                }
                return;
            }
        }
    }

    @Override
    public List<String> getChargeBoxIdList() {
        return Collections.list(lookupTable.keys());
    }

    @Override
    public Map<String, Deque<SessionContext>> getACopy() {
        return ImmutableMap.copyOf(lookupTable);
    }

    @Override
    public int getNumberOfChargeBoxes() {
        return lookupTable.size();
    }

    @Override
    public WebSocketSession getSession(String chargeBoxId) {
        Deque<SessionContext> endpointDeque = lookupTable.get(chargeBoxId);
        try {
            return wsSessionSelectStrategy.getSession(endpointDeque);
        } catch (NoSuchElementException e) {
            throw new SteveException("No session context for chargeBoxId '%s'", chargeBoxId, e);
        }
    }

}
