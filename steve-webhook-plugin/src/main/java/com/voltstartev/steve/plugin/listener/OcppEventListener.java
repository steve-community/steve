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
package com.voltstartev.steve.plugin.listener;

import com.voltstartev.steve.plugin.events.*;
import com.voltstartev.steve.plugin.service.WebhookSender;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.repository.dto.ConnectorStatusUpdate;
import de.rwth.idsg.steve.repository.dto.TransactionStart;
import de.rwth.idsg.steve.repository.dto.TransactionStop;
import de.rwth.idsg.steve.service.dto.EnhancedMeterValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OcppEventListener {

    private final WebhookSender webhookSender;
    private final WebhookProperties properties;

    public OcppEventListener(WebhookSender webhookSender, WebhookProperties properties) {
        this.webhookSender = webhookSender;
        this.properties = properties;
    }

    /**
     * Handle StatusNotification (connector status change)
     */
    @EventListener
    public void onConnectorStatus(ConnectorStatusUpdate event) {
        ConnectorStatusEvent webhookEvent = new ConnectorStatusEvent(
            event.getChargeBoxId(),
            event.getConnectorId(),
            event.getStatus().getValue(),
            event.getErrorCode() != null ? event.getErrorCode().getValue() : null,
            event.getInfo(),
            event.getTimestamp(),  // OCPP timestamp
            Instant.now()          // Received at SteVe
        );
        
        webhookSender.sendAsync("connector.status", webhookEvent);
    }

    /**
     * Handle StartTransaction
     */
    @EventListener
    public void onTransactionStart(TransactionStart event) {
        TransactionStartedEvent webhookEvent = new TransactionStartedEvent(
            event.getChargeBoxId(),
            event.getConnectorId(),
            event.getTransactionId(),
            event.getIdTag(),
            event.getStartMeterValue(),
            event.getStartTimestamp(),
            Instant.now(),
            event.getReservationId()
        );
        
        webhookSender.sendAsync("transaction.started", webhookEvent);
    }

    /**
     * Handle StopTransaction
     */
    @EventListener
    public void onTransactionStop(TransactionStop event) {
        // Convert transaction data if present
        List<TransactionStoppedEvent.MeterValue> transactionData = null;
        if (event.getTransactionData() != null && !event.getTransactionData().isEmpty()) {
            transactionData = event.getTransactionData().stream()
                .map(td -> new TransactionStoppedEvent.MeterValue(
                    td.getTimestamp(),
                    td.getSampledValue().stream()
                        .map(sv -> new TransactionStoppedEvent.SampledValue(
                            sv.getValue(),
                            sv.getContext(),
                            sv.getFormat(),
                            sv.getMeasurand(),
                            sv.getPhase(),
                            sv.getLocation(),
                            sv.getUnit()
                        ))
                        .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
        }
        
        TransactionStoppedEvent webhookEvent = new TransactionStoppedEvent(
            event.getChargeBoxId(),
            event.getConnectorId(),
            event.getTransactionId(),
            event.getIdTag(),
            event.getStopMeterValue(),
            event.getStopTimestamp(),
            Instant.now(),
            event.getReason() != null ? event.getReason().getValue() : null,
            transactionData
        );
        
        webhookSender.sendAsync("transaction.stopped", webhookEvent);
    }

    /**
     * Handle MeterValues with smart filtering
     */
    @EventListener
    public void onMeterValues(EnhancedMeterValue event) {
        // Skip if filtering is disabled
        if (!properties.isMeterValuesSamplingEnabled()) {
            sendMeterValuesEvent(event);
            return;
        }
        
        // Apply smart filtering: only send if significant change or time threshold
        if (shouldSendMeterValues(event)) {
            sendMeterValuesEvent(event);
        } else {
            log.debug(" MeterValues filtered (no significant change): {}:{} (tx={})",
                event.getChargeBoxId(), event.getConnectorId(), event.getTransactionId());
        }
    }
    
    private boolean shouldSendMeterValues(EnhancedMeterValue event) {
        // For now, use simple time-based filtering
        // In production, you'd track last sent time/energy per connector in a cache
        return event.getSampledValue().stream()
            .anyMatch(sv -> "Energy.Active.Import.Register".equals(sv.getMeasurand()))
            && event.getTimestamp().toEpochMilli() % (properties.getMeterValuesMinIntervalSeconds() * 1000) < 5000;
    }
    
    private void sendMeterValuesEvent(EnhancedMeterValue event) {
        List<MeterValuesEvent.MeterValue> meterValues = List.of(
            new MeterValuesEvent.MeterValue(
                event.getTimestamp(),
                event.getSampledValue().stream()
                    .map(sv -> new MeterValuesEvent.SampledValue(
                        sv.getValue(),
                        sv.getContext(),
                        sv.getFormat(),
                        sv.getMeasurand(),
                        sv.getPhase(),
                        sv.getLocation(),
                        sv.getUnit()
                    ))
                    .collect(Collectors.toList())
            )
        );
        
        MeterValuesEvent webhookEvent = new MeterValuesEvent(
            event.getChargeBoxId(),
            event.getConnectorId(),
            event.getTransactionId(),
            meterValues,
            event.getTimestamp(),
            Instant.now()
        );
        
        webhookSender.sendAsync("meter.values", webhookEvent);
    }
}
