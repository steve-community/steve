/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
 * Copyright (C) 2026 VoltStar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package com.voltstartev.steve.plugin.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voltstartev.steve.plugin.config.WebhookProperties;
import com.voltstartev.steve.plugin.events.SteveWebhookEvent;
import com.voltstartev.steve.plugin.retry.WebhookRetry;
import com.voltstartev.steve.plugin.retry.WebhookRetryQueue;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class WebhookSender {

    private final RestTemplate restTemplate;
    private final WebhookProperties properties;
    private final WebhookRetryQueue retryQueue;
    private final ObjectMapper objectMapper;
    private final MeterRegistry meterRegistry;
    
    // Metrics
    private final Counter sentCounter;
    private final Counter failedCounter;
    private final Timer durationTimer;

    public WebhookSender(RestTemplate restTemplate,
                        WebhookProperties properties,
                        WebhookRetryQueue retryQueue,
                        ObjectMapper objectMapper,
                        MeterRegistry meterRegistry) {
        this.restTemplate = restTemplate;
        this.properties = properties;
        this.retryQueue = retryQueue;
        this.objectMapper = objectMapper;
        this.meterRegistry = meterRegistry;
        
        // Initialize metrics
        this.sentCounter = Counter.builder("steve.webhook.sent")
            .tag("status", "success")
            .register(meterRegistry);
        this.failedCounter = Counter.builder("steve.webhook.sent")
            .tag("status", "failed")
            .register(meterRegistry);
        this.durationTimer = Timer.builder("steve.webhook.duration")
            .register(meterRegistry);
    }

    /**
     * Send webhook asynchronously (non-blocking)
     */
    @Async("webhookExecutor")
    public CompletableFuture<Void> sendAsync(String eventType, SteveWebhookEvent event) {
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            sendWebhook(eventType, event);
            sentCounter.increment();
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            failedCounter.increment();
            log.error(" Webhook send failed", e);
            
            // Queue for retry
            WebhookRetry retry = new WebhookRetry(
                eventType,
                event,
                0,
                System.currentTimeMillis() + properties.getRetryInitialDelayMs()
            );
            retryQueue.add(retry);
            
            return CompletableFuture.failedFuture(e);
        } finally {
            sample.stop(durationTimer.tag("event_type", eventType));
        }
    }

    /**
     * Core webhook sending logic
     */
    private void sendWebhook(String eventType, SteveWebhookEvent event) throws Exception {
        // Build payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("eventId", UUID.randomUUID().toString());
        payload.put("idempotencyKey", generateIdempotencyKey(eventType, event));
        payload.put("type", eventType);
        payload.put("timestamp", Instant.now().toString());  // Backend receipt time
        payload.put("chargeBoxId", event.getChargeBoxId());
        payload.put("connectorId", event.getConnectorId());
        payload.put("eventTimestamp", event.getEventTimestamp().toString());  // OCPP time
        payload.put("data", event);  // Full event details
        
        // Sign payload with HMAC-SHA256
        String signature = signPayload(payload, properties.getSecret());
        
        // Build HTTP request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Steve-Signature", signature);
        headers.set("X-Steve-Event-Type", eventType);
        headers.set("X-Steve-Event-Id", payload.get("eventId").toString());
        headers.set("X-Steve-Idempotency-Key", payload.get("idempotencyKey").toString());
        
        HttpEntity<String> request = new HttpEntity<>(
            objectMapper.writeValueAsString(payload),
            headers
        );
        
        // Send POST request
        restTemplate.postForEntity(properties.getUrl(), request, Void.class);
        
        log.debug("✅ Webhook sent: {} for {} (idempotency: {})", 
            eventType, event.getChargeBoxId(), payload.get("idempotencyKey"));
    }

    /**
     * Generate HMAC-SHA256 signature
     */
    private String signPayload(Object payload, String secret) throws Exception {
        if (secret == null || secret.isEmpty()) {
            return "";  // Skip signing if no secret configured
        }
        
        String json = objectMapper.writeValueAsString(payload);
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(
            secret.getBytes(StandardCharsets.UTF_8), 
            "HmacSHA256"
        );
        sha256_HMAC.init(secretKey);
        byte[] hash = sha256_HMAC.doFinal(json.getBytes(StandardCharsets.UTF_8));
        
        // Convert to hex
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Generate deterministic idempotency key
     */
    private String generateIdempotencyKey(String eventType, SteveWebhookEvent event) {
        return String.format("%s:%s:%d:%d",
            eventType,
            event.getChargeBoxId(),
            event.getConnectorId() != null ? event.getConnectorId() : -1,
            event.getEventTimestamp().toEpochMilli()
        );
    }
}
