package net.parkl.ocpp.module.esp;

import net.parkl.ocpp.module.esp.model.ESPChargingConsumptionRequest;
import net.parkl.ocpp.module.esp.model.ESPChargingStopRequest;
import net.parkl.ocpp.module.esp.model.ESPRfidChargingStartRequest;

public interface EmobilityServiceProvider {
    void stopChargingExternal(ESPChargingStopRequest req);

    void updateChargingConsumptionExternal(ESPChargingConsumptionRequest req);

    void sendHeartBeatOfflineAlert(String chargeBoxId);

    boolean checkRfidTag(String rfidTag, String chargeBoxId);

    void notifyAboutRfidStart(ESPRfidChargingStartRequest startRequest);
}
