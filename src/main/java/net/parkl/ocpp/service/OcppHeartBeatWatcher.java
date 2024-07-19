package net.parkl.ocpp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.OcppChargeBox;
import net.parkl.ocpp.repositories.OcppChargeBoxRepository;
import net.parkl.ocpp.service.config.AdvancedChargeBoxConfiguration;
import net.parkl.ocpp.service.middleware.OcppNotificationMiddleware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OcppHeartBeatWatcher {
    private static final int CHECK_INTERVAL_MINS = 5;
    private final TaskExecutor taskExecutor;
    private final OcppNotificationMiddleware notificationMiddleware;
    private final OcppChargeBoxRepository chargeBoxRepository;
    private final AdvancedChargeBoxConfiguration advancedChargeBoxConfiguration;



    public void watch() {
        List<String> chargeBoxIds = advancedChargeBoxConfiguration.getChargeBoxesForAlert();
        chargeBoxRepository.findByChargeBoxIdIn(chargeBoxIds).forEach(this::heartBeatCheck);
    }

    public void heartBeatCheck(OcppChargeBox chargeBox) {
        log.info("Checking last heartbeat timestamp for chargebox with id: {}", chargeBox.getChargeBoxId());
        Calendar date = Calendar.getInstance();
        date.add(Calendar.MINUTE, -CHECK_INTERVAL_MINS);
        Date fiveMinBefore = date.getTime();
        if (chargeBox.getLastHeartbeatTimestamp() != null &&
                chargeBox.getLastHeartbeatTimestamp().before(fiveMinBefore)) {
            log.info("Last heartbeat did not arrive in the {} mins period for chargebox with id={}, sending alert...",
                    CHECK_INTERVAL_MINS, chargeBox.getChargeBoxId());
            taskExecutor.execute(() -> notificationMiddleware.sendHeartBeatOfflineAlert(chargeBox.getChargeBoxId()));
        }
    }
}
