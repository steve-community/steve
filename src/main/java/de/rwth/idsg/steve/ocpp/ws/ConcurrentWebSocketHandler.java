/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2024 SteVe Community Team
 * All Rights Reserved.
 *
 * Parkl Digital Technologies
 * Copyright (C) 2020-2021
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

import de.rwth.idsg.steve.config.WebSocketConfiguration;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import net.parkl.analytics.dto.ChargerConnectionRequest;
import net.parkl.ocpp.analytics.AnalyticsClient;
import net.parkl.ocpp.service.config.AdvancedChargeBoxConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.adapter.standard.StandardWebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static de.rwth.idsg.steve.config.WebSocketConfiguration.IDLE_TIMEOUT_IN_MS;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 07.05.2019
 */
@Slf4j
public abstract class ConcurrentWebSocketHandler implements WebSocketHandler {

    @Value("${ocpp.ws.buffer.multiplier:1}")
    private float defaultBufferMultiplier;

    @Autowired
    protected AdvancedChargeBoxConfiguration advancedChargeBoxConfiguration;
    @Autowired
    protected AnalyticsClient analyticsClient;

    private static final int sendTimeLimit = (int) TimeUnit.SECONDS.toMillis(600);

    private static final int bufferSizeLimit = WebSocketConfiguration.MAX_MSG_SIZE;


    private final Map<String, ConcurrentWebSocketSessionDecorator> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        this.onOpen(internalGet(session));
        float bufferMultiplier = getBufferMultiplier(session);
        session.setBinaryMessageSizeLimit((int)(bufferMultiplier * bufferSizeLimit));
        session.setTextMessageSizeLimit((int)(bufferMultiplier * bufferSizeLimit));
        final Session nativeSession = ((StandardWebSocketSession) session).getNativeSession(Session.class);
        nativeSession.getUserProperties()
                .put("org.apache.tomcat.websocket.READ_IDLE_TIMEOUT_MS", IDLE_TIMEOUT_IN_MS);
        nativeSession.getUserProperties()
                .put("org.apache.tomcat.websocket.WRITE_IDLE_TIMEOUT_MS", IDLE_TIMEOUT_IN_MS);
        nativeSession.getUserProperties()
                .put("org.apache.tomcat.websocket.BLOCKING_SEND_TIMEOUT", IDLE_TIMEOUT_IN_MS);
        String podIp = System.getenv("POD_IP");
        if (podIp == null) {
            podIp = "unknown";
        }
        String chargeBoxId = (String) session.getAttributes().get(AbstractWebSocketEndpoint.CHARGEBOX_ID_KEY);
        ChargerConnectionRequest req = ChargerConnectionRequest.builder()
                .chargerBoxId(chargeBoxId)
                .connectedAt(String.valueOf(LocalDateTime.now(Clock.systemUTC())))
                .podIp(podIp)
                .serverType("JAVA")
                .build();

        analyticsClient.createConnection(req)
                .doOnNext(connection -> log.info("Created connection in analytics: {}", connection))
                .doOnError(error -> log.error("Failed to create connection in analytics", error))
                .subscribe();

        log.info("Created new session {} with buffer size {}", session.getId(), session.getTextMessageSizeLimit());
    }

    private float getBufferMultiplier(WebSocketSession session) {
        String chargeBoxId=(String) session.getAttributes().get(AbstractWebSocketEndpoint.CHARGEBOX_ID_KEY);
        return advancedChargeBoxConfiguration.getWebSocketBufferMultiplier(chargeBoxId, defaultBufferMultiplier);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        this.onMessage(internalGet(session), message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        this.onError(internalGet(session), exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        this.onClose(sessions.remove(session.getId()), closeStatus);
    }

    private ConcurrentWebSocketSessionDecorator internalGet(WebSocketSession session) {
        return sessions.computeIfAbsent(session.getId(), s -> new ConcurrentWebSocketSessionDecorator(session, sendTimeLimit, (int)(getBufferMultiplier(session) * bufferSizeLimit)));
    }

    // -------------------------------------------------------------------------
    // Implement in extending classes
    // -------------------------------------------------------------------------

    abstract void onMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception;

    abstract void onOpen(WebSocketSession session) throws Exception;

    abstract void onClose(WebSocketSession session, CloseStatus closeStatus) throws Exception;

    abstract void onError(WebSocketSession session, Throwable throwable) throws Exception;
}
