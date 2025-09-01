/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2025 SteVe Community Team
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import de.rwth.idsg.steve.config.DelegatingTaskScheduler;
import de.rwth.idsg.steve.config.OcppWebSocketConfiguration;
import de.rwth.idsg.steve.ocpp.OcppTransport;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import de.rwth.idsg.steve.ocpp.ws.data.SessionContext;
import de.rwth.idsg.steve.ocpp.ws.pipeline.Deserializer;
import de.rwth.idsg.steve.ocpp.ws.pipeline.IncomingPipeline;
import de.rwth.idsg.steve.ocpp.ws.pipeline.Sender;
import de.rwth.idsg.steve.ocpp.ws.pipeline.Serializer;
import de.rwth.idsg.steve.repository.OcppServerRepository;
import de.rwth.idsg.steve.service.notification.OcppStationWebSocketConnected;
import de.rwth.idsg.steve.service.notification.OcppStationWebSocketDisconnected;
import org.jspecify.annotations.Nullable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.SubProtocolCapable;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 17.03.2015
 */
public abstract class AbstractWebSocketEndpoint extends ConcurrentWebSocketHandler implements SubProtocolCapable {
    public static final String CHARGEBOX_ID_KEY = "CHARGEBOX_ID_KEY";

    private final WebSocketLogger webSocketLogger;
    private final DelegatingTaskScheduler asyncTaskScheduler;
    private final OcppServerRepository ocppServerRepository;
    private final FutureResponseContextStore futureResponseContextStore;
    private final SessionContextStore sessionContextStore;
    private final IncomingPipeline pipeline;
    private final List<Consumer<String>> connectedCallbackList;
    private final List<Consumer<String>> disconnectedCallbackList;
    private final Object sessionContextLock = new Object();

    protected AbstractWebSocketEndpoint(
            WebSocketLogger webSocketLogger,
            DelegatingTaskScheduler asyncTaskScheduler,
            OcppServerRepository ocppServerRepository,
            FutureResponseContextStore futureResponseContextStore,
            ApplicationEventPublisher applicationEventPublisher,
            SessionContextStore sessionContextStore,
            Sender sender,
            ObjectMapper ocppMapper,
            TypeStore typeStore,
            Consumer<CommunicationContext> handler) {
        this.webSocketLogger = webSocketLogger;
        this.asyncTaskScheduler = asyncTaskScheduler;
        this.ocppServerRepository = ocppServerRepository;
        this.futureResponseContextStore = futureResponseContextStore;
        this.sessionContextStore = sessionContextStore;
        this.pipeline = new IncomingPipeline(
                new Serializer(ocppMapper),
                new Deserializer(ocppMapper, futureResponseContextStore, typeStore),
                sender,
                handler);
        this.connectedCallbackList = new ArrayList<>();
        this.connectedCallbackList.add(
                chargeBoxId -> applicationEventPublisher.publishEvent(new OcppStationWebSocketConnected(chargeBoxId)));
        this.disconnectedCallbackList = new ArrayList<>();
        this.disconnectedCallbackList.add(chargeBoxId ->
                applicationEventPublisher.publishEvent(new OcppStationWebSocketDisconnected(chargeBoxId)));
    }

    public abstract OcppVersion getVersion();

    @Override
    public List<String> getSubProtocols() {
        return Collections.singletonList(getVersion().getValue());
    }

