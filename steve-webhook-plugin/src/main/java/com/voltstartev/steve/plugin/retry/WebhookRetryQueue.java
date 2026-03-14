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

import com.voltstartev.steve.plugin.service.WebhookSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Component
public class WebhookRetryQueue {

    private final BlockingQueue<WebhookRetry> retryQueue = new LinkedBlockingQueue<>(1000);
    private final WebhookSender webhookSender;
    private final WebhookProperties properties;

    public WebhookRetryQueue(WebhookSender webhookSender, WebhookProperties properties) {
        this.webhookSender = webhookSender;
        this.properties = properties;
    }

    /**
     * Add event to retry queue
     */
    public void add(WebhookRetry retry) {
        if (!retryQueue.offer(retry)) {
            log.warn(" Retry queue full, dropping event: {}", retry.getIdempotencyKey());
        } else {
            log.debug(" Queued for retry: {} (attempt {}/{})", 
                retry.getIdempotencyKey(), retry.getAttempt(), properties.getRetryMaxAttempts());
        }
    }

    /**
     * Process retries every 5 seconds
     */
    @Scheduled(fixedDelay = 5000)
    public void processRetries() {
        List<WebhookRetry> batch = new ArrayList<>();
        retryQueue.drainTo(batch, 10);  // Process up to 10 at a time
        
        for (WebhookRetry retry : batch) {
            try {
                if (!retry.isReadyForRetry()) {
                    // Not ready yet, re-queue
                    retryQueue.offer(retry);
                    continue;
                }
                
                if (retry.getAttempt() >= properties.getRetryMaxAttempts()) {
                    log.error(" Max retries exceeded, dropping event: {}", retry.getIdempotencyKey());
                    continue;
                }
                
                // Attempt resend
                webhookSender.sendAsync(retry.getEventType(), retry.getEvent())
                    .exceptionally(ex -> {
                        log.warn(" Retry attempt {} failed for {}: {}", 
                            retry.getAttempt() + 1, 
                            retry.getIdempotencyKey(), 
                            ex.getMessage());
                        
                        // Re-queue with increased delay
                        retry.incrementAttempt(properties.getRetryInitialDelayMs());
                        retryQueue.offer(retry);
                        return null;
                    });
                    
            } catch (Exception e) {
                log.error("❌ Error processing retry", e);
                retry.incrementAttempt(properties.getRetryInitialDelayMs());
                retryQueue.offer(retry);
            }
        }
    }
    
    public int getQueueSize() {
        return retryQueue.size();
    }
}
