package de.rwth.idsg.steve.ocpp.ws;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.ocpp.ws.data.FutureResponseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Presumption: The responses must be sent using the same connection as the requests!
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 21.03.2015
 */
@Slf4j
@Service
public class FutureResponseContextStoreImpl implements FutureResponseContextStore {

    // We store for each chargeBox connection, multiple pairs of (messageId, context)
    // (session, (messageId, context))
    private final Map<WebSocketSession, Map<String, FutureResponseContext>> lookupTable = new HashMap<>();

    @Override
    public void addSession(WebSocketSession session) {
        lookupTable.computeIfAbsent(session, webSocketSession -> {
            log.debug("Creating new store for sessionId '{}'", webSocketSession.getId());
            return new ConcurrentHashMap<>();
        });
    }

    @Override
    public void removeSession(WebSocketSession session) {
        log.debug("Deleting the store for sessionId '{}'", session.getId());
        lookupTable.remove(session);
    }

    @Override
    public void add(WebSocketSession session, String messageId, FutureResponseContext context) {
        Map<String, FutureResponseContext> map = lookupTable.get(session);
        if (map == null) {
            throw new SteveException("sessionId '%s' is not in store", session.getId());
        } else {
            map.put(messageId, context);
            log.debug("Store size for sessionId '{}': {}", session.getId(), map.size());
        }
    }

    @Override
    public FutureResponseContext get(WebSocketSession session, String messageId) {
        Map<String, FutureResponseContext> map = lookupTable.get(session);
        if (map == null) {
            throw new SteveException("sessionId '%s' is not in store", session.getId());
        } else {
            FutureResponseContext context = map.remove(messageId);
            log.debug("Store size for sessionId '{}': {}", session.getId(), map.size());
            return context;
        }
    }
}
