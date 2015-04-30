package de.rwth.idsg.steve.ocpp.ws.custom;

import de.rwth.idsg.steve.ocpp.ws.data.SessionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.util.Deque;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 30.04.2015
 */
@Slf4j
public class RoundRobinStrategy implements WsSessionSelectStrategy {

    public RoundRobinStrategy() {
        log.debug("Initialized");
    }

    @Override
    public WebSocketSession getSession(Deque<SessionContext> sessionContexts, String chargeBoxId) {
        // Remove the first item, and add at the end
        SessionContext s = sessionContexts.removeFirst();
        sessionContexts.addLast(s);
        return s.getSession();
    }
}
