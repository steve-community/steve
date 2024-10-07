package net.parkl.ocpp.service.middleware.receiver;

import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.module.esp.model.ESPChargingData;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class InMemoryAsyncMessageReceiver implements AsyncMessageReceiver {
    private ConcurrentHashMap<String, ESPChargingData> chargingDataMap = new ConcurrentHashMap<>();

    @Override
    public ESPChargingData receiveAsyncStopData(String externalChargeId) {
        return null;
    }

    @Override
    public void updateChargingConsumption(String externalChargeId, ESPChargingData data) {
        log.info("Updating charging consumption for externalChargeId {}: {}", externalChargeId, data);
        chargingDataMap.put(externalChargeId, data);

    }
}
