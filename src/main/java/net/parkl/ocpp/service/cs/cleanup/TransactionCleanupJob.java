package net.parkl.ocpp.service.cs.cleanup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TransactionCleanupJob {
    private final TransactionCleanupManager cleanupManager;

    @Scheduled(fixedRate = 300000)
    public void checkTransactionsForCleanup() {
        log.info("Transactions cleanup started...");
        long start = System.currentTimeMillis();
        cleanupManager.cleanupTransactions();
        log.info("Transactions cleanup completed in {} ms", System.currentTimeMillis()-start);
    }
}
