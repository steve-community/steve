/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2024 SteVe Community Team
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
import org.springframework.web.socket.WebSocketSession;

import java.util.Deque;

/**
 * We want to support multiple connections to a charge point. For sending messages we need a
 * mechanism to select one WebSocketSession. Implementations of this interface should use
 * different mechanisms to realize that.
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 30.04.2015
 */
public interface WsSessionSelectStrategy {
    WebSocketSession getSession(Deque<SessionContext> sessionContexts);
}
