package net.parkl.ocpp.service.drivertest;

import net.parkl.ocpp.entities.OcppChargingProcess;
import net.parkl.ocpp.service.driver.AdvancedChargeBoxConfigDriver;
import net.parkl.ocpp.service.driver.ChargeBoxDriver;
import net.parkl.ocpp.service.driver.ChargingDriver;
import net.parkl.ocpp.service.driver.DriverTestBase;
import org.junit.Before;
import org.junit.Test;

import static de.rwth.idsg.steve.ocpp.OcppProtocol.V_16_SOAP;
import static net.parkl.ocpp.service.config.AdvancedChargeBoxConfigKeys.KEY_IDTAG_MAX10;
import static org.assertj.core.api.Assertions.assertThat;

public class AdvancedConfigRfidTagDriverTest extends DriverTestBase {

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
    public void testRfidMax10Char() {
        advancedChargeBoxConfigDriver
                .withChargeBoxId("rfidChargeBox")
                .withKey(KEY_IDTAG_MAX10)
                .withValue("true")
                .createConfig();

        chargeBoxDriver.withName("rfidChargeBox")
                .withProtocol(V_16_SOAP)
                .withConnectors(2)
                .createChargeBox();

        String originalRfidTag = "BASKTEBALL-01";

        String chargingProcessId =
                chargingDriver.withChargeBoxId("rfidChargeBox")
                        .withConnectorId(1)
                        .withStartValue(0)
                        .withPlate("TSLA404")
                        .withRfid(originalRfidTag)
                        .start();

        chargingDriver.waitForChargingProcessStartedWithTransaction();

        OcppChargingProcess started = chargingDriver.getChargingProcess(chargingProcessId);

        assertThat(started.getOcppTag()).isEqualTo(originalRfidTag.substring(0, 9));
    }
}
