package net.parkl.ocpp.service.cs.cleanup;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

@Component
@Slf4j
public class TransactionCleanupConfig {
    static final long HOURS_IN_MS = 60*60*1000L;
    @Value("${ocpp.transaction.cleanup.check.threshold.hrs:1}")
    @Getter
    private int cleanupCheckThresholdHours;
    @Value("${ocpp.transaction.cleanup.nonexistent.threshold.hrs:2}")
    @Getter
    private int cleanupNonExistentThresholdHours;

    @Value("${ocpp.transaction.cleanup.no-transaction.threshold.hrs:24}")
    @Getter
    private int cleanupNoTransactionThresholdHours;

    @Value("${ocpp.transaction.cleanup.stopped.threshold.hrs:1}")
    @Getter
    private int cleanupStoppedThresholdHours;
    @Value("${ocpp.consumption.threshold.days:30}")
    @Getter
    private int cleanupConsumptionThresholdDays;

    public Date getCleanupCheckThreshold() {
        return new Date(System.currentTimeMillis()-cleanupCheckThresholdHours * HOURS_IN_MS);
    }
    public boolean checkNonExistentThreshold(LocalDateTime startDate) {
        log.info("Checking non-existent threshold with start date {}: {} hours before now...", startDate,
                cleanupNonExistentThresholdHours);
        return startDate.isBefore(LocalDateTime.now().minusHours(cleanupNonExistentThresholdHours));
    }
}
