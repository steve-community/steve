package net.parkl.ocpp.service.cs.cleanup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.OcppChargingProcess;
import net.parkl.ocpp.entities.Transaction;
import net.parkl.ocpp.module.esp.EmobilityServiceProvider;
import net.parkl.ocpp.module.esp.model.ESPChargingProcessCheckResult;
import net.parkl.ocpp.service.ChargingProcessService;
import net.parkl.ocpp.util.CalendarUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionCleanupManager {
    private static final long HOURS_IN_MS = 60*60*1000;
    @Value("${ocpp.transaction.cleanup.check.threshold.hrs:1}")
    private int cleanupCheckThresholdHours;
    @Value("${ocpp.transaction.cleanup.nonexistent.threshold.hrs:2}")
    private int cleanupNonExistentThresholdHours;
    @Value("${ocpp.transaction.cleanup.stopped.threshold.hrs:1}")
    private int cleanupStoppedThresholdHours;
    @Value("${ocpp.consumption.threshold.days:30}")
    private int cleanupConsumptionThresholdDays;

    private final TransactionCleanupService cleanupService;
    private final ChargingProcessService chargingProcessService;
    private final EmobilityServiceProvider emobilityServiceProvider;

    public int cleanupTransactions() {
        Date cleanupCheckThreshold = getCleanupCheckThreshold();
        log.debug("Checking transactions for cleanup before: {}...", cleanupCheckThreshold);
        List<Transaction> transactions = cleanupService.getTransactionsForCleanup(cleanupCheckThreshold);
        int cleanedUp = 0;

        if (!transactions.isEmpty()) {
            log.info("Found {} possible transactions for cleanup (before {})...", transactions.size(),
                    cleanupCheckThreshold);


            for (Transaction transaction : transactions) {
                log.info("Checking transaction {} for cleanup...", transaction.getTransactionPk());
                boolean cleanup = checkTransactionForCleanup(transaction);

                if (cleanup && cleanupService.cleanupTransaction(transaction.getTransactionPk())) {
                    cleanedUp++;
                }
            }

            log.info("Transaction cleanup completed: {} of {} transactions", cleanedUp, transactions.size());
        }

        int deleted = chargingProcessService.cleanupWithoutTransaction();
        if (deleted > 0) {
            log.info("Cleaned up {} charging processes without transaction", deleted);
        }

        return cleanedUp+deleted;
    }

    private boolean checkTransactionForCleanup(Transaction transaction) {
        OcppChargingProcess chargingProcess = chargingProcessService.findByTransactionId(transaction.getTransactionPk());
        boolean cleanup = false;
        if (chargingProcess != null) {
            ESPChargingProcessCheckResult checkResult =
                    emobilityServiceProvider.checkChargingProcess(chargingProcess.getOcppChargingProcessId());
            if (!checkResult.isExists()) {
                log.info("Charging process {} does not exist at the ESP, transaction id={}",
                        chargingProcess.getOcppChargingProcessId(), transaction.getTransactionPk());
                cleanup = checkNonExistentThreshold(transaction.getStartEventTimestamp());
            } else if (checkResult.isStopped()) {
                log.info("Charging process {} already stopped at the ESP, transaction id={}",
                        chargingProcess.getOcppChargingProcessId(), transaction.getTransactionPk());
                cleanup = checkStoppedThreshold(checkResult.getTimeElapsedSinceStop());
            } else {
                log.info("Charging process {} still running at the ESP, transaction id={}",
                        chargingProcess.getOcppChargingProcessId(), transaction.getTransactionPk());
            }
        } else {
            log.info("Charging process was null for transaction: {}", transaction.getTransactionPk());
            cleanup = checkNonExistentThreshold(transaction.getStartEventTimestamp());
        }
        return cleanup;
    }

    private Date getCleanupCheckThreshold() {
        return new Date(System.currentTimeMillis()-cleanupCheckThresholdHours * HOURS_IN_MS);
    }

    private boolean checkStoppedThreshold(long timePassedSinceStop) {
        log.info("Checking stopped threshold: {} hours before now...", cleanupStoppedThresholdHours);
        return timePassedSinceStop > cleanupStoppedThresholdHours * HOURS_IN_MS;
    }

    private boolean checkNonExistentThreshold(Date startDate) {
        log.info("Checking non-existent threshold with start date {}: {} hours before now...", startDate, cleanupNonExistentThresholdHours);
        return startDate.getTime() < System.currentTimeMillis()-cleanupNonExistentThresholdHours * HOURS_IN_MS;
    }

    public int cleanupConsumptionStates() {
        Date date = CalendarUtils.createDaysBeforeNow(cleanupConsumptionThresholdDays);
        log.info("Cleaning up consumption states older than {}...", date);
        return cleanupService.cleanupConsumptionStates(date);
    }
}
