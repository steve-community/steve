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
package de.rwth.idsg.steve.ocpp.ws.custom;

import de.rwth.idsg.steve.ocpp.ws.data.SessionContext;
import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

import java.util.Deque;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
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

    public static WsSessionSelectStrategy fromName(String v) {
        for (WsSessionSelectStrategyEnum s: WsSessionSelectStrategyEnum.values()) {
            if (s.name().equals(v)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Could not find a valid WsSessionSelectStrategy for name: " + v);
    }
}
