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
