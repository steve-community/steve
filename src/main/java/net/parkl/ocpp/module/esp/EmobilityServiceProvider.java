package net.parkl.ocpp.module.esp;

import net.parkl.ocpp.module.esp.model.ESPChargingConsumptionRequest;
import net.parkl.ocpp.module.esp.model.ESPChargingStopRequest;

public interface EmobilityServiceProvider {
    void stopChargingExternal(ESPChargingStopRequest req);

    void updateChargingConsumptionExternal(ESPChargingConsumptionRequest req);
}
