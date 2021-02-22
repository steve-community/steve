package net.parkl.ocpp.service;

import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.OcppChargeBox;
import net.parkl.ocpp.service.cs.ChargePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
@Slf4j
public class OcppHeartBeatWatcher {

    private static final int CHECK_INTERVAL_MINS = 5;
    private final ChargePointService chargePointService;
    private final TaskExecutor taskExecutor;
    private final OcppMiddleware ocppMiddleware;

    @Autowired
    public OcppHeartBeatWatcher(ChargePointService chargePointService, TaskExecutor taskExecutor, OcppMiddleware ocppProxyServerFacade) {
        this.chargePointService = chargePointService;
        this.taskExecutor = taskExecutor;
        this.ocppMiddleware = ocppProxyServerFacade;
    }

    @Scheduled(fixedRate = 300000)
    public void watch() {
        chargePointService.getAllChargeBoxes().forEach(this::heartBeatCheck);
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
            taskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    ocppMiddleware.sendHeartBeatOfflineAlert(chargeBox.getChargeBoxId());
                }
            });
        }
    }
}