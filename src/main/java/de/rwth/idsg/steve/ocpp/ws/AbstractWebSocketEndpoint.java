/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2019 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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

import com.google.common.base.Strings;
import de.rwth.idsg.steve.SteveConfiguration;
import de.rwth.idsg.steve.config.WebSocketConfiguration;
import de.rwth.idsg.steve.ocpp.OcppTransport;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.ws.cluster.ClusteredInvokerClient;
import de.rwth.idsg.steve.ocpp.ws.cluster.ClusteredWebSocketSessionStore;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import de.rwth.idsg.steve.ocpp.ws.data.SessionContext;
import de.rwth.idsg.steve.ocpp.ws.pipeline.IncomingPipeline;
import de.rwth.idsg.steve.service.notification.OcppStationWebSocketConnected;
import de.rwth.idsg.steve.service.notification.OcppStationWebSocketDisconnected;
import de.rwth.idsg.steve.repository.TaskStore;
import net.parkl.ocpp.entities.OcppChargeBox;
import net.parkl.ocpp.service.cs.ChargePointService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.socket.*;

import javax.annotation.PostConstruct;
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
    @Autowired private FutureResponseContextStore futureResponseContextStore;
    @Autowired private ApplicationEventPublisher applicationEventPublisher;

    @Autowired private ChargePointService chargePointService;
    @Autowired private ClusteredInvokerClient clusteredInvokerClient;


    @Autowired
    private SteveConfiguration config;
    @Autowired
    private ClusteredWebSocketSessionStore clusteredWebSocketSessionStore;

    public static final String CHARGEBOX_ID_KEY = "CHARGEBOX_ID_KEY";

    private SessionContextStore sessionContextStore;
    private final List<Consumer<String>> connectedCallbackList = new ArrayList<>();
    private final List<Consumer<String>> disconnectedCallbackList = new ArrayList<>();
    private final Object sessionContextLock = new Object();

    private IncomingPipeline pipeline;

    public abstract OcppVersion getVersion();

    @PostConstruct
    public void setup() {
    	sessionContextStore = new SessionContextStore(config, clusteredWebSocketSessionStore);
    }

    public void init(IncomingPipeline pipeline) {
        this.pipeline = pipeline;

        connectedCallbackList.add((chargeBoxId) -> applicationEventPublisher.publishEvent(new OcppStationWebSocketConnected(chargeBoxId)));
        disconnectedCallbackList.add((chargeBoxId) -> applicationEventPublisher.publishEvent(new OcppStationWebSocketDisconnected(chargeBoxId)));
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

        // https://github.com/steve-community/steve/issues/66
        if (Strings.isNullOrEmpty(incomingString)) {
            WebSocketLogger.receivedEmptyText(chargeBoxId, session);
            return;
        }

        WebSocketLogger.receivedText(chargeBoxId, session, incomingString);

        CommunicationContext context = new CommunicationContext(session, clusteredInvokerClient, chargeBoxId, null);
        context.setIncomingString(incomingString);

        pipeline.accept(context);
    }

    private void handlePongMessage(WebSocketSession session) {
        WebSocketLogger.receivedPong(getChargeBoxId(session), session);
        chargePointService.updateChargeboxHeartbeat(getChargeBoxId(session), DateTime.now());
    }

    @Override
    public void onOpen(WebSocketSession session) throws Exception {
        String chargeBoxId = getChargeBoxId(session);

        WebSocketLogger.connected(chargeBoxId, session);
        chargePointService.updateOcppProtocol(chargeBoxId, getVersion().toProtocol(OcppTransport.JSON));

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

    public boolean containsLocalSession(String chargeBoxId) {
        return sessionContextStore.containsLocalSession(chargeBoxId);
    }
}
