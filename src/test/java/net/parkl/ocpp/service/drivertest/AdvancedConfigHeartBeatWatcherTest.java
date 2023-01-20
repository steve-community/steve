package net.parkl.ocpp.service.drivertest;

import net.parkl.ocpp.entities.OcppChargeBox;
import net.parkl.ocpp.service.driver.AdvancedChargeBoxConfigDriver;
import net.parkl.ocpp.service.driver.ChargeBoxDriver;
import net.parkl.ocpp.service.driver.ChargingDriver;
import net.parkl.ocpp.service.driver.DriverTestBase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.rwth.idsg.steve.ocpp.OcppProtocol.V_16_SOAP;
import static net.parkl.ocpp.service.config.AdvancedChargeBoxConfigKeys.KEY_SKIP_HEARTBEAT_CHECK;

public class AdvancedConfigHeartBeatWatcherTest extends DriverTestBase {

    private AdvancedChargeBoxConfigDriver advancedChargeBoxConfigDriver;
    private ChargeBoxDriver chargeBoxDriver;
    private ChargingDriver chargingDriver;

    @BeforeEach
    public void setUp() {
        chargeBoxDriver = driverFactory.createChargeBoxDriver();
        advancedChargeBoxConfigDriver = driverFactory.createAdvancedChargeBoxDriver();
        chargingDriver = driverFactory.createChargingDriver();
        chargeBoxDriver.deleteAllChargePoints();
    }

    @Test
    public void getChargeBoxesForAlert() {
        advancedChargeBoxConfigDriver
                .withChargeBoxId("skipChargeBox")
                .withKey(KEY_SKIP_HEARTBEAT_CHECK)
                .withValue("true")
                .createConfig();

        chargeBoxDriver.withName("skipChargeBox")
                .withProtocol(V_16_SOAP)
                .withConnectors(2)
                .createChargeBox();

        chargeBoxDriver.withName("testChargeBox")
                .withProtocol(V_16_SOAP)
                .withConnectors(2)
                .createChargeBox();

        chargeBoxDriver.withName("testChargeBoxActive")
                .withProtocol(V_16_SOAP)
                .withConnectors(2)
                .createChargeBox();

        chargingDriver.withChargeBoxId("testChargeBoxActive")
                .withConnectorId(1)
                .withStartValue(0)
                .withPlate("ABC123")
                .withRfid("TAG_1")
                .start();

        chargingDriver.waitForChargingProcessStartedWithTransaction();

        List<OcppChargeBox> chargeBoxForAlert = advancedChargeBoxConfigDriver.getChargeBoxForAlert();
        Assertions.assertThat(chargeBoxForAlert).hasSize(1);
        Assertions.assertThat(chargeBoxForAlert).extracting("chargeBoxId").contains("testChargeBox");
    }
}
