package net.parkl.ocpp.service.driver;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.OcppChargingProcess;
import net.parkl.ocpp.module.esp.model.ESPChargingStartRequest;
import net.parkl.ocpp.module.esp.model.ESPChargingUserStopRequest;
import net.parkl.ocpp.service.ChargingProcessService;
import net.parkl.ocpp.service.OcppConsumptionListener;
import net.parkl.ocpp.service.chargepoint.TestChargePoint;
import net.parkl.ocpp.service.middleware.OcppChargingMiddleware;
import net.parkl.ocpp.util.AsyncWaiter;

import java.util.List;

@NoArgsConstructor
@Slf4j
public class ChargingDriver {

    private OcppChargingMiddleware chargingMiddleware;
    private TestChargePoint testChargePoint;
    private ChargingProcessService chargingProcessService;

    private final TestConsumptionListener testConsumptionListener = new TestConsumptionListener();
    private String chargeBoxId;
    private int connectorId;
    private String plate;
    private int startValue;
    private int stopValue;
    private String rfidTag;
    private Float kwhLimit;
    private Integer minuteLimit;

    public static ChargingDriver createChargingDriver(OcppChargingMiddleware ocppMiddleware,
                                                      TestChargePoint testChargePoint,
                                                      ChargingProcessService chargingProcessService) {
        ChargingDriver driver = new ChargingDriver();
        driver.testChargePoint = testChargePoint;
        driver.chargingMiddleware = ocppMiddleware;
        driver.chargingProcessService = chargingProcessService;
        return driver;
    }

    public String start() {
        testConsumptionListener.reset();
        chargingMiddleware.registerConsumptionListener(testConsumptionListener);

        ESPChargingStartRequest req = ESPChargingStartRequest.builder()
                .chargeBoxId(chargeBoxId)
                .chargerId(String.valueOf(connectorId))
                .licensePlate(plate)
                .rfidTag(rfidTag)
                .limitKwh(kwhLimit)
                .limitMin(minuteLimit)
                .build();

        testChargePoint.setConsumptionStart(startValue);

        return chargingMiddleware.startCharging(req).getExternalChargingProcessId();
    }

    public void stop(String externalChargingProcessId) {
        testConsumptionListener.reset();
        testChargePoint.setConsumptionStop(stopValue);
        chargingMiddleware.stopCharging(
                ESPChargingUserStopRequest.builder()
                        .externalChargeId(externalChargingProcessId)
                        .build());
    }

    public OcppChargingProcess getChargingProcess(String chargingProcessId) {
        return chargingProcessService.findOcppChargingProcess(chargingProcessId);
    }

    public void waitForChargingProcessStartedWithTransaction() {
        new AsyncWaiter<>(5000).waitFor(() ->
                chargingProcessService.findByOcppTagAndConnectorAndEndDateIsNullAndTransactionIsNotNull(rfidTag, connectorId, chargeBoxId));
    }

    public ESPChargingConsumptionRequest waitForConsumption() {
        return testConsumptionListener.listenForConsumption();
    }

    public List<OcppChargingProcess> findOpenChargingProcessesWithLimitKwh() {
        return chargingProcessService.findOpenChargingProcessesWithLimitKwh();
    }

    public List<OcppChargingProcess> findOpenChargingProcessesWithLimitMinute() {
        return chargingProcessService.findOpenChargingProcessesWithLimitMinute();
    }

    @Getter
    private static class TestConsumptionListener implements OcppConsumptionListener {
        private ESPChargingConsumptionRequest lastConsumption;

        @Override
        public void consumptionUpdated(ESPChargingConsumptionRequest request) {
            this.lastConsumption = request;
        }

        private ESPChargingConsumptionRequest listenForConsumption() {
            new AsyncWaiter<>(10000).waitFor(() -> this.lastConsumption);
            log.info("test Consumption: {}", lastConsumption);
            return lastConsumption;
        }

        public void reset() {
            lastConsumption = null;
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

    public ChargingDriver withLimitKwh(float kwhLimit) {
        this.kwhLimit = kwhLimit;
        return this;
    }

    public ChargingDriver withLimitMinute(int minuteLimit) {
        this.minuteLimit = minuteLimit;
        return this;
    }
}
