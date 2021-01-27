package net.parkl.ocpp.service.config;

import net.parkl.ocpp.entities.AdvancedChargeBoxConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = OcppServiceTestConfig.class)
@WebAppConfiguration
public class AdvancedChargeBoxConfigServiceTest {

    @Autowired
    private AdvancedChargeBoxConfigService service;

    @Test
    public void testAddAndDeleteSpecificConfig() {
        String testChargeBoxId1 = "chargebox.1";
        String testChargeBoxId2 = "chargebox.2";

        service.saveConfigValue(testChargeBoxId1, "key1", "value");
        service.saveConfigValue(testChargeBoxId2, "key2", "value");
        service.saveConfigValue(testChargeBoxId1, "key3", "value");
        List<AdvancedChargeBoxConfig> configList = service.getAll();
        List<String> chargeBoxIdList = service.getChargeBoxIds();

        assertEquals(3, configList.size());
        assertEquals(2, chargeBoxIdList.size());
        assertTrue(chargeBoxIdList.stream().anyMatch(id -> id.equals("chargebox.1")));

        int idToDelete = configList.stream().findFirst().orElseThrow().getOcppChargeBoxConfigId();
        service.delete(idToDelete);

        configList = service.getAll();
        assertEquals(2, configList.size());
    }

}