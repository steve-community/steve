/*
 * Parkl Digital Technologies
 * Copyright (C) 2020-2021
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
package net.parkl.ocpp.util;

import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class AsyncWaiter<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncWaiter.class);

    private long delayMs = 100;
    private final long timeoutMs;
    private long intervalMs = 1000;
    private T value;
    private int count;

    public AsyncWaiter(long timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public T waitFor(Callable<T> callable) {
        Callable<Boolean> condition = () -> {
            count++;
            LOGGER.debug("Polling for value: #{}...", count);
            value = callable.call();
            return value != null;
        };
        try {
            Awaitility
                    .with().pollInSameThread()
                    .pollDelay(delayMs, TimeUnit.MILLISECONDS)
                    .and().pollInterval(intervalMs, TimeUnit.MILLISECONDS)
                    .atMost(timeoutMs, TimeUnit.MILLISECONDS).await().until(condition);
        } catch (ConditionTimeoutException ex) {
            LOGGER.info("Polling timeout after {} ms", timeoutMs);
        }
        return value;
    }

    public void setDelayMs(long delayMs) {
        this.delayMs = delayMs;
    }

    public void setIntervalMs(long intervalMs) {
        this.intervalMs = intervalMs;
    }
}
