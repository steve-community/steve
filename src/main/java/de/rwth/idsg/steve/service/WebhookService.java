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
package de.rwth.idsg.steve.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.rwth.idsg.steve.service.notification.OcppMeterValues;
import de.rwth.idsg.steve.service.notification.OcppTransactionEnded;
import de.rwth.idsg.steve.service.notification.OcppTransactionStarted;
import de.rwth.idsg.steve.service.notification.OcppConnectorStatus;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2015._10.MeterValue;
import ocpp.cs._2015._10.SampledValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Listens to SteVe's internal Spring application events and forwards
 * them as HMAC-signed HTTP webhooks to the VoltStartEV backend.
 *
 * All listener methods are @Async — they run on a separate thread pool
 * and never block SteVe's OCPP message processing thread.
 *
 * Webhook is silently disabled when voltstartev.webhook.url is empty,
 * making this safe for environments that don't configure it.
 */
@Slf4j
@Service
public class WebhookService {

    @Value("${voltstartev.webhook.url:}")
    private String webhookUrl;

    @Value("${voltstartev.webhook.secret:}")
    private String webhookSecret;

    private final ObjectMapper objectMapper = new ObjectMapper()
        .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    // Java 11 built-in HTTP client — no extra Maven dependency needed
    private final HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(3))
        .build();

    // -------------------------------------------------------------------------
    // Event Listeners — one per OCPP event type we care about
    // -------------------------------------------------------------------------

    @Async
    @EventListener
    public void onTransactionStarted(OcppTransactionStarted event) {
        // InsertTransactionParams fields confirmed from source:
        // chargeBoxId, connectorId, idTag, startTimestamp, startMeterValue
        Map<String, Object> payload = new HashMap<>();
        payload.put("chargeBoxId",   event.getParams().getChargeBoxId());
        payload.put("connectorId",   event.getParams().getConnectorId());
        payload.put("transactionId", event.getTransactionId());
        payload.put("idTag",         event.getParams().getIdTag());
        payload.put("meterStart",    event.getParams().getStartMeterValue());
        payload.put("startTime",     event.getParams().getStartTimestamp() != null
                                     ? event.getParams().getStartTimestamp().toString()
                                     : null);
        send("OcppTransactionStarted", payload);
    }

    @Async
    @EventListener
    public void onTransactionEnded(OcppTransactionEnded event) {
        // UpdateTransactionParams fields confirmed from source:
        // chargeBoxId, transactionId, stopTimestamp, stopMeterValue, stopReason
        Map<String, Object> payload = new HashMap<>();
        payload.put("chargeBoxId",   event.getParams().getChargeBoxId());
        payload.put("transactionId", event.getParams().getTransactionId());
        payload.put("meterStop",     event.getParams().getStopMeterValue());
        payload.put("stopReason",    event.getParams().getStopReason());
        payload.put("stopTime",      event.getParams().getStopTimestamp() != null
                                     ? event.getParams().getStopTimestamp().toString()
                                     : null);
        send("OcppTransactionEnded", payload);
    }

    @Async
    @EventListener
    public void onMeterValues(OcppMeterValues event) {
        // Skip meter values not tied to a transaction —
        // they have no billing context and we don't need them
        if (event.getTransactionId() == null) {
            return;
        }

        List<Map<String, Object>> sampledValues = flattenMeterValues(event.getMeterValues());
        if (sampledValues.isEmpty()) return;

        Map<String, Object> payload = new HashMap<>();
        payload.put("chargeBoxId",   event.getChargeBoxId());
        payload.put("connectorId",   event.getConnectorId());
        payload.put("transactionId", event.getTransactionId());
        payload.put("sampledValues", sampledValues);
        send("OcppMeterValues", payload);
    }

    @Async
    @EventListener
    public void onConnectorStatus(OcppConnectorStatus event) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("chargeBoxId", event.getChargeBoxId());
        payload.put("connectorId", event.getConnectorId());
        payload.put("status", event.getStatus());
        payload.put("errorCode", event.getErrorCode());
        payload.put("info", event.getInfo());
        payload.put("vendorId", event.getVendorId());
        payload.put("vendorErrorCode", event.getVendorErrorCode());
        payload.put("timestamp", event.getTimestamp() != null 
            ? event.getTimestamp().toString() 
            : null);
        
        send("OcppConnectorStatus", payload);
    }

    // -------------------------------------------------------------------------
    // Core send logic — sign and POST
    // -------------------------------------------------------------------------

    private void send(String eventType, Map<String, Object> payload) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            return; // Silently disabled — no URL configured
        }
        if (webhookSecret == null || webhookSecret.isBlank()) {
            log.warn("voltstartev.webhook.secret not configured — skipping {}", eventType);
            return;
        }

        try {
            payload.put("eventType", eventType);
            payload.put("eventId",   buildEventId(eventType, payload));
            payload.put("timestamp", Instant.now().toString());

            String json      = objectMapper.writeValueAsString(payload);
            String signature = sign(json);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(webhookUrl))
                .timeout(Duration.ofSeconds(5))
                .header("Content-Type",       "application/json")
                .header("X-Signature",        signature)
                .header("X-Event-Id",         (String) payload.get("eventId"))
                .header("X-Steve-Event-Type", eventType)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

            HttpResponse<String> response = httpClient.send(
                request, HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                log.debug("Webhook OK: {} → HTTP {}", eventType, response.statusCode());
            } else {
                log.warn("Webhook rejected: {} → HTTP {} | body={}",
                    eventType, response.statusCode(), response.body());
            }

        } catch (Exception e) {
            // Never throw — this must not affect SteVe's OCPP processing
            log.error("Webhook send failed [{}]: {}", eventType, e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Flatten OCPP MeterValue list into a simple list of sampled value maps.
     * Keeps only the latest reading per measurand+phase combination.
     */
    private List<Map<String, Object>> flattenMeterValues(List<MeterValue> meterValues) {
        // key = measurand:phase, value = latest sampled entry
        Map<String, Map<String, Object>> latest = new HashMap<>();

        for (MeterValue mv : meterValues) {
            if (mv.getSampledValue() == null) continue;
            for (SampledValue sv : mv.getSampledValue()) {

                String measurand = sv.isSetMeasurand()
                    ? sv.getMeasurand().value()
                    : "Energy.Active.Import.Register"; // OCPP default when omitted

                String phase = sv.isSetPhase() ? sv.getPhase().value() : null;
                String key   = measurand + (phase != null ? ":" + phase : "");

                Map<String, Object> entry = new HashMap<>();
                entry.put("measurand", measurand);
                entry.put("value",     sv.getValue());
                entry.put("unit",      sv.isSetUnit()    ? sv.getUnit().value()    : null);
                entry.put("phase",     sv.isSetPhase()   ? sv.getPhase().value()   : null);
                entry.put("context",   sv.isSetContext() ? sv.getContext().value() : null);

                latest.put(key, entry); // last-write wins → keeps latest timestamp
            }
        }

        return new ArrayList<>(latest.values());
    }

    private String sign(String json) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(
            webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"
        ));
        byte[] hash = mac.doFinal(json.getBytes(StandardCharsets.UTF_8));
        // Hex encode without extra dependency
        StringBuilder sb = new StringBuilder(hash.length * 2);
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    /**
     * Deterministic event ID — same physical event always produces the same ID.
     * This lets the Node.js backend deduplicate retries using a simple DB lookup.
     * Format: eventType:chargeBoxId:connectorId:transactionId
     */
    private String buildEventId(String eventType, Map<String, Object> payload) {
        return eventType
            + ":" + payload.getOrDefault("chargeBoxId",   "x")
            + ":" + payload.getOrDefault("connectorId",   "0")
            + ":" + payload.getOrDefault("transactionId", Instant.now().toEpochMilli());
    }
}
