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
package de.rwth.idsg.steve.ocpp20.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.rwth.idsg.steve.ocpp20.config.Ocpp20Configuration;
import de.rwth.idsg.steve.ocpp20.model.*;
import de.rwth.idsg.steve.ocpp20.service.CentralSystemService20;
import de.rwth.idsg.steve.ocpp20.service.Ocpp20MessageDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ocpp.v20.enabled", havingValue = "true")
public class Ocpp20WebSocketEndpoint extends TextWebSocketHandler {

    private final CentralSystemService20 centralSystemService;
    private final Ocpp20Configuration ocpp20Config;
    private final Ocpp20MessageDispatcher messageDispatcher;
    private final ObjectMapper objectMapper = createObjectMapper();
    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String chargeBoxId = extractChargeBoxId(session);

        if (!ocpp20Config.isChargePointAllowed(chargeBoxId)) {
            log.warn("Charge point '{}' is not allowed to use OCPP 2.0 (beta mode)", chargeBoxId);
            session.close(CloseStatus.POLICY_VIOLATION.withReason("Not in OCPP 2.0 beta list"));
            return;
        }

        sessionMap.put(chargeBoxId, session);
        messageDispatcher.registerSession(chargeBoxId, session);
        log.info("OCPP 2.0 WebSocket connection established for '{}'", chargeBoxId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String chargeBoxId = extractChargeBoxId(session);
        String payload = message.getPayload();

        log.debug("OCPP 2.0 message from '{}': {}", chargeBoxId, payload);

        try {
            List<?> jsonArray = objectMapper.readValue(payload, List.class);

            if (jsonArray.size() < 3) {
                sendError(session, null, "ProtocolError", "Invalid message format");
                return;
            }

            int messageTypeId = (Integer) jsonArray.get(0);
            String messageId = (String) jsonArray.get(1);

            if (messageTypeId == 2) {
                handleCallMessage(session, chargeBoxId, messageId, jsonArray);
            } else if (messageTypeId == 3) {
                Object responsePayload = jsonArray.get(2);
                messageDispatcher.handleCallResult(messageId, responsePayload);
            } else if (messageTypeId == 4) {
                String errorCode = (String) jsonArray.get(2);
                String errorDescription = (String) jsonArray.get(3);
                Object errorDetails = jsonArray.size() > 4 ? jsonArray.get(4) : null;
                messageDispatcher.handleCallError(messageId, errorCode, errorDescription, errorDetails);
            } else {
                sendError(session, messageId, "MessageTypeNotSupported", "Unknown message type: " + messageTypeId);
            }

        } catch (Exception e) {
            log.error("Error processing OCPP 2.0 message from '{}'", chargeBoxId, e);
            sendError(session, null, "InternalError", e.getMessage());
        }
    }

    private void handleCallMessage(WebSocketSession session, String chargeBoxId, String messageId, List<?> jsonArray) throws Exception {
        String action = (String) jsonArray.get(2);
        Map<String, Object> payloadMap = (Map<String, Object>) jsonArray.get(3);

        log.info("OCPP 2.0 Call from '{}': action={}, messageId={}", chargeBoxId, action, messageId);

        Object response = dispatchMessage(action, payloadMap, chargeBoxId);

        sendCallResult(session, messageId, response);
    }

