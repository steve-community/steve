package net.parkl.ocpp.service;

import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.OcppChargingProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import static net.parkl.ocpp.service.OcppConstants.REASON_VEHICLE_CHARGED;

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
                    chargingProcess.getTransactionStart().getTransactionPk());
            ocppMiddleware.stopChargingExternal(chargingProcess, REASON_VEHICLE_CHARGED);
        });
    }

    public void notifyAboutConsumptionUpdated(OcppChargingProcess chargingProcess, String startValue, String stopValue) {
        executor.execute(() -> {
            log.info("Notifying ESP about consumption of transaction: {}...",
                    chargingProcess.getTransactionStart().getTransactionPk());
            log.info("start: {}, stop: {}", startValue, stopValue);
            ocppMiddleware.updateConsumption(chargingProcess, startValue, stopValue);
        });
    }
}
