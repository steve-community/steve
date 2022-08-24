package net.parkl.ocpp.service.config;

import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.module.esp.EmobilityServiceProvider;
import net.parkl.ocpp.module.esp.model.ESPChargingConsumptionRequest;
import net.parkl.ocpp.module.esp.model.ESPChargingProcessCheckResult;
import net.parkl.ocpp.module.esp.model.ESPChargingStopRequest;
import net.parkl.ocpp.module.esp.model.ESPRfidChargingStartRequest;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TestESP implements EmobilityServiceProvider {
    @Override
    public void stopChargingExternal(ESPChargingStopRequest req) {
        log.info("stopping charging");
    }

    @Override
    public void updateChargingConsumptionExternal(ESPChargingConsumptionRequest req) {
        log.info("update charging consumption");
    }

    @Override
    public void sendHeartBeatOfflineAlert(String chargeBoxId) {
        log.info("sending heartbeat offline alert");
    }

    @Override
    public boolean checkRfidTag(String rfidTag, String chargeBoxId) {
        log.info("checking rfid tag");
        return true;
    }

    @Override
    public void notifyAboutRfidStart(ESPRfidChargingStartRequest startRequest) {
        log.info("notify about rfid start");
    }

    @Override
    public ESPChargingProcessCheckResult checkChargingProcess(String chargingProcessId) {
        return new ESPChargingProcessCheckResult(true,3*60*60*1000L);
    }
}
