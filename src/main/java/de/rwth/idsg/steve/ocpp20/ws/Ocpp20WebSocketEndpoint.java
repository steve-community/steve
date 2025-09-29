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
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.rwth.idsg.steve.ocpp20.config.Ocpp20Configuration;
import de.rwth.idsg.steve.ocpp20.model.*;
import de.rwth.idsg.steve.ocpp20.service.CentralSystemService20;
import de.rwth.idsg.steve.ocpp20.service.Ocpp20MessageDispatcher;
import de.rwth.idsg.steve.ocpp20.validation.Ocpp20JsonSchemaValidator;
import de.rwth.idsg.steve.ocpp20.ws.handler.Ocpp20MessageHandlerRegistry;
import de.rwth.idsg.steve.repository.ChargePointRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ConditionalOnProperty(name = "ocpp.v20.enabled", havingValue = "true")
public class Ocpp20WebSocketEndpoint extends TextWebSocketHandler {

    private final CentralSystemService20 centralSystemService;
    private final Ocpp20Configuration ocpp20Config;
    private final Ocpp20MessageDispatcher messageDispatcher;
    private final ChargePointRepository chargePointRepository;
    private final Ocpp20RateLimiter rateLimiter;
    private final Ocpp20MessageHandlerRegistry handlerRegistry;
    private final Ocpp20JsonSchemaValidator schemaValidator;
    private final ObjectMapper objectMapper;
    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    public Ocpp20WebSocketEndpoint(
            CentralSystemService20 centralSystemService,
            Ocpp20Configuration ocpp20Config,
            Ocpp20MessageDispatcher messageDispatcher,
            ChargePointRepository chargePointRepository,
            Ocpp20RateLimiter rateLimiter,
            Ocpp20MessageHandlerRegistry handlerRegistry,
            Ocpp20JsonSchemaValidator schemaValidator,
            @Qualifier("ocpp20ObjectMapper") ObjectMapper objectMapper) {
        this.centralSystemService = centralSystemService;
        this.ocpp20Config = ocpp20Config;
        this.messageDispatcher = messageDispatcher;
        this.chargePointRepository = chargePointRepository;
        this.rateLimiter = rateLimiter;
        this.handlerRegistry = handlerRegistry;
        this.schemaValidator = schemaValidator;

        SimpleModule ocppModule = new SimpleModule();
        ocppModule.addDeserializer(Ocpp20Message.class, new Ocpp20MessageDeserializer());
        objectMapper.registerModule(ocppModule);
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String chargeBoxId = extractChargeBoxId(session);

        if (!authenticateChargePoint(session, chargeBoxId)) {
            log.warn("Authentication failed for charge point '{}'", chargeBoxId);
            session.close(CloseStatus.POLICY_VIOLATION.withReason("Authentication failed"));
            return;
        }

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

        if (!rateLimiter.allowRequest(chargeBoxId)) {
            log.warn("Rate limit exceeded for charge point '{}'", chargeBoxId);
            sendError(session, null, "RateLimitExceeded", "Too many requests");
            return;
        }

        if (payload.length() > 1048576) {
            log.warn("Message too large from '{}': {} bytes", chargeBoxId, payload.length());
            sendError(session, null, "ProtocolError", "Message too large (max 1MB)");
            return;
        }

        log.debug("OCPP 2.0 message from '{}': {}", chargeBoxId, payload);

        try {
            Ocpp20Message ocppMessage = objectMapper.readValue(payload, Ocpp20Message.class);
            ocppMessage.validate();

            if (ocppMessage.isCall()) {
                Ocpp20JsonSchemaValidator.ValidationResult validationResult =
                    schemaValidator.validate(ocppMessage.getAction(), ocppMessage.getPayload());

                if (!validationResult.isValid() && !validationResult.isSkipped()) {
                    log.warn("Schema validation failed for '{}' from '{}': {}",
                        ocppMessage.getAction(), chargeBoxId, validationResult.getErrorMessage());
                    sendError(session, ocppMessage.getMessageId(), "FormationViolation",
                        validationResult.getErrorMessage());
                    return;
                }

                handleCallMessage(session, chargeBoxId, ocppMessage);
            } else if (ocppMessage.isCallResult()) {
                messageDispatcher.handleCallResult(ocppMessage.getMessageId(), ocppMessage.getPayload());
            } else if (ocppMessage.isCallError()) {
                String errorCode = ocppMessage.getPayload().get("errorCode").asText();
                String errorDescription = ocppMessage.getPayload().get("errorDescription").asText();
                Object errorDetails = ocppMessage.getPayload().get("errorDetails");
                messageDispatcher.handleCallError(ocppMessage.getMessageId(), errorCode, errorDescription, errorDetails);
            }

        } catch (IllegalArgumentException e) {
            log.warn("Invalid OCPP 2.0 message from '{}': {}", chargeBoxId, e.getMessage());
            sendError(session, null, "FormationViolation", e.getMessage());
        } catch (Exception e) {
            log.error("Error processing OCPP 2.0 message from '{}'", chargeBoxId, e);
            sendError(session, null, "InternalError", "Message processing failed");
        }
    }

