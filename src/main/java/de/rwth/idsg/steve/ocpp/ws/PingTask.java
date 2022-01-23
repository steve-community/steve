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
package de.rwth.idsg.steve.ocpp.ws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.nio.ByteBuffer;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 17.03.2015
 */
@Slf4j
@RequiredArgsConstructor
public class PingTask implements Runnable {
    private final String chargeBoxId;
    private final WebSocketSession session;

    private static final PingMessage PING_MESSAGE = new PingMessage(ByteBuffer.wrap("ping".getBytes(UTF_8)));

    @Override
    public void run() {
        WebSocketLogger.sendingPing(chargeBoxId, session);
        try {
            session.sendMessage(PING_MESSAGE);
        } catch (IOException e) {
            WebSocketLogger.pingError(chargeBoxId, session, e);
            // TODO: Do something about this
        }
    }
}
