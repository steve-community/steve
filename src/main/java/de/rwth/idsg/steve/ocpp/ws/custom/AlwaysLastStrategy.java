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
public class AlwaysLastStrategy implements WsSessionSelectStrategy {

    public AlwaysLastStrategy() {
        log.debug("Initialized");
    }

    @Override
    public WebSocketSession getSession(Deque<SessionContext> sessionContexts) {
        return sessionContexts.getLast().getSession();
    }
}
