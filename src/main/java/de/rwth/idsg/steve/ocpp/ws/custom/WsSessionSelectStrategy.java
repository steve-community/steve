package de.rwth.idsg.steve.ocpp.ws.custom;

import de.rwth.idsg.steve.ocpp.ws.data.SessionContext;
import org.springframework.web.socket.WebSocketSession;

import java.util.Deque;

/**
 * We want to support multiple connections to a charge point. For sending messages we need a
 * mechanism to select one WebSocketSession. Implementations of this interface should use
 * different mechanisms to realize that.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 30.04.2015
 */
public interface WsSessionSelectStrategy {
    WebSocketSession getSession(Deque<SessionContext> sessionContexts, String chargeBoxId);
}
