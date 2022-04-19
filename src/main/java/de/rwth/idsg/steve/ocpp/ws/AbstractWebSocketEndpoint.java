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

import com.google.common.base.Strings;
import de.rwth.idsg.steve.config.WebSocketConfiguration;
import de.rwth.idsg.steve.ocpp.OcppTransport;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import de.rwth.idsg.steve.ocpp.ws.data.SessionContext;
import de.rwth.idsg.steve.ocpp.ws.pipeline.IncomingPipeline;
import de.rwth.idsg.steve.repository.OcppServerRepository;
import de.rwth.idsg.steve.service.NotificationService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.SubProtocolCapable;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 17.03.2015
 */
public abstract class AbstractWebSocketEndpoint extends ConcurrentWebSocketHandler implements SubProtocolCapable {

    @Autowired private ScheduledExecutorService service;
    @Autowired private OcppServerRepository ocppServerRepository;
    @Autowired private FutureResponseContextStore futureResponseContextStore;
    @Autowired private NotificationService notificationService;

    public static final String CHARGEBOX_ID_KEY = "CHARGEBOX_ID_KEY";

    private final SessionContextStore sessionContextStore = new SessionContextStore();
    private final List<Consumer<String>> connectedCallbackList = new ArrayList<>();
    private final List<Consumer<String>> disconnectedCallbackList = new ArrayList<>();
    private final Object sessionContextLock = new Object();

    private IncomingPipeline pipeline;

    public abstract OcppVersion getVersion();

    public void init(IncomingPipeline pipeline) {
        this.pipeline = pipeline;

        connectedCallbackList.add((chargeBoxId) -> notificationService.ocppStationWebSocketConnected(chargeBoxId));
        disconnectedCallbackList.add((chargeBoxId) -> notificationService.ocppStationWebSocketDisconnected(chargeBoxId));
    }

    @Override
    public List<String> getSubProtocols() {
        return Collections.singletonList(getVersion().getValue());
    }

    @Override
    public void onMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (message instanceof TextMessage) {
            handleTextMessage(session, (TextMessage) message);

        } else if (message instanceof PongMessage) {
            handlePongMessage(session);

        } else if (message instanceof BinaryMessage) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Binary messages not supported"));

        } else {
            throw new IllegalStateException("Unexpected WebSocket message type: " + message);
        }
    }

    private void handleTextMessage(WebSocketSession session, TextMessage webSocketMessage) throws Exception {
        String incomingString = webSocketMessage.getPayload();
        String chargeBoxId = getChargeBoxId(session);

        // https://github.com/RWTH-i5-IDSG/steve/issues/66
        if (Strings.isNullOrEmpty(incomingString)) {
            WebSocketLogger.receivedEmptyText(chargeBoxId, session);
            return;
        }

        WebSocketLogger.receivedText(chargeBoxId, session, incomingString);

        CommunicationContext context = new CommunicationContext(session, chargeBoxId);
        context.setIncomingString(incomingString);

        pipeline.accept(context);
    }

    private void handlePongMessage(WebSocketSession session) {
        WebSocketLogger.receivedPong(getChargeBoxId(session), session);
        ocppServerRepository.updateChargeboxHeartbeat(getChargeBoxId(session), DateTime.now());
    }

    @Override
    public void onOpen(WebSocketSession session) throws Exception {
        String chargeBoxId = getChargeBoxId(session);

        WebSocketLogger.connected(chargeBoxId, session);
        ocppServerRepository.updateOcppProtocol(chargeBoxId, getVersion().toProtocol(OcppTransport.JSON));

        // Just to keep the connection alive, such that the servers do not close
        // the connection because of a idle timeout, we ping-pong at fixed intervals.
        ScheduledFuture pingSchedule = service.scheduleAtFixedRate(
                new PingTask(chargeBoxId, session),
                WebSocketConfiguration.PING_INTERVAL,
                WebSocketConfiguration.PING_INTERVAL,
                TimeUnit.MINUTES);

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
        String chargeBoxId = getChargeBoxId(session);

        WebSocketLogger.closed(chargeBoxId, session, closeStatus);

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
        WebSocketLogger.transportError(getChargeBoxId(session), session, throwable);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    protected String getChargeBoxId(WebSocketSession session) {
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
