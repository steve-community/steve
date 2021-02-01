package net.parkl.ocpp.service;

import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.OcppChargingProcess;
import net.parkl.ocpp.entities.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ESPNotificationService {
    @Autowired
    @Qualifier("taskExecutor")
    private TaskExecutor executor;

    @Autowired
    private OcppMiddleware ocppMiddleware;

    public void notifyAboutChargingStopped(OcppChargingProcess chargingProcess) {
        executor.execute(() -> {
            log.info("Notifying ESP about stop transaction from charger: {}...",
                     chargingProcess.getTransaction().getTransactionPk());
            ocppMiddleware.stopChargingExternal(chargingProcess, OcppConstants.REASON_VEHICLE_CHARGED);
        });
    }

    public void notifyAboutConsumptionUpdated(OcppChargingProcess chargingProcess, Transaction transaction) {
        executor.execute(() -> {
            log.info("Notifying ESP about consumption of transaction: {}...",
                     chargingProcess.getTransaction().getTransactionPk());
            log.info("start: {}, stop: {}", transaction.getStartValue(), transaction.getStopValue());
            ocppMiddleware.updateConsumption(chargingProcess, transaction.getStartValue(), transaction.getStopValue());
        });
    }
}
