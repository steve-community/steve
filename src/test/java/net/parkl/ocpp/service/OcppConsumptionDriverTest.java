package net.parkl.ocpp.service;

import net.parkl.ocpp.module.esp.model.ESPChargingConsumptionRequest;
import net.parkl.ocpp.service.driver.ChargeBoxDriver;
import net.parkl.ocpp.service.driver.ChargingDriver;
import net.parkl.ocpp.service.driver.DriverTestBase;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import static de.rwth.idsg.steve.ocpp.OcppProtocol.V_16_SOAP;
import static net.parkl.ocpp.service.config.AdvancedChargeBoxConfigKeys.KEY_TRANSACTION_PARTIAL_ENABLED;


public class OcppConsumptionDriverTest extends DriverTestBase {
    private ChargeBoxDriver chargeBoxDriver;
    private ChargingDriver chargingDriver;

    @Before
    public void setUp() {
        chargeBoxDriver = driverFactory.createChargeBoxDriver();
        chargingDriver = driverFactory.createChargingDriver();
    }

    @Test
    public void testOcppConsumption() throws InterruptedException {
        chargeBoxDriver.deleteAdvancedConfigByKey(KEY_TRANSACTION_PARTIAL_ENABLED);

        chargeBoxDriver.withName("test1")
                .withProtocol(V_16_SOAP)
                .withConnectors(2)
                .createChargeBox();

        String chargingProcessId =
                chargingDriver.withChargeBoxId("test1")
                        .withConnectorId(1)
                        .withStartValue(200)
                        .withPlate("ABC123")
                        .withRfid("TAG_1")
                        .start();

        Thread.sleep(500);
        chargingDriver.withStopValue(500).stop(chargingProcessId);

        ESPChargingConsumptionRequest consumption = chargingDriver.waitForConsumption();
        Assertions.assertThat(consumption).isNotNull();

    }

}
