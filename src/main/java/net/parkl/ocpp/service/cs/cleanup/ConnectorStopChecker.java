package net.parkl.ocpp.service.cs.cleanup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.OcppChargingProcess;
import net.parkl.ocpp.entities.Transaction;
import net.parkl.ocpp.service.ChargingProcessService;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ConnectorStopChecker implements TransactionCleanupChecker{
    private final ChargingProcessService chargingProcessService;
    private final EmobilityServiceProviderChecker emobilityServiceProviderChecker;


    @Override
    public boolean checkTransactionForCleanup(Transaction transaction) {
        OcppChargingProcess chargingProcess = chargingProcessService.findByTransactionId(transaction.getTransactionPk());
        if (chargingProcess != null) {
            return emobilityServiceProviderChecker.checkEmobilityServiceProvider(transaction, chargingProcess);
        } else {
            return true;
        }
    }
}
