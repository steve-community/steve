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

import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class Ocpp20RateLimiter {

    private static final int MAX_REQUESTS_PER_MINUTE = 60;
    private static final int MAX_REQUESTS_PER_HOUR = 1000;
    private static final long CLEANUP_INTERVAL_MINUTES = 5;

    private final Map<String, RateLimitBucket> buckets = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public Ocpp20RateLimiter() {
        scheduler.scheduleAtFixedRate(
            this::cleanupExpiredBuckets,
            CLEANUP_INTERVAL_MINUTES,
            CLEANUP_INTERVAL_MINUTES,
            TimeUnit.MINUTES
        );
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down OCPP 2.0 rate limiter...");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
                log.warn("Rate limiter scheduler forced shutdown");
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
            log.error("Rate limiter shutdown interrupted", e);
        }
    }

    public boolean allowRequest(String chargeBoxId) {
        RateLimitBucket bucket = buckets.computeIfAbsent(chargeBoxId, k -> new RateLimitBucket());
        return bucket.tryConsume();
    }

    public void reset(String chargeBoxId) {
        buckets.remove(chargeBoxId);
    }

    private void cleanupExpiredBuckets() {
        Instant oneHourAgo = Instant.now().minusSeconds(3600);
        buckets.entrySet().removeIf(entry -> {
            RateLimitBucket bucket = entry.getValue();
            return bucket.getLastAccessTime().isBefore(oneHourAgo) && bucket.getRequestCount().get() == 0;
        });
        log.debug("Rate limiter cleanup: {} active charge points", buckets.size());
    }

    @Getter
    private static class RateLimitBucket {
        private final AtomicLong requestCount = new AtomicLong(0);
        private volatile Instant minuteWindowStart = Instant.now();
        private volatile Instant hourWindowStart = Instant.now();
        private volatile Instant lastAccessTime = Instant.now();
        private final AtomicLong minuteRequests = new AtomicLong(0);
        private final AtomicLong hourRequests = new AtomicLong(0);

        public synchronized boolean tryConsume() {
            Instant now = Instant.now();
            lastAccessTime = now;

            if (now.isAfter(minuteWindowStart.plusSeconds(60))) {
                minuteWindowStart = now;
                minuteRequests.set(0);
            }

            if (now.isAfter(hourWindowStart.plusSeconds(3600))) {
                hourWindowStart = now;
                hourRequests.set(0);
            }

            long minuteCount = minuteRequests.incrementAndGet();
            long hourCount = hourRequests.incrementAndGet();

            if (minuteCount > MAX_REQUESTS_PER_MINUTE) {
                minuteRequests.decrementAndGet();
                hourRequests.decrementAndGet();
                return false;
            }

            if (hourCount > MAX_REQUESTS_PER_HOUR) {
                minuteRequests.decrementAndGet();
                hourRequests.decrementAndGet();
                return false;
            }

            requestCount.incrementAndGet();
            return true;
        }
    }
}