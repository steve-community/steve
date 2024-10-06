package net.parkl.ocpp.service.cs.cleanup;

import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.Connector;
import net.parkl.ocpp.entities.OcppChargingProcess;
import net.parkl.ocpp.entities.Transaction;
import net.parkl.ocpp.module.esp.model.ESPConnectorStopResults;
import net.parkl.ocpp.repositories.OcppChargingProcessRepository;
import net.parkl.ocpp.repositories.TransactionRepository;
import net.parkl.ocpp.service.ChargingProcessService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectorStopService {
    private final TransactionRepository transactionRepository;
    private final ConnectorStopChecker connectorStopChecker;
    private final TransactionCleanupManager transactionCleanupManager;
    private final OcppChargingProcessRepository ocppChargingProcessRepository;

    public ESPConnectorStopResults stopConnectorCharging(Connector connector) {
        List<Transaction> transactions = transactionRepository.findByConnectorAndStopTimestampIsNullOrderByStartTimestampDesc(connector);

        int cleanedUp = transactionCleanupManager.cleanupTransactionsWithCheck(transactions, connectorStopChecker);

        if (cleanedUp > 0) {
            log.info("Transactions cleanup completed, {} transactions cleaned up", cleanedUp);
        } else {
            log.info("No transactions to cleanup");
        }

        List<OcppChargingProcess> processes = ocppChargingProcessRepository.findWithoutTransactionOnConnector(connector);
        int deleted = transactionCleanupManager.cleanupChargingProcessesWithoutTransaction(processes);

        if (deleted > 0) {
            log.info("Charging processes cleanup completed, {} charging processes cleaned up", deleted);
        } else {
            log.info("No charging processes to cleanup");
        }
        return ESPConnectorStopResults.builder()
                .transactionsCleanedUp(cleanedUp)
                .processesCleanedUp(deleted)
                .build();
    }


}
