package de.rwth.idsg.steve.ocpp.ws.custom;

import de.rwth.idsg.steve.ocpp.ws.data.SessionContext;
import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

import java.util.Deque;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 30.04.2015
 */
@Getter
public enum WsSessionSelectStrategyEnum implements WsSessionSelectStrategy {

    ALWAYS_LAST {
        /**
         * Always use the last opened session/connection.
         */
        @Override
        public WebSocketSession getSession(Deque<SessionContext> sessionContexts) {
            return sessionContexts.getLast().getSession();
        }
    },

    ROUND_ROBIN {
        /**
         * The sessions/connections are chosen in a round robin fashion.
         * This would allow to distribute load to different connections.
         */
        @Override
        public WebSocketSession getSession(Deque<SessionContext> sessionContexts) {
            // Remove the first item, and add at the end
            SessionContext s = sessionContexts.removeFirst();
            sessionContexts.addLast(s);
            return s.getSession();
        }
    };

    public static WsSessionSelectStrategyEnum fromName(String v) {
        for (WsSessionSelectStrategyEnum s: WsSessionSelectStrategyEnum.values()) {
            if (s.name().equals(v)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Could not find a valid WsSessionSelectStrategy for name: " + v);
    }
}
