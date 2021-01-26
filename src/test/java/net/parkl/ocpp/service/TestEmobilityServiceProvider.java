package net.parkl.ocpp.service;

import net.parkl.ocpp.module.esp.EmobilityServiceProvider;
import net.parkl.ocpp.module.esp.model.ESPChargingConsumptionRequest;
import net.parkl.ocpp.module.esp.model.ESPChargingStopRequest;
import net.parkl.ocpp.module.esp.model.ESPRfidChargingStartRequest;
import org.springframework.stereotype.Component;

@Component
public class TestEmobilityServiceProvider implements EmobilityServiceProvider {
    @Override
    public void stopChargingExternal(ESPChargingStopRequest req) {

    }

    @Override
    public void updateChargingConsumptionExternal(ESPChargingConsumptionRequest req) {

    }

    @Override
    public void sendHeartBeatOfflineAlert(String chargeBoxId) {

    }

    @Override
    public boolean checkRfidTag(String rfidTag, String chargeBoxId) {
        return false;
    }

    @Override
    public void notifyAboutRfidStart(ESPRfidChargingStartRequest startRequest) {

    }
}
