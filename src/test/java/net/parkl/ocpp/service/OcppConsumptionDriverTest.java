package net.parkl.ocpp.service;

import lombok.Getter;
import net.parkl.ocpp.module.esp.model.ESPChargingConsumptionRequest;
import net.parkl.ocpp.service.driver.ChargeBoxDriver;
import net.parkl.ocpp.service.driver.ChargingDriver;
import net.parkl.ocpp.service.driver.DriverTestBase;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static de.rwth.idsg.steve.ocpp.OcppProtocol.V_16_SOAP;
import static net.parkl.ocpp.service.config.AdvancedChargeBoxConfigKeys.KEY_TRANSACTION_PARTIAL_ENABLED;


@Ignore
public class OcppConsumptionDriverTest extends DriverTestBase {
    private ChargeBoxDriver chargeBoxDriver;
    private ChargingDriver chargingDriver;

    @Before
    public void setUp() {
        chargeBoxDriver = driverFactory.createChargeBoxDriver();
        chargingDriver = driverFactory.createChargingDriver();
    }

    @Test
    public void testOcppConsumption() {
        TestConsumptionListener consumptionListener = new TestConsumptionListener();
        chargingDriver.registerConsumptionListener(consumptionListener);

        chargeBoxDriver.deleteAdvancedConfigByKey(KEY_TRANSACTION_PARTIAL_ENABLED);

        chargeBoxDriver.withName("test1")
                .withProtocol(V_16_SOAP)
                .withConnectors(2)
                .createChargeBox();

        chargingDriver.withChargeBoxId("test1")
                .withConnectorId(1)
                .withPlate("ABC123")
                .withStartValue(0)
                .withStopValue(1100)
                .withRfid("TAG_1")
                .start();
//
//        helper.startAndStopCharging("test1", 1, "ABC123", 0, 1100, "TAG_1");
//        waitForConsumption();
//        Assert.assertEquals(lastConsumption.getTotalPower(), 1.1f, 0.01f);
//        Assert.assertEquals(lastConsumption.getStopValue(), 1.1f, 0.01f);
//
//        lastConsumption = null;
//        helper.startAndStopCharging("test1", 1, "ABC123", 1100, 2300, "TAG_2");
//        waitForConsumption();
//        Assert.assertEquals(lastConsumption.getStartValue(), 1.1f, 0.01f);
//        Assert.assertEquals(lastConsumption.getTotalPower(), 1.2f, 0.01f);
//        Assert.assertEquals(lastConsumption.getStopValue(), 2.3f, 0.01f);
    }

    @Getter
    private class TestConsumptionListener implements OcppConsumptionListener {
        private ESPChargingConsumptionRequest lastConsumption;

        @Override
        public void consumptionUpdated(ESPChargingConsumptionRequest request) {
            this.lastConsumption = request;
        }
    }
}
