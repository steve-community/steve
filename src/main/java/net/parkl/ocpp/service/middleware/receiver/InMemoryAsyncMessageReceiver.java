package net.parkl.ocpp.service.middleware.receiver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.module.esp.model.ESPChargingData;
import net.parkl.ocpp.util.AsyncWaiter;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class InMemoryAsyncMessageReceiver implements AsyncMessageReceiver {
    private final ConcurrentHashMap<String, ESPChargingData> chargingDataMap = new ConcurrentHashMap<>();



    @Override
    public ESPChargingData receiveAsyncStopData(String externalChargeId, long timeout) {
        AsyncWaiter<ESPChargingData> waiter = new AsyncWaiter<>(timeout);
        ESPChargingData state = waiter.waitFor(() -> {
            ESPChargingData state1 = chargingDataMap.get(externalChargeId);
            if (state1 == null || state1.getStopValue() == null || state1.getStartValue() == null) {
                log.debug("IotChargingState not found yet");
                return null;
            }
            return state1;
        });

        if (state == null) {
            log.info("Wait for async stop failed: {}", externalChargeId);
            return null;
        }
        log.info("Received async stop data for {}: {}",externalChargeId, state);
        return state;
    }

    @Override
    public void updateChargingConsumption(String externalChargeId, ESPChargingData data) {
        log.info("Updating charging consumption for externalChargeId {}: {}", externalChargeId, data);
        chargingDataMap.put(externalChargeId, data);

    }
}
