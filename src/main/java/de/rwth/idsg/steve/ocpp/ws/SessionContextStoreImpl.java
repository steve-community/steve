package de.rwth.idsg.steve.ocpp.ws;

import com.google.common.collect.ImmutableMap;
import de.rwth.idsg.steve.SteveException;
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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * We want to support multiple connections to a charge point.
 *
 * For sending messages we need a mechanism to select one WebSocketSession.
 * This is done in a round robin fashion. See {@link #getSession(String)}.
 *
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

    @Override
    public void add(String chargeBoxId, WebSocketSession session, ScheduledFuture pingSchedule) {
        SessionContext context = new SessionContext(session, pingSchedule, new DateTime());

        Deque<SessionContext> endpointDeque = lookupTable.get(chargeBoxId);
        if (endpointDeque == null) {
            endpointDeque = new ArrayDeque<>();
            endpointDeque.add(context);
            lookupTable.put(chargeBoxId, endpointDeque);

        } else {
            endpointDeque.addLast(context); // Adding at the end
        }
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
    public int getNumberOfConnections(String chargeBoxId) {
        Deque<SessionContext> endpointDeque = lookupTable.get(chargeBoxId);
        return (endpointDeque == null) ? 0 : endpointDeque.size();
    }

    @Override
    public WebSocketSession getSession(String chargeBoxId) {
        Deque<SessionContext> endpointDeque = lookupTable.get(chargeBoxId);
        try {
            // Remove the first item, and add at the end
            SessionContext s = endpointDeque.removeFirst();
            endpointDeque.addLast(s);
            return s.getSession();

        } catch (NoSuchElementException e) {
            throw new SteveException("No session context for chargeBoxId '" + chargeBoxId + "'", e);
        }
    }

}
