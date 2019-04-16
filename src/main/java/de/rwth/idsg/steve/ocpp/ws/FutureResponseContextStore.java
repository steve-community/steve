package de.rwth.idsg.steve.ocpp.ws;

import de.rwth.idsg.steve.ocpp.ws.data.FutureResponseContext;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.socket.WebSocketSession;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 21.03.2015
 */
public interface FutureResponseContextStore {
    void addSession(WebSocketSession session);
    void removeSession(WebSocketSession session);
    void add(WebSocketSession session, String messageId, FutureResponseContext context);
    @Nullable FutureResponseContext get(WebSocketSession session, String messageId);
}
