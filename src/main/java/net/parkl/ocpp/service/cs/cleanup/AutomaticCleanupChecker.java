package net.parkl.ocpp.service.cs.cleanup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.OcppChargingProcess;
import net.parkl.ocpp.entities.Transaction;
import net.parkl.ocpp.service.ChargingProcessService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class AutomaticCleanupChecker implements TransactionCleanupChecker {
    private final ChargingProcessService chargingProcessService;
    private final EmobilityServiceProviderChecker emobilityServiceProviderChecker;
    private final TransactionCleanupConfig config;

    @Override
    public boolean checkTransactionForCleanup(Transaction transaction) {
        List<OcppChargingProcess> chargingProcesses = chargingProcessService.findListByTransactionId(transaction.getTransactionPk());
        boolean cleanup = false;
        if (!chargingProcesses.isEmpty()) {
            for (OcppChargingProcess chargingProcess : chargingProcesses) {
                cleanup = emobilityServiceProviderChecker.checkEmobilityServiceProvider(transaction, chargingProcess);
                if (!cleanup) {
                    break;
                }
            }
        } else {
            log.info("Charging process was null for transaction: {}", transaction.getTransactionPk());
            cleanup = config.checkNonExistentThreshold(transaction.getStartEventTimestamp());
        }
        return cleanup;
    }
}
