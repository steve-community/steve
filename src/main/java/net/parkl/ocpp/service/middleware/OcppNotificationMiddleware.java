package net.parkl.ocpp.service.middleware;

import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.module.esp.EmobilityServiceProvider;
import net.parkl.ocpp.module.esp.model.ESPRfidChargingStartRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OcppNotificationMiddleware  {

    @Autowired
    private EmobilityServiceProvider emobilityServiceProvider;

    public void sendHeartBeatOfflineAlert(String chargeBoxId) {
        log.info("Sending heart beat offline alert, chargeBoxId: {}", chargeBoxId);
        try {
            emobilityServiceProvider.sendHeartBeatOfflineAlert(chargeBoxId);
        } catch (Exception ex) {
            log.error("Failed to end heart beat offline alert, chargeBoxId:{} ", chargeBoxId, ex);
        }
    }

    public void notifyAboutRfidStart(ESPRfidChargingStartRequest startRequest) {
        log.info("Notify about RFID start, RFID = {}, charger = {}/{}",
                startRequest.getRfidTag(), startRequest.getChargeBoxId(), startRequest.getConnectorId());
        try {
            emobilityServiceProvider.notifyAboutRfidStart(startRequest);
        } catch (Exception ex) {
            log.error("Failed to notify about RFID start, RFID = {}, charger = {}/{}",
                    startRequest.getRfidTag(), startRequest.getChargeBoxId(), startRequest.getConnectorId(), ex);
        }
    }

}
