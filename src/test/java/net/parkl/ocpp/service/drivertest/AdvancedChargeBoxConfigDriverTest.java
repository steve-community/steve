package net.parkl.ocpp.service.drivertest;

import net.parkl.ocpp.service.driver.AdvancedChargeBoxConfigDriver;
import net.parkl.ocpp.service.driver.DriverTestBase;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AdvancedChargeBoxConfigDriverTest extends DriverTestBase {

    private AdvancedChargeBoxConfigDriver configDriver;

    @Before
    public void setUp() {
        configDriver = driverFactory.createAdvancedChargeBoxDriver();
    }

    @Test
    public void testAddConfigValue() {
        configDriver
                .withChargeBoxId("chargebox.1")
                .withKey("key1")
                .withValue("value_of_key1")
                .createConfig();

        assertThat(configDriver.findByChargeBoxIdAndKey("chargebox.1", "key1"))
                .extracting("chargeBoxId",
                        "configKey",
                        "configValue")
                .containsExactly("chargebox.1",
                        "key1",
                        "value_of_key1");
    }

    @Test
    public void testDeleteConfig() {
        configDriver
                .withChargeBoxId("chargebox.2")
                .withKey("key1")
                .withValue("value_of_key1")
                .createConfig();

        configDriver.deleteByKey(
                configDriver.findByChargeBoxIdAndKey("chargebox.2", "key1").getConfigKey());

        assertThat(configDriver.findByChargeBoxIdAndKey("chargebox.2", "key1")).isNull();
    }

}