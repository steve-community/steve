package net.parkl.ocpp.service.drivertest;

import net.parkl.ocpp.entities.OcppChargeBox;
import net.parkl.ocpp.service.driver.AdvancedChargeBoxConfigDriver;
import net.parkl.ocpp.service.driver.ChargeBoxDriver;
import net.parkl.ocpp.service.driver.DriverTestBase;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.springframework.stereotype.Component;

import java.util.List;

import static de.rwth.idsg.steve.ocpp.OcppProtocol.V_16_SOAP;
import static net.parkl.ocpp.service.config.AdvancedChargeBoxConfigKeys.KEY_SKIP_HEARTBEAT_CHECK;

@Component
public class AdvancedConfigHeartBeatWatcherTest extends DriverTestBase {

    private ChargeBoxDriver chargeBoxDriver;
    private AdvancedChargeBoxConfigDriver advancedChargeBoxConfigDriver;

    @Before
    public void setUp() {
        chargeBoxDriver = driverFactory.createChargeBoxDriver();
        advancedChargeBoxConfigDriver = driverFactory.createAdvancedChargeBoxDriver();
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

        List<OcppChargeBox> chargeBoxForAlert = advancedChargeBoxConfigDriver.getChargeBoxForAlert();
        Assertions.assertThat(chargeBoxForAlert).hasSize(1);
        Assertions.assertThat(chargeBoxForAlert).extracting("chargeBoxId").contains("testChargeBox");
    }
}
