package net.parkl.ocpp.service.driver;

import lombok.NoArgsConstructor;
import net.parkl.ocpp.module.esp.model.ESPChargingStartRequest;
import net.parkl.ocpp.module.esp.model.ESPChargingUserStopRequest;
import net.parkl.ocpp.service.OcppMiddleware;

@NoArgsConstructor
public class ChargingDriver {

    private OcppMiddleware ocppMiddleware;

    private String chargeBoxId;
    private int connectorId;
    private String plate;
    private int startValue;
    private int stopValue;
    private String rfidTag;

    public static ChargingDriver createChargingDriver(OcppMiddleware ocppMiddleware) {
        ChargingDriver driver = new ChargingDriver();
        driver.ocppMiddleware = ocppMiddleware;
        return driver;
    }

    public String start() {
        ESPChargingStartRequest req = ESPChargingStartRequest.builder()
                .chargeBoxId(chargeBoxId)
                .chargerId(String.valueOf(connectorId))
                .licensePlate(plate)
                .rfidTag(rfidTag).build();

        return ocppMiddleware.startCharging(req).getExternalChargingProcessId();
    }

    public void stop(String externalChargingProcessId) {
        ocppMiddleware.stopCharging(
                ESPChargingUserStopRequest.builder()
                        .externalChargeId(externalChargingProcessId)
                        .build());
    }

}
