package net.parkl.ocpp.service.driver;

import lombok.Getter;
import lombok.NoArgsConstructor;
import net.parkl.ocpp.module.esp.model.ESPChargingConsumptionRequest;
import net.parkl.ocpp.module.esp.model.ESPChargingStartRequest;
import net.parkl.ocpp.module.esp.model.ESPChargingUserStopRequest;
import net.parkl.ocpp.service.OcppConsumptionListener;
import net.parkl.ocpp.service.OcppMiddleware;
import net.parkl.ocpp.service.chargepoint.TestChargePoint;
import net.parkl.ocpp.util.AsyncWaiter;

@NoArgsConstructor
public class ChargingDriver {

    private OcppMiddleware ocppMiddleware;
    private TestChargePoint testChargePoint;

    private TestConsumptionListener testConsumptionListener = new TestConsumptionListener();
    private String chargeBoxId;
    private int connectorId;
    private String plate;
    private int startValue;
    private int stopValue;
    private String rfidTag;

    public static ChargingDriver createChargingDriver(OcppMiddleware ocppMiddleware, TestChargePoint testChargePoint) {
        ChargingDriver driver = new ChargingDriver();
        driver.testChargePoint = testChargePoint;
        driver.ocppMiddleware = ocppMiddleware;
        return driver;
    }

    public String start() {
        ocppMiddleware.registerConsumptionListener(testConsumptionListener);

        ESPChargingStartRequest req = ESPChargingStartRequest.builder()
                .chargeBoxId(chargeBoxId)
                .chargerId(String.valueOf(connectorId))
                .licensePlate(plate)
                .rfidTag(rfidTag).build();

        testChargePoint.setConsumptionStart(startValue);

        return ocppMiddleware.startCharging(req).getExternalChargingProcessId();
    }

    public void stop(String externalChargingProcessId) {
        testChargePoint.setConsumptionStop(stopValue);
        ocppMiddleware.stopCharging(
                ESPChargingUserStopRequest.builder()
                        .externalChargeId(externalChargingProcessId)
                        .build());
    }

    public ESPChargingConsumptionRequest waitForConsumption() {
        return testConsumptionListener.listenForConsumption();
    }

    @Getter
    private class TestConsumptionListener implements OcppConsumptionListener {
        private ESPChargingConsumptionRequest lastConsumption;

        @Override
        public void consumptionUpdated(ESPChargingConsumptionRequest request) {
            this.lastConsumption = request;
        }

        private ESPChargingConsumptionRequest listenForConsumption() {
            new AsyncWaiter<>(10000).waitFor(() -> this.lastConsumption);
            return lastConsumption;
        }
    }

    public ChargingDriver withChargeBoxId(String chargeBoxId) {
        this.chargeBoxId = chargeBoxId;
        return this;
    }

    public ChargingDriver withConnectorId(int connectorId) {
        this.connectorId = connectorId;
        return this;
    }

    public ChargingDriver withPlate(String plate) {
        this.plate = plate;
        return this;
    }

    public ChargingDriver withStartValue(int startValue) {
        this.startValue = startValue;
        return this;
    }

    public ChargingDriver withStopValue(int stopValue) {
        this.stopValue = stopValue;
        return this;
    }

    public ChargingDriver withRfid(String rfidTag) {
        this.rfidTag = rfidTag;
        return this;
    }

}
