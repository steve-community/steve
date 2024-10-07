package net.parkl.ocpp.service.middleware.receiver;

import net.parkl.ocpp.module.esp.model.ESPChargingData;

public interface AsyncMessageReceiver {
    ESPChargingData receiveAsyncStopData(String externalChargeId);

    void updateChargingConsumption(String externalChargeId, ESPChargingData req);
}
