/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
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
import de.rwth.idsg.steve.ocpp.OcppTransport;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import de.rwth.idsg.steve.ocpp.ws.pipeline.IncomingPipeline;
import de.rwth.idsg.steve.ocpp.ws.pipeline.OcppCallHandler;
import de.rwth.idsg.steve.repository.OcppServerRepository;
import de.rwth.idsg.steve.service.notification.OcppStationWebSocketConnected;
import de.rwth.idsg.steve.service.notification.OcppStationWebSocketDisconnected;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.SubProtocolCapable;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 17.03.2015
 */
@Component
@RequiredArgsConstructor
public class WebSocketEndpoint extends ConcurrentWebSocketHandler implements SubProtocolCapable {

    public static final String CHARGEBOX_ID_KEY = "CHARGEBOX_ID_KEY";

    private final List<OcppCallHandler> versionHandlers;
    private final OcppServerRepository ocppServerRepository;
    private final SessionContextStoreHolder sessionContextStoreHolder;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final IncomingPipeline incomingPipeline;

    @Override
    public List<String> getSubProtocols() {
        return versionHandlers.stream()
            .map(it -> it.getVersion().getValue())
            .toList();
    }

    @Override
    public void onMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (message instanceof TextMessage textMessage) {
            handleTextMessage(session, textMessage);

        } else if (message instanceof PongMessage) {
            handlePongMessage(session);

        } else if (message instanceof BinaryMessage) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Binary messages not supported"));

        } else {
            throw new IllegalStateException("Unexpected WebSocket message type: " + message);
        }
    }

    private void handleTextMessage(WebSocketSession session, TextMessage webSocketMessage) throws Exception {
        var chargeBoxId = getChargeBoxId(session);
        var version = getVersion(session);

        String incomingString = webSocketMessage.getPayload();

        // https://github.com/steve-community/steve/issues/66
        if (Strings.isNullOrEmpty(incomingString)) {
            WebSocketLogger.receivedEmptyText(chargeBoxId, session);
            return;
        }

        WebSocketLogger.receivedText(chargeBoxId, session, incomingString);

        var inMsg = new CommunicationContext.In(
            chargeBoxId,
            version.toProtocol(OcppTransport.JSON),
            session.getId(),
            incomingString
        );

        incomingPipeline.accept(inMsg);
    }

    private void handlePongMessage(WebSocketSession session) {
        WebSocketLogger.receivedPong(getChargeBoxId(session), session);
        ocppServerRepository.updateChargeboxHeartbeat(getChargeBoxId(session), DateTime.now());
    }

    @Override
    public void onOpen(WebSocketSession session) throws Exception {
        var chargeBoxId = getChargeBoxId(session);
        var version = getVersion(session);

        WebSocketLogger.connected(chargeBoxId, session);

        var sessionContextStore = sessionContextStoreHolder.getOrCreate(version);
        boolean stationConnected = sessionContextStore.add(chargeBoxId, session);

        ocppServerRepository.updateOcppProtocol(chargeBoxId, version.toProtocol(OcppTransport.JSON));

        // Take into account that there might be multiple connections to a charging station.
        // Send notification only for the change 0 -> 1.
        if (stationConnected) {
            applicationEventPublisher.publishEvent(new OcppStationWebSocketConnected(chargeBoxId, version));
        }
    }

    @Override
    public void onClose(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        var chargeBoxId = getChargeBoxId(session);
        var version = getVersion(session);

        WebSocketLogger.closed(chargeBoxId, session, closeStatus);

        var sessionContextStore = sessionContextStoreHolder.getOrCreate(version);
        boolean stationDisconnected = sessionContextStore.remove(chargeBoxId, session);

        // Take into account that there might be multiple connections to a charging station.
        // Send notification only for the change 1 -> 0.
        if (stationDisconnected) {
            applicationEventPublisher.publishEvent(new OcppStationWebSocketDisconnected(chargeBoxId, version));
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

    private OcppVersion getVersion(WebSocketSession session) {
        return OcppVersion.fromValue(session.getAcceptedProtocol());
    }
}
