package net.parkl.ocpp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.OcppChargeBox;
import net.parkl.ocpp.repositories.OcppChargeBoxRepository;
import net.parkl.ocpp.service.config.AdvancedChargeBoxConfiguration;
import net.parkl.ocpp.service.middleware.OcppNotificationMiddleware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OcppHeartBeatWatcher {

    @Value("${ocpp.heartbeat.check.interval.mins:5}")
    private int checkIntervalMins;

    @Value("${ocpp.heartbeat.notification.async:true}")
    private boolean asyncNotification;

    private final TaskExecutor taskExecutor;
    private final OcppNotificationMiddleware notificationMiddleware;
    private final OcppChargeBoxRepository chargeBoxRepository;
    private final AdvancedChargeBoxConfiguration advancedChargeBoxConfiguration;



    public void watch() {
        long start=System.currentTimeMillis();
        log.debug("Starting heartbeat check for all chargeboxes...");
        List<String> chargeBoxIds = advancedChargeBoxConfiguration.getChargeBoxesForAlert();
        chargeBoxRepository.findByChargeBoxIdIn(chargeBoxIds).forEach(this::heartBeatCheck);
        log.info("Heartbeat check completed in {} ms", System.currentTimeMillis()-start);
    }

    public void heartBeatCheck(OcppChargeBox chargeBox) {
        log.debug("Checking last heartbeat timestamp for chargebox with id: {}", chargeBox.getChargeBoxId());
        Calendar date = Calendar.getInstance();
        date.add(Calendar.MINUTE, -checkIntervalMins);
        Date fiveMinBefore = date.getTime();
        if (chargeBox.getLastHeartbeatTimestamp() != null &&
                chargeBox.getLastHeartbeatTimestamp().before(fiveMinBefore)) {
            log.debug("Last heartbeat did not arrive in the {} mins period for chargebox with id={}, sending alert...",
                    checkIntervalMins, chargeBox.getChargeBoxId());
            if (asyncNotification) {
                taskExecutor.execute(() -> notificationMiddleware.sendHeartBeatOfflineAlert(chargeBox.getChargeBoxId()));
            } else {
                notificationMiddleware.sendHeartBeatOfflineAlert(chargeBox.getChargeBoxId());
            }

        }
    }
}
