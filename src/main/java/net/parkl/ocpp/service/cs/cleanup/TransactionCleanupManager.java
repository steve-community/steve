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


    private final TransactionCleanupService cleanupService;
    private final ChargingProcessService chargingProcessService;
    private final EmobilityServiceProviderChecker emobilityServiceProviderChecker;
    private final AutomaticCleanupChecker automaticCleanupChecker;
    private final TransactionCleanupConfig config;


    public int cleanupTransactions() {
        Date cleanupCheckThreshold = config.getCleanupCheckThreshold();
        log.debug("Checking transactions for cleanup before: {}...", cleanupCheckThreshold);
        List<Transaction> transactions = cleanupService.getTransactionsForCleanup(cleanupCheckThreshold);


        int cleanedUp = cleanupTransactionsWithCheck(transactions, automaticCleanupChecker);

        List<OcppChargingProcess> processes = chargingProcessService.findWithoutTransactionForCleanup(
                config.getCleanupNoTransactionThresholdHours());
        int deleted = cleanupChargingProcessesWithoutTransaction(processes);

        return cleanedUp+deleted;
    }

    int cleanupChargingProcessesWithoutTransaction(List<OcppChargingProcess> processes) {
        int deleted=0;
        if (!processes.isEmpty()) {
            log.info("Found {} charging processes without transaction for cleanup...", processes.size());
            for (OcppChargingProcess process : processes) {
                log.info("Checking process {} for cleanup...", process.getOcppChargingProcessId());
                boolean cleanup = emobilityServiceProviderChecker.checkEmobilityServiceProvider(null, process);

                if (cleanup) {
                    chargingProcessService.deleteByChargingProcessId(process.getOcppChargingProcessId());
                    deleted++;
                }
            }
        }
        if (deleted > 0) {
            log.info("Cleaned up {} charging processes without transaction", deleted);
        }
        return deleted;
    }

    int cleanupTransactionsWithCheck(List<Transaction> transactions, TransactionCleanupChecker checker) {
        int cleanedUp = 0;
        if (!transactions.isEmpty()) {
            log.info("Checking {} possible transactions for cleanup...", transactions.size());


            for (Transaction transaction : transactions) {
                log.info("Checking transaction {} for cleanup...", transaction.getTransactionPk());
                boolean cleanup = checker.checkTransactionForCleanup(transaction);

                if (cleanup && cleanupService.cleanupTransaction(transaction.getTransactionPk())) {
                    cleanedUp++;
                }
            }

            log.info("Transaction cleanup completed: {} of {} transactions", cleanedUp, transactions.size());
        }
        return cleanedUp;
    }







    public int cleanupConsumptionStates() {
        Date date = CalendarUtils.createDaysBeforeNow(config.getCleanupConsumptionThresholdDays());
        log.info("Cleaning up consumption states older than {}...", date);
        return cleanupService.cleanupConsumptionStates(date);
    }
}
