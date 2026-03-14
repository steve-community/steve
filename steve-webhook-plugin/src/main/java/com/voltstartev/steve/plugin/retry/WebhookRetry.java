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
package com.voltstartev.steve.plugin.retry;

import com.voltstartev.steve.plugin.events.SteveWebhookEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebhookRetry {
    private String eventType;
    private SteveWebhookEvent event;
    private int attempt;
    private long nextRetryAt;  // Epoch millis
    
    /**
     * Generate deterministic idempotency key for deduplication
     */
    public String getIdempotencyKey() {
        return String.format("%s:%s:%d:%d",
            eventType,
            event.getChargeBoxId(),
            event.getConnectorId() != null ? event.getConnectorId() : -1,
            event.getEventTimestamp().toEpochMilli()
        );
    }
    
    public void incrementAttempt(long baseDelayMs) {
        this.attempt++;
        // Exponential backoff: baseDelay * 2^(attempt-1)
        long delay = (long) (baseDelayMs * Math.pow(2, attempt - 1));
        this.nextRetryAt = System.currentTimeMillis() + delay;
    }
    
    public boolean isReadyForRetry() {
        return System.currentTimeMillis() >= nextRetryAt;
    }
}