    private void handleCallMessage(WebSocketSession session, String chargeBoxId, Ocpp20Message ocppMessage) {
        String action = ocppMessage.getAction();
        String messageId = ocppMessage.getMessageId();

        log.info("OCPP 2.0 Call from '{}': action={}, messageId={}", chargeBoxId, action, messageId);

        try {
            Object response = dispatchMessage(action, ocppMessage.getPayload(), chargeBoxId);
            sendCallResult(session, messageId, response);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request from '{}' for action '{}': {}", chargeBoxId, action, e.getMessage());
            sendError(session, messageId, "FormationViolation", e.getMessage());
        } catch (UnsupportedOperationException e) {
            log.warn("Unsupported action '{}' from '{}'", action, chargeBoxId);
            sendError(session, messageId, "NotSupported", "Action not supported: " + action);
        } catch (Exception e) {
            log.error("Error handling call message from '{}' for action '{}'", chargeBoxId, action, e);
            sendError(session, messageId, "InternalError", "Failed to process " + action);
        }
    }

    private Object dispatchMessage(String action, JsonNode payloadNode, String chargeBoxId) throws Exception {
        if (handlerRegistry.isActionSupported(action)) {
            return handlerRegistry.dispatch(action, payloadNode, chargeBoxId);
        }

        String payloadJson = objectMapper.writeValueAsString(payloadNode);
        return dispatchLegacy(action, payloadJson, chargeBoxId);
    }

    private Object dispatchLegacy(String action, String payloadJson, String chargeBoxId) throws Exception {
        switch (action) {
            case "TransactionEvent":
                TransactionEventRequest txReq = objectMapper.readValue(payloadJson, TransactionEventRequest.class);
                return centralSystemService.handleTransactionEvent(txReq, chargeBoxId);
            case "StatusNotification":
                StatusNotificationRequest statusReq = objectMapper.readValue(payloadJson, StatusNotificationRequest.class);
                return centralSystemService.handleStatusNotification(statusReq, chargeBoxId);
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

    private void sendCallResult(WebSocketSession session, String messageId, Object response) {
        try {
            if (session == null || !session.isOpen()) {
                log.warn("Cannot send CallResult for messageId '{}': session is null or closed", messageId);
                return;
            }
            List<Object> callResult = List.of(3, messageId, response);
            String json = objectMapper.writeValueAsString(callResult);
            session.sendMessage(new TextMessage(json));
            log.debug("Sent CallResult for messageId '{}'", messageId);
        } catch (Exception e) {
            log.error("Failed to send CallResult for messageId '{}'", messageId, e);
        }
    }

    private void sendError(WebSocketSession session, String messageId, String errorCode, String errorDescription) {
        try {
            if (session == null || !session.isOpen()) {
                log.warn("Cannot send CallError: session is null or closed");
                return;
            }
            List<Object> callError = List.of(4, messageId != null ? messageId : "", errorCode, errorDescription, Map.of());
            String json = objectMapper.writeValueAsString(callError);
            session.sendMessage(new TextMessage(json));
            log.debug("Sent CallError: {} - {}", errorCode, errorDescription);
        } catch (Exception e) {
            log.error("Failed to send CallError: {} - {}", errorCode, errorDescription, e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String chargeBoxId = extractChargeBoxId(session);
        try {
            sessionMap.remove(chargeBoxId);
            messageDispatcher.unregisterSession(chargeBoxId);
            rateLimiter.reset(chargeBoxId);
            log.info("OCPP 2.0 WebSocket connection closed for '{}': {}", chargeBoxId, status);
        } finally {
            sessionMap.remove(chargeBoxId);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        String chargeBoxId = extractChargeBoxId(session);
        log.error("OCPP 2.0 WebSocket transport error for '{}'", chargeBoxId, exception);

        try {
            if (session != null && session.isOpen()) {
                session.close(CloseStatus.SERVER_ERROR.withReason("Transport error"));
            }
        } catch (Exception e) {
            log.error("Failed to close session after transport error for '{}'", chargeBoxId, e);
        }
    }

    private String extractChargeBoxId(WebSocketSession session) {
        String path = session.getUri().getPath();
        String[] parts = path.split("/");
        return parts[parts.length - 1];
    }

    public WebSocketSession getSession(String chargeBoxId) {
        return sessionMap.get(chargeBoxId);
    }

    private boolean authenticateChargePoint(WebSocketSession session, String chargeBoxId) {
        try {
            if (!chargePointRepository.isRegistered(chargeBoxId)) {
                log.warn("Charge point '{}' not registered in database", chargeBoxId);
                return false;
            }

            HttpHeaders headers = session.getHandshakeHeaders();
            List<String> authHeaders = headers.get(HttpHeaders.AUTHORIZATION);

            if (authHeaders == null || authHeaders.isEmpty()) {
                log.debug("No Authorization header from '{}', allowing (backward compatibility)", chargeBoxId);
                return true;
            }

            String authHeader = authHeaders.get(0);
            if (!authHeader.startsWith("Basic ")) {
                log.warn("Invalid Authorization header format from '{}'", chargeBoxId);
                return false;
            }

            String base64Credentials = authHeader.substring(6);
            String credentials = new String(java.util.Base64.getDecoder().decode(base64Credentials));
            String[] parts = credentials.split(":", 2);

            if (parts.length != 2) {
                log.warn("Invalid credentials format from '{}'", chargeBoxId);
                return false;
            }

            String username = parts[0];
            String password = parts[1];

            if (!username.equals(chargeBoxId)) {
                log.warn("Username '{}' does not match chargeBoxId '{}'", username, chargeBoxId);
                return false;
            }

            boolean authenticated = chargePointRepository.validatePassword(chargeBoxId, password);
            if (!authenticated) {
                log.warn("Invalid password for charge point '{}'", chargeBoxId);
            }
            return authenticated;

        } catch (Exception e) {
            log.error("Authentication error for charge point '{}'", chargeBoxId, e);
            return false;
        }
    }
}