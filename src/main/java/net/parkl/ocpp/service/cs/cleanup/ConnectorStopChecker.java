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
public class ConnectorStopChecker implements TransactionCleanupChecker{
    private final ChargingProcessService chargingProcessService;
    private final EmobilityServiceProviderChecker emobilityServiceProviderChecker;


    @Override
    public boolean checkTransactionForCleanup(Transaction transaction) {
        List<OcppChargingProcess> chargingProcesses = chargingProcessService.findListByTransactionId(transaction.getTransactionPk());

        if (!chargingProcesses.isEmpty()) {
            boolean cleanup= false;
            for (OcppChargingProcess chargingProcess : chargingProcesses) {
                cleanup = emobilityServiceProviderChecker.checkEmobilityServiceProvider(transaction, chargingProcess);
                if (!cleanup) {
                    break;
                }
            }
            return cleanup;
        } else {
            return true;
        }
    }
}
