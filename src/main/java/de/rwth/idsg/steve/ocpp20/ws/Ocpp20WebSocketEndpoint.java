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
import de.rwth.idsg.steve.ocpp20.security.Ocpp20AuthenticationConfig;
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
import java.net.InetSocketAddress;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;

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
    private final Ocpp20AuthenticationConfig authConfig;
    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    public Ocpp20WebSocketEndpoint(
            CentralSystemService20 centralSystemService,
            Ocpp20Configuration ocpp20Config,
            Ocpp20MessageDispatcher messageDispatcher,
            ChargePointRepository chargePointRepository,
            Ocpp20RateLimiter rateLimiter,
            Ocpp20MessageHandlerRegistry handlerRegistry,
            Ocpp20JsonSchemaValidator schemaValidator,
            Ocpp20AuthenticationConfig authConfig,
            @Qualifier("ocpp20ObjectMapper") ObjectMapper objectMapper) {
        this.centralSystemService = centralSystemService;
        this.ocpp20Config = ocpp20Config;
        this.messageDispatcher = messageDispatcher;
        this.chargePointRepository = chargePointRepository;
        this.rateLimiter = rateLimiter;
        this.handlerRegistry = handlerRegistry;
        this.schemaValidator = schemaValidator;
        this.authConfig = authConfig;

        SimpleModule ocppModule = new SimpleModule();
        ocppModule.addDeserializer(Ocpp20Message.class, new Ocpp20MessageDeserializer());
        objectMapper.registerModule(ocppModule);
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String chargeBoxId = extractChargeBoxId(session);
        String remoteIP = extractRemoteIP(session);

        // Check IP blacklist first
        if (authConfig.isBlacklisted(remoteIP)) {
            log.warn("Connection from blacklisted IP '{}' for charge point '{}'", remoteIP, chargeBoxId);
            session.close(CloseStatus.POLICY_VIOLATION.withReason("IP address blacklisted"));
            return;
        }

        // Check IP whitelist
        if (!authConfig.isWhitelisted(remoteIP)) {
            log.warn("Connection from non-whitelisted IP '{}' for charge point '{}'", remoteIP, chargeBoxId);
            session.close(CloseStatus.POLICY_VIOLATION.withReason("IP address not whitelisted"));
            return;
        }

        // Skip authentication for trusted networks if configured
        boolean isTrustedNetwork = authConfig.isTrustedNetwork(remoteIP);
        if (isTrustedNetwork) {
            log.info("Connection from trusted network '{}' for charge point '{}', skipping authentication", remoteIP, chargeBoxId);
        } else if (!authenticateChargePoint(session, chargeBoxId)) {
            log.warn("Authentication failed for charge point '{}' from IP '{}'", chargeBoxId, remoteIP);
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
        return switch (action) {
            case "TransactionEvent" ->
                centralSystemService.handleTransactionEvent(
                    objectMapper.readValue(payloadJson, TransactionEventRequest.class), chargeBoxId);
            case "StatusNotification" ->
                centralSystemService.handleStatusNotification(
                    objectMapper.readValue(payloadJson, StatusNotificationRequest.class), chargeBoxId);
            case "MeterValues" ->
                centralSystemService.handleMeterValues(
                    objectMapper.readValue(payloadJson, MeterValuesRequest.class), chargeBoxId);
            case "SignCertificate" ->
                centralSystemService.handleSignCertificate(
                    objectMapper.readValue(payloadJson, SignCertificateRequest.class), chargeBoxId);
            case "SecurityEventNotification" ->
                centralSystemService.handleSecurityEventNotification(
                    objectMapper.readValue(payloadJson, SecurityEventNotificationRequest.class), chargeBoxId);
            case "NotifyReport" ->
                centralSystemService.handleNotifyReport(
                    objectMapper.readValue(payloadJson, NotifyReportRequest.class), chargeBoxId);
            case "NotifyEvent" ->
                centralSystemService.handleNotifyEvent(
                    objectMapper.readValue(payloadJson, NotifyEventRequest.class), chargeBoxId);
            case "FirmwareStatusNotification" ->
                centralSystemService.handleFirmwareStatusNotification(
                    objectMapper.readValue(payloadJson, FirmwareStatusNotificationRequest.class), chargeBoxId);
            case "LogStatusNotification" ->
                centralSystemService.handleLogStatusNotification(
                    objectMapper.readValue(payloadJson, LogStatusNotificationRequest.class), chargeBoxId);
            case "NotifyEVChargingNeeds" ->
                centralSystemService.handleNotifyEVChargingNeeds(
                    objectMapper.readValue(payloadJson, NotifyEVChargingNeedsRequest.class), chargeBoxId);
            case "ReportChargingProfiles" ->
                centralSystemService.handleReportChargingProfiles(
                    objectMapper.readValue(payloadJson, ReportChargingProfilesRequest.class), chargeBoxId);
            case "ReservationStatusUpdate" ->
                centralSystemService.handleReservationStatusUpdate(
                    objectMapper.readValue(payloadJson, ReservationStatusUpdateRequest.class), chargeBoxId);
            case "ClearedChargingLimit" ->
                centralSystemService.handleClearedChargingLimit(
                    objectMapper.readValue(payloadJson, ClearedChargingLimitRequest.class), chargeBoxId);
            case "NotifyChargingLimit" ->
                centralSystemService.handleNotifyChargingLimit(
                    objectMapper.readValue(payloadJson, NotifyChargingLimitRequest.class), chargeBoxId);
            case "NotifyCustomerInformation" ->
                centralSystemService.handleNotifyCustomerInformation(
                    objectMapper.readValue(payloadJson, NotifyCustomerInformationRequest.class), chargeBoxId);
            case "NotifyDisplayMessages" ->
                centralSystemService.handleNotifyDisplayMessages(
                    objectMapper.readValue(payloadJson, NotifyDisplayMessagesRequest.class), chargeBoxId);
            case "NotifyEVChargingSchedule" ->
                centralSystemService.handleNotifyEVChargingSchedule(
                    objectMapper.readValue(payloadJson, NotifyEVChargingScheduleRequest.class), chargeBoxId);
            case "NotifyMonitoringReport" ->
                centralSystemService.handleNotifyMonitoringReport(
                    objectMapper.readValue(payloadJson, NotifyMonitoringReportRequest.class), chargeBoxId);
            case "PublishFirmwareStatusNotification" ->
                centralSystemService.handlePublishFirmwareStatusNotification(
                    objectMapper.readValue(payloadJson, PublishFirmwareStatusNotificationRequest.class), chargeBoxId);
            case "DataTransfer" ->
                centralSystemService.handleDataTransfer(
                    objectMapper.readValue(payloadJson, DataTransferRequest.class), chargeBoxId);
            case "RequestStartTransaction" ->
                centralSystemService.handleRequestStartTransaction(
                    objectMapper.readValue(payloadJson, RequestStartTransactionRequest.class), chargeBoxId);
            case "RequestStopTransaction" ->
                centralSystemService.handleRequestStopTransaction(
                    objectMapper.readValue(payloadJson, RequestStopTransactionRequest.class), chargeBoxId);
            case "Get15118EVCertificate" ->
                centralSystemService.handleGet15118EVCertificate(
                    objectMapper.readValue(payloadJson, Get15118EVCertificateRequest.class), chargeBoxId);
            case "GetCertificateStatus" ->
                centralSystemService.handleGetCertificateStatus(
                    objectMapper.readValue(payloadJson, GetCertificateStatusRequest.class), chargeBoxId);
            default ->
                throw new IllegalArgumentException("Unsupported action: " + action);
        };
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

            // Check client certificate authentication if enabled
            if (authConfig.getMode() == Ocpp20AuthenticationConfig.AuthMode.CERTIFICATE ||
                authConfig.getMode() == Ocpp20AuthenticationConfig.AuthMode.COMBINED) {
                if (!authenticateWithCertificate(session, chargeBoxId)) {
                    if (authConfig.getMode() == Ocpp20AuthenticationConfig.AuthMode.CERTIFICATE) {
                        log.warn("Certificate authentication failed for '{}'", chargeBoxId);
                        return false;
                    }
                    // For COMBINED mode, fall back to basic auth
                }
            }

            // Check basic authentication if enabled
            if (authConfig.getMode() == Ocpp20AuthenticationConfig.AuthMode.BASIC ||
                authConfig.getMode() == Ocpp20AuthenticationConfig.AuthMode.COMBINED) {
                return authenticateWithBasicAuth(session, chargeBoxId);
            }

            // NONE mode or allow no auth
            if (authConfig.getMode() == Ocpp20AuthenticationConfig.AuthMode.NONE ||
                authConfig.isAllowNoAuth()) {
                log.debug("No authentication required for '{}'", chargeBoxId);
                return true;
            }

            return false;

        } catch (Exception e) {
            log.error("Authentication error for charge point '{}'", chargeBoxId, e);
            return false;
        }
    }

    private boolean authenticateWithCertificate(WebSocketSession session, String chargeBoxId) {
        try {
            // Get SSL session from WebSocket session attributes
            Object sslSessionObj = session.getAttributes().get("SSL_SESSION");
            if (!(sslSessionObj instanceof SSLSession)) {
                log.debug("No SSL session available for '{}'", chargeBoxId);
                return false;
            }

            SSLSession sslSession = (SSLSession) sslSessionObj;

            // Get peer certificates
            X509Certificate[] peerCerts;
            try {
                peerCerts = (X509Certificate[]) sslSession.getPeerCertificates();
            } catch (SSLPeerUnverifiedException e) {
                log.debug("No client certificate provided by '{}'", chargeBoxId);
                return false;
            }

            if (peerCerts == null || peerCerts.length == 0) {
                log.debug("No client certificates found for '{}'", chargeBoxId);
                return false;
            }

            X509Certificate clientCert = peerCerts[0];
            String subjectDN = clientCert.getSubjectDN().getName();

            // Validate certificate
            try {
                clientCert.checkValidity();
            } catch (Exception e) {
                log.warn("Client certificate expired or not yet valid for '{}': {}", chargeBoxId, subjectDN);
                return false;
            }

            // Check if certificate DN is trusted
            if (!authConfig.getTrustedCertificateDNs().isEmpty()) {
                if (!authConfig.getTrustedCertificateDNs().contains(subjectDN)) {
                    log.warn("Client certificate DN not trusted for '{}': {}", chargeBoxId, subjectDN);
                    return false;
                }
            }

            // Check if CN matches charge box ID
            String cn = extractCN(subjectDN);
            if (cn != null && !cn.equals(chargeBoxId)) {
                log.warn("Certificate CN '{}' does not match charge box ID '{}'", cn, chargeBoxId);
                return false;
            }

            log.info("Client certificate authentication successful for '{}' with DN: {}", chargeBoxId, subjectDN);
            return true;

        } catch (Exception e) {
            log.error("Certificate authentication error for '{}'", chargeBoxId, e);
            return false;
        }
    }

    private String extractCN(String dn) {
        String[] parts = dn.split(",");
        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.startsWith("CN=")) {
                return trimmed.substring(3);
            }
        }
        return null;
    }

    private boolean authenticateWithBasicAuth(WebSocketSession session, String chargeBoxId) {
        try {
            HttpHeaders headers = session.getHandshakeHeaders();
            List<String> authHeaders = headers.get(HttpHeaders.AUTHORIZATION);

            if (authHeaders == null || authHeaders.isEmpty()) {
                if (authConfig.isAllowNoAuth()) {
                    log.debug("No Authorization header from '{}', allowing (no auth allowed)", chargeBoxId);
                    return true;
                }
                log.debug("No Authorization header from '{}'", chargeBoxId);
                return false;
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
            log.error("Basic authentication error for charge point '{}'", chargeBoxId, e);
            return false;
        }
    }

    private String extractRemoteIP(WebSocketSession session) {
        try {
            InetSocketAddress remoteAddress = session.getRemoteAddress();
            if (remoteAddress != null) {
                String ip = remoteAddress.getAddress().getHostAddress();

                // Check for X-Forwarded-For header (proxy/load balancer)
                HttpHeaders headers = session.getHandshakeHeaders();
                List<String> xForwardedFor = headers.get("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                    String forwarded = xForwardedFor.get(0);
                    if (forwarded.contains(",")) {
                        // Take the first IP in the chain
                        ip = forwarded.split(",")[0].trim();
                    } else {
                        ip = forwarded.trim();
                    }
                }

                return ip;
            }
        } catch (Exception e) {
            log.error("Failed to extract remote IP", e);
        }
        return "unknown";
    }
}