package net.parkl.ocpp.service.middleware.receiver;

import net.parkl.ocpp.module.esp.model.ESPChargingData;
import org.springframework.stereotype.Component;

@Component
public class InMemoryAsyncMessageReceiver implements AsyncMessageReceiver {
    @Override
    public ESPChargingData receiveAsyncStopData(String externalChargeId) {
        return null;
    }

    @Override
    public void updateChargingConsumption(String externalChargeId, ESPChargingData req) {

    }
}
