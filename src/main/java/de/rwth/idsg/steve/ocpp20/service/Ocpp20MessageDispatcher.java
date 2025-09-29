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
package de.rwth.idsg.steve.ocpp20.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ocpp.v20.enabled", havingValue = "true")
public class Ocpp20MessageDispatcher {

    @Qualifier("ocpp20ObjectMapper")
    private final ObjectMapper objectMapper;
    private final Map<String, PendingRequest<?>> pendingRequests = new ConcurrentHashMap<>();
    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    private static final long DEFAULT_TIMEOUT_SECONDS = 30;

    public void registerSession(String chargeBoxId, WebSocketSession session) {
        sessionMap.put(chargeBoxId, session);
        log.debug("Registered WebSocket session for '{}'", chargeBoxId);
    }

    public void unregisterSession(String chargeBoxId) {
        sessionMap.remove(chargeBoxId);
        log.debug("Unregistered WebSocket session for '{}'", chargeBoxId);
    }

    public <REQ, RES> CompletableFuture<RES> sendCall(
            String chargeBoxId,
            String action,
            REQ request,
            Class<RES> responseClass) {

        WebSocketSession session = sessionMap.get(chargeBoxId);
        if (session == null || !session.isOpen()) {
            CompletableFuture<RES> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalStateException(
                "No active WebSocket session for charge box: " + chargeBoxId));
            return future;
        }

        String messageId = UUID.randomUUID().toString();
        CompletableFuture<RES> future = new CompletableFuture<>();

        PendingRequest<RES> pendingRequest = new PendingRequest<>(
            chargeBoxId, action, messageId, responseClass, future
        );
        pendingRequests.put(messageId, pendingRequest);

        future.orTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .whenComplete((result, error) -> {
                if (error != null) {
                    log.warn("Request timeout or error for '{}' action '{}': {}",
                        chargeBoxId, action, error.getMessage());
                }
                pendingRequests.remove(messageId);
            });

        try {
            List<Object> callMessage = List.of(
                2,
                messageId,
                action,
                request
            );

            String json = objectMapper.writeValueAsString(callMessage);
            log.info("OCPP 2.0 sending Call to '{}': action={}, messageId={}",
                chargeBoxId, action, messageId);
            log.debug("Call payload: {}", json);

            session.sendMessage(new TextMessage(json));

        } catch (Exception e) {
            log.error("Error sending OCPP 2.0 Call to '{}'", chargeBoxId, e);
            pendingRequests.remove(messageId);
            future.completeExceptionally(e);
        }

        return future;
    }

    @SuppressWarnings("unchecked")
    public void handleCallResult(String messageId, Object payload) {
        PendingRequest<?> pendingRequest = pendingRequests.get(messageId);

        if (pendingRequest == null) {
            log.warn("Received CallResult for unknown or timed-out messageId: {}", messageId);
            return;
        }

        try {
            Object response = objectMapper.convertValue(payload, pendingRequest.responseClass);
            log.info("OCPP 2.0 received CallResult for '{}': action={}, messageId={}",
                pendingRequest.chargeBoxId, pendingRequest.action, messageId);
            log.debug("CallResult payload: {}", payload);

            if (((CompletableFuture<Object>) pendingRequest.future).complete(response)) {
                pendingRequests.remove(messageId);
            } else {
                log.debug("CallResult arrived after timeout for messageId: {}", messageId);
                pendingRequests.remove(messageId);
            }

        } catch (Exception e) {
            log.error("Error processing CallResult for messageId '{}'", messageId, e);
            pendingRequest.future.completeExceptionally(e);
            pendingRequests.remove(messageId);
        }
    }

    public void handleCallError(String messageId, String errorCode, String errorDescription, Object errorDetails) {
        PendingRequest<?> pendingRequest = pendingRequests.get(messageId);

        if (pendingRequest == null) {
            log.warn("Received CallError for unknown or timed-out messageId: {}", messageId);
            return;
        }

        log.warn("OCPP 2.0 received CallError for '{}': action={}, messageId={}, errorCode={}, description={}",
            pendingRequest.chargeBoxId, pendingRequest.action, messageId, errorCode, errorDescription);

        String errorMessage = String.format("[%s] %s", errorCode, errorDescription);
        if (pendingRequest.future.completeExceptionally(new RuntimeException(errorMessage))) {
            pendingRequests.remove(messageId);
        } else {
            log.debug("CallError arrived after timeout for messageId: {}", messageId);
            pendingRequests.remove(messageId);
        }
    }

    public boolean hasActiveSession(String chargeBoxId) {
        WebSocketSession session = sessionMap.get(chargeBoxId);
        return session != null && session.isOpen();
    }

    public int getPendingRequestCount() {
        return pendingRequests.size();
    }

    private static class PendingRequest<RES> {
        final String chargeBoxId;
        final String action;
        final String messageId;
        final Class<RES> responseClass;
        final CompletableFuture<RES> future;

        PendingRequest(String chargeBoxId, String action, String messageId,
                      Class<RES> responseClass, CompletableFuture<RES> future) {
            this.chargeBoxId = chargeBoxId;
            this.action = action;
            this.messageId = messageId;
            this.responseClass = responseClass;
            this.future = future;
        }
    }
}