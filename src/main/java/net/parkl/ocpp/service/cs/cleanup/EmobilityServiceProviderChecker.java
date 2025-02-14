package net.parkl.ocpp.service.cs.cleanup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.OcppChargingProcess;
import net.parkl.ocpp.entities.Transaction;
import net.parkl.ocpp.module.esp.EmobilityServiceProvider;
import net.parkl.ocpp.module.esp.model.ESPChargingProcessCheckResult;
import org.springframework.stereotype.Component;


import static net.parkl.ocpp.service.cs.cleanup.TransactionCleanupConfig.HOURS_IN_MS;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmobilityServiceProviderChecker {
    private final EmobilityServiceProvider emobilityServiceProvider;
    private final TransactionCleanupConfig config;

    public boolean checkEmobilityServiceProvider(Transaction transaction, OcppChargingProcess chargingProcess) {
        boolean cleanup = false;
        ESPChargingProcessCheckResult checkResult =
                emobilityServiceProvider.checkChargingProcess(chargingProcess.getOcppChargingProcessId());
        if (!checkResult.isExists()) {
            log.info("Charging process {} does not exist at the ESP, transaction id={}",
                    chargingProcess.getOcppChargingProcessId(),
                    transaction!=null ? String.valueOf(transaction.getTransactionPk()) : "-");
            cleanup = config.checkNonExistentThreshold(
                    transaction!=null ? transaction.getStartEventTimestamp() :
                            chargingProcess.getStartDate());
        } else if (checkResult.isStopped()) {
            log.info("Charging process {} already stopped at the ESP, transaction id={}",
                    chargingProcess.getOcppChargingProcessId(),
                    transaction!=null ? String.valueOf(transaction.getTransactionPk()) : "-");
            cleanup = checkStoppedThreshold(checkResult.getTimeElapsedSinceStop());
        } else {
            log.info("Charging process {} still running at the ESP, transaction id={}",
                    chargingProcess.getOcppChargingProcessId(),
                    transaction!=null ? String.valueOf(transaction.getTransactionPk()) : "-");
        }
        return cleanup;
    }
    private boolean checkStoppedThreshold(long timePassedSinceStop) {
        log.info("Checking stopped threshold: {} hours before now...", config.getCleanupStoppedThresholdHours());
        return timePassedSinceStop > config.getCleanupStoppedThresholdHours() * HOURS_IN_MS;
    }




}
