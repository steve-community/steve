package net.parkl.ocpp.service.cs.cleanup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TransactionCleanupJob {
    private final TransactionCleanupManager cleanupManager;


    public void checkTransactionsForCleanup() {
        log.debug("Transactions cleanup started...");
        long start = System.currentTimeMillis();
        int cleanedUp = cleanupManager.cleanupTransactions();
        if (cleanedUp > 0) {
            log.info("Transactions cleanup completed in {} ms, {} transactions cleaned up", System.currentTimeMillis()-start, cleanedUp);
        } else {
            log.debug("Transactions cleanup completed in {} ms", System.currentTimeMillis() - start);
        }

        start = System.currentTimeMillis();
        int consumptionsCleanedUp = cleanupManager.cleanupConsumptionStates();
        if (consumptionsCleanedUp > 0) {
            log.info("Consumption state cleanup completed in {} ms, {} transactions cleaned up", System.currentTimeMillis()-start, consumptionsCleanedUp);
        } else {
            log.debug("Consumption state cleanup completed in {} ms", System.currentTimeMillis() - start);
        }
    }
}
