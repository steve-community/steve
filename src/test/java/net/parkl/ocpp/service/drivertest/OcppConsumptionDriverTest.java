package net.parkl.ocpp.service.drivertest;

import net.parkl.ocpp.module.esp.model.ESPChargingConsumptionRequest;
import net.parkl.ocpp.service.driver.AdvancedChargeBoxConfigDriver;
import net.parkl.ocpp.service.driver.ChargeBoxDriver;
import net.parkl.ocpp.service.driver.ChargingDriver;
import net.parkl.ocpp.service.driver.DriverTestBase;
import org.junit.Before;
import org.junit.Test;

import static de.rwth.idsg.steve.ocpp.OcppProtocol.V_16_SOAP;
import static net.parkl.ocpp.service.config.AdvancedChargeBoxConfigKeys.KEY_TRANSACTION_PARTIAL_ENABLED;
import static org.assertj.core.api.Assertions.assertThat;


public class OcppConsumptionDriverTest extends DriverTestBase {
    private ChargeBoxDriver chargeBoxDriver;
    private ChargingDriver chargingDriver;
    private AdvancedChargeBoxConfigDriver advancedChargeBoxConfigDriver;

    @Before
    public void setUp() {
        chargeBoxDriver = driverFactory.createChargeBoxDriver();
        chargingDriver = driverFactory.createChargingDriver();
        advancedChargeBoxConfigDriver = driverFactory.createAdvancedChargeBoxDriver();
    }

    @Test
    public void testConsumptionUpdate()  {
        advancedChargeBoxConfigDriver.deleteByKey(KEY_TRANSACTION_PARTIAL_ENABLED);

        chargeBoxDriver.withName("test1")
                .withProtocol(V_16_SOAP)
                .withConnectors(2)
                .createChargeBox();

        String chargingProcessId =
                chargingDriver.withChargeBoxId("test1")
                        .withConnectorId(1)
                        .withStartValue(0)
                        .withPlate("ABC123")
                        .withRfid("TAG_1")
                        .start();

        chargingDriver.waitForChargingProcessStartedWithTransaction();

        chargingDriver.withStopValue(1100).stop(chargingProcessId);

        ESPChargingConsumptionRequest consumption = chargingDriver.waitForConsumption();
        assertThat(consumption)
                .isNotNull()
                .extracting("totalPower", "startValue", "stopValue")
                .containsExactly(1.1f, 0.0f, 1.1f);

        String chargingProcess2Id =
                chargingDriver.withChargeBoxId("test1")
                        .withConnectorId(1)
                        .withStartValue(1100)
                        .withPlate("ABC123")
                        .withRfid("TAG_2")
                        .start();

        chargingDriver.waitForChargingProcessStartedWithTransaction();

        chargingDriver.withStopValue(2300).stop(chargingProcess2Id);

        consumption = chargingDriver.waitForConsumption();
        assertThat(consumption)
                .isNotNull()
                .extracting("totalPower", "startValue", "stopValue")
                .containsExactly(1.2f, 1.1f, 2.3f);
    }

    @Test
    public void testPartialConsumptionUpdate() throws InterruptedException {
        advancedChargeBoxConfigDriver
                .withChargeBoxId("test2")
                .withKey(KEY_TRANSACTION_PARTIAL_ENABLED)
                .withValue("true")
                .createConfig();

        chargeBoxDriver.withName("test2")
                .withProtocol(V_16_SOAP)
                .withConnectors(2)
                .createChargeBox();

        String chargingProcessId =
                chargingDriver.withChargeBoxId("test2")
                        .withConnectorId(1)
                        .withStartValue(0)
                        .withPlate("ABC123")
                        .withRfid("TAG_3")
                        .start();

        chargingDriver.waitForChargingProcessStartedWithTransaction();

        chargingDriver.withStopValue(1100).stop(chargingProcessId);

        ESPChargingConsumptionRequest consumption = chargingDriver.waitForConsumption();
        assertThat(consumption)
                .isNotNull()
                .extracting("totalPower", "startValue", "stopValue")
                .containsExactly(1.1f, 0.0f, 1.1f);

        String chargingProcess2Id =
                chargingDriver.withChargeBoxId("test2")
                        .withConnectorId(1)
                        .withStartValue(0)
                        .withPlate("ABC123")
                        .withRfid("TAG_4")
                        .start();

        chargingDriver.waitForChargingProcessStartedWithTransaction();

        chargingDriver.withStopValue(2300).stop(chargingProcess2Id);

        consumption = chargingDriver.waitForConsumption();
        assertThat(consumption)
                .isNotNull()
                .extracting("totalPower", "startValue", "stopValue")
                .containsExactly(2.3f, 1.1f, 3.4f);
    }

}