    @Override
    public void onMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        switch (message) {
            case TextMessage textMessage -> handleTextMessage(session, textMessage);
            case PongMessage pongMessage -> handlePongMessage(session);
            case BinaryMessage binaryMessage ->
                session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Binary messages not supported"));
            default -> throw new IllegalStateException("Unexpected WebSocket message type: " + message);
        }
    }

    private void handleTextMessage(WebSocketSession session, TextMessage webSocketMessage) {
        var incomingString = webSocketMessage.getPayload();
        var chargeBoxId = getChargeBoxId(session);

        // https://github.com/steve-community/steve/issues/66
        if (Strings.isNullOrEmpty(incomingString)) {
            webSocketLogger.receivedEmptyText(chargeBoxId, session);
            return;
        }

        webSocketLogger.receivedText(chargeBoxId, session, incomingString);

        var context = new CommunicationContext(session, chargeBoxId);
        context.setIncomingString(incomingString);

        pipeline.accept(context);
    }

    private void handlePongMessage(WebSocketSession session) {
        var chargeBoxId = getChargeBoxId(session);
        webSocketLogger.receivedPong(chargeBoxId, session);
        ocppServerRepository.updateChargeboxHeartbeat(chargeBoxId, Instant.now());
    }

    @Override
    public void onOpen(WebSocketSession session) throws Exception {
        var chargeBoxId = getChargeBoxId(session);
        if (Strings.isNullOrEmpty(chargeBoxId)) {
            session.close(CloseStatus.POLICY_VIOLATION.withReason("Missing chargeBoxId"));
            return;
        }

        webSocketLogger.connected(chargeBoxId, session);
        ocppServerRepository.updateOcppProtocol(chargeBoxId, getVersion().toProtocol(OcppTransport.JSON));

        // Just to keep the connection alive, such that the servers do not close
        // the connection because of a idle timeout, we ping-pong at fixed intervals.
        var pingSchedule = asyncTaskScheduler.scheduleAtFixedRate(
                new PingTask(webSocketLogger, chargeBoxId, session),
                Instant.now().plus(OcppWebSocketConfiguration.PING_INTERVAL),
                OcppWebSocketConfiguration.PING_INTERVAL);

        futureResponseContextStore.addSession(session);

        int sizeBeforeAdd;

        synchronized (sessionContextLock) {
            sizeBeforeAdd = sessionContextStore.getSize(chargeBoxId);
            sessionContextStore.add(chargeBoxId, session, pingSchedule);
        }

        // Take into account that there might be multiple connections to a charging station.
        // Send notification only for the change 0 -> 1.
        if (sizeBeforeAdd == 0) {
            connectedCallbackList.forEach(consumer -> consumer.accept(chargeBoxId));
        }
    }

    @Override
    public void onClose(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        var chargeBoxId = getChargeBoxId(session);

        webSocketLogger.closed(chargeBoxId, session, closeStatus);

        futureResponseContextStore.removeSession(session);

        int sizeAfterRemove;

        synchronized (sessionContextLock) {
            sessionContextStore.remove(chargeBoxId, session);
            sizeAfterRemove = sessionContextStore.getSize(chargeBoxId);
        }

        // Take into account that there might be multiple connections to a charging station.
        // Send notification only for the change 1 -> 0.
        if (sizeAfterRemove == 0) {
            disconnectedCallbackList.forEach(consumer -> consumer.accept(chargeBoxId));
        }
    }

    @Override
    public void onError(WebSocketSession session, Throwable throwable) throws Exception {
        webSocketLogger.transportError(getChargeBoxId(session), session, throwable);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    protected @Nullable String getChargeBoxId(WebSocketSession session) {
        return (String) session.getAttributes().get(CHARGEBOX_ID_KEY);
    }

    protected void registerConnectedCallback(Consumer<String> consumer) {
        connectedCallbackList.add(consumer);
    }

    protected void registerDisconnectedCallback(Consumer<String> consumer) {
        disconnectedCallbackList.add(consumer);
    }

    public List<String> getChargeBoxIdList() {
        return sessionContextStore.getChargeBoxIdList();
    }

    public int getNumberOfChargeBoxes() {
        return sessionContextStore.getNumberOfChargeBoxes();
    }

    public Map<String, Deque<SessionContext>> getACopy() {
        return sessionContextStore.getACopy();
    }

    public WebSocketSession getSession(String chargeBoxId) {
        return sessionContextStore.getSession(chargeBoxId);
    }
}