    private Object dispatchMessage(String action, Map<String, Object> payloadMap, String chargeBoxId) throws Exception {
        String payloadJson = objectMapper.writeValueAsString(payloadMap);

        switch (action) {
            case "BootNotification":
                BootNotificationRequest bootReq = objectMapper.readValue(payloadJson, BootNotificationRequest.class);
                return centralSystemService.handleBootNotification(bootReq, chargeBoxId);

            case "Authorize":
                AuthorizeRequest authReq = objectMapper.readValue(payloadJson, AuthorizeRequest.class);
                return centralSystemService.handleAuthorize(authReq, chargeBoxId);

            case "TransactionEvent":
                TransactionEventRequest txReq = objectMapper.readValue(payloadJson, TransactionEventRequest.class);
                return centralSystemService.handleTransactionEvent(txReq, chargeBoxId);

            case "StatusNotification":
                StatusNotificationRequest statusReq = objectMapper.readValue(payloadJson, StatusNotificationRequest.class);
                return centralSystemService.handleStatusNotification(statusReq, chargeBoxId);

            case "Heartbeat":
                HeartbeatRequest heartbeatReq = objectMapper.readValue(payloadJson, HeartbeatRequest.class);
                return centralSystemService.handleHeartbeat(heartbeatReq, chargeBoxId);

            case "MeterValues":
                MeterValuesRequest meterReq = objectMapper.readValue(payloadJson, MeterValuesRequest.class);
                return centralSystemService.handleMeterValues(meterReq, chargeBoxId);

            case "SignCertificate":
                SignCertificateRequest signCertReq = objectMapper.readValue(payloadJson, SignCertificateRequest.class);
                return centralSystemService.handleSignCertificate(signCertReq, chargeBoxId);

            case "SecurityEventNotification":
                SecurityEventNotificationRequest secEventReq = objectMapper.readValue(payloadJson, SecurityEventNotificationRequest.class);
                return centralSystemService.handleSecurityEventNotification(secEventReq, chargeBoxId);

            case "NotifyReport":
                NotifyReportRequest notifyReportReq = objectMapper.readValue(payloadJson, NotifyReportRequest.class);
                return centralSystemService.handleNotifyReport(notifyReportReq, chargeBoxId);

            case "NotifyEvent":
                NotifyEventRequest notifyEventReq = objectMapper.readValue(payloadJson, NotifyEventRequest.class);
                return centralSystemService.handleNotifyEvent(notifyEventReq, chargeBoxId);

            case "FirmwareStatusNotification":
                FirmwareStatusNotificationRequest fwStatusReq = objectMapper.readValue(payloadJson, FirmwareStatusNotificationRequest.class);
                return centralSystemService.handleFirmwareStatusNotification(fwStatusReq, chargeBoxId);

            case "LogStatusNotification":
                LogStatusNotificationRequest logStatusReq = objectMapper.readValue(payloadJson, LogStatusNotificationRequest.class);
                return centralSystemService.handleLogStatusNotification(logStatusReq, chargeBoxId);

            case "NotifyEVChargingNeeds":
                NotifyEVChargingNeedsRequest evNeedsReq = objectMapper.readValue(payloadJson, NotifyEVChargingNeedsRequest.class);
                return centralSystemService.handleNotifyEVChargingNeeds(evNeedsReq, chargeBoxId);

            case "ReportChargingProfiles":
                ReportChargingProfilesRequest reportProfilesReq = objectMapper.readValue(payloadJson, ReportChargingProfilesRequest.class);
                return centralSystemService.handleReportChargingProfiles(reportProfilesReq, chargeBoxId);

            case "ReservationStatusUpdate":
                ReservationStatusUpdateRequest resStatusReq = objectMapper.readValue(payloadJson, ReservationStatusUpdateRequest.class);
                return centralSystemService.handleReservationStatusUpdate(resStatusReq, chargeBoxId);

            case "ClearedChargingLimit":
                ClearedChargingLimitRequest clearedLimitReq = objectMapper.readValue(payloadJson, ClearedChargingLimitRequest.class);
                return centralSystemService.handleClearedChargingLimit(clearedLimitReq, chargeBoxId);

            case "NotifyChargingLimit":
                NotifyChargingLimitRequest notifyLimitReq = objectMapper.readValue(payloadJson, NotifyChargingLimitRequest.class);
                return centralSystemService.handleNotifyChargingLimit(notifyLimitReq, chargeBoxId);

            case "NotifyCustomerInformation":
                NotifyCustomerInformationRequest custInfoReq = objectMapper.readValue(payloadJson, NotifyCustomerInformationRequest.class);
                return centralSystemService.handleNotifyCustomerInformation(custInfoReq, chargeBoxId);

            case "NotifyDisplayMessages":
                NotifyDisplayMessagesRequest displayMsgReq = objectMapper.readValue(payloadJson, NotifyDisplayMessagesRequest.class);
                return centralSystemService.handleNotifyDisplayMessages(displayMsgReq, chargeBoxId);

            case "NotifyEVChargingSchedule":
                NotifyEVChargingScheduleRequest evScheduleReq = objectMapper.readValue(payloadJson, NotifyEVChargingScheduleRequest.class);
                return centralSystemService.handleNotifyEVChargingSchedule(evScheduleReq, chargeBoxId);

            case "NotifyMonitoringReport":
                NotifyMonitoringReportRequest monitoringReq = objectMapper.readValue(payloadJson, NotifyMonitoringReportRequest.class);
                return centralSystemService.handleNotifyMonitoringReport(monitoringReq, chargeBoxId);

            case "PublishFirmwareStatusNotification":
                PublishFirmwareStatusNotificationRequest pubFwReq = objectMapper.readValue(payloadJson, PublishFirmwareStatusNotificationRequest.class);
                return centralSystemService.handlePublishFirmwareStatusNotification(pubFwReq, chargeBoxId);

            default:
                throw new IllegalArgumentException("Unsupported action: " + action);
        }
    }

    private void sendCallResult(WebSocketSession session, String messageId, Object response) throws Exception {
        List<Object> callResult = List.of(3, messageId, response);
        String json = objectMapper.writeValueAsString(callResult);
        session.sendMessage(new TextMessage(json));
        log.debug("Sent CallResult for messageId '{}'", messageId);
    }

    private void sendError(WebSocketSession session, String messageId, String errorCode, String errorDescription) throws Exception {
        List<Object> callError = List.of(4, messageId != null ? messageId : "", errorCode, errorDescription, Map.of());
        String json = objectMapper.writeValueAsString(callError);
        session.sendMessage(new TextMessage(json));
        log.debug("Sent CallError: {} - {}", errorCode, errorDescription);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String chargeBoxId = extractChargeBoxId(session);
        sessionMap.remove(chargeBoxId);
        messageDispatcher.unregisterSession(chargeBoxId);
        log.info("OCPP 2.0 WebSocket connection closed for '{}': {}", chargeBoxId, status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        String chargeBoxId = extractChargeBoxId(session);
        log.error("OCPP 2.0 WebSocket transport error for '{}'", chargeBoxId, exception);
    }

    private String extractChargeBoxId(WebSocketSession session) {
        String path = session.getUri().getPath();
        String[] parts = path.split("/");
        return parts[parts.length - 1];
    }

    public WebSocketSession getSession(String chargeBoxId) {
        return sessionMap.get(chargeBoxId);
    }
}