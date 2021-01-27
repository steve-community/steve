package net.parkl.ocpp.service.config;

import net.parkl.ocpp.service.IntegrationTestBase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class AdvancedChargeBoxConfigServiceTest extends IntegrationTestBase {

    @Autowired
    private AdvancedChargeBoxConfigService service;

    @Test
    public void testAddConfigValue() {
        service.saveConfigValue("chargebox.1", "key1", "value_of_key1");
        assertThat(service.findByChargeBoxIdAndKey("chargebox.1", "key1"))
                .extracting("chargeBoxId",
                        "configKey",
                        "configValue")
                .containsExactly("chargebox.1",
                        "key1",
                        "value_of_key1");
    }

    @Test
    public void testDeleteConfig() {
        service.saveConfigValue("chargebox.2", "key1", "value_of_key1");
        service.delete(service.findByChargeBoxIdAndKey("chargebox.2", "key1").getId());

        assertThat(service.findByChargeBoxIdAndKey("chargebox.2", "key1")).isNull();
    }

}