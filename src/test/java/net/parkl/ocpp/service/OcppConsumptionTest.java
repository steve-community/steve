package net.parkl.ocpp.service;

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import net.parkl.ocpp.module.esp.model.ESPChargingConsumptionRequest;
import net.parkl.ocpp.service.config.AdvancedChargeBoxConfigKeys;
import net.parkl.ocpp.service.config.AdvancedChargeBoxConfigService;
import net.parkl.ocpp.service.config.OcppServiceTestConfig;
import net.parkl.ocpp.util.AsyncWaiter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = OcppServiceTestConfig.class)
@WebAppConfiguration
public class OcppConsumptionTest implements OcppConsumptionListener {
    @Autowired
    private OcppMiddleware facade;
    @Autowired
    private OcppTestHelper helper;
    @Autowired
    private AdvancedChargeBoxConfigService chargeBoxConfigService;


    private ESPChargingConsumptionRequest lastConsumption;

    @Test
    public void testOcppConsumption() {
        this.lastConsumption = null;
        facade.registerConsumptionListener(this);

        chargeBoxConfigService.deleteByKey(AdvancedChargeBoxConfigKeys.KEY_TRANSACTION_PARTIAL_ENABLED);

        helper.createChargeBox("test1", OcppProtocol.V_16_SOAP, 2);

        helper.startAndStopCharging("test1", 1, "ABC123", 0, 1100, "TAG_1");
        waitForConsumption();
        Assert.assertEquals(lastConsumption.getTotalPower(), 1.1f, 0.01f);
        Assert.assertEquals(lastConsumption.getStopValue(), 1.1f, 0.01f);

        lastConsumption = null;
        helper.startAndStopCharging("test1", 1, "ABC123", 1100, 2300, "TAG_2");
        waitForConsumption();
        Assert.assertEquals(lastConsumption.getStartValue(), 1.1f, 0.01f);
        Assert.assertEquals(lastConsumption.getTotalPower(), 1.2f, 0.01f);
        Assert.assertEquals(lastConsumption.getStopValue(), 2.3f, 0.01f);
    }

    @Test
    public void testOcppConsumptionPartial() {
        this.lastConsumption = null;
        facade.registerConsumptionListener(this);

        chargeBoxConfigService.deleteByKey(AdvancedChargeBoxConfigKeys.KEY_TRANSACTION_PARTIAL_ENABLED);
        chargeBoxConfigService.saveConfigValue("test2", AdvancedChargeBoxConfigKeys.KEY_TRANSACTION_PARTIAL_ENABLED,
                "true");


        helper.createChargeBox("test2", OcppProtocol.V_16_SOAP, 2);

        helper.startAndStopCharging("test2", 1, "ABC123", 0, 1100,"TAG_3" );
        waitForConsumption();
        Assert.assertEquals(lastConsumption.getTotalPower(), 1.1f, 0.01f);
        Assert.assertEquals(lastConsumption.getStopValue(), 1.1f, 0.01f);

        lastConsumption = null;
        helper.startAndStopCharging("test2", 1, "ABC123", 0, 2300, "TAG_4");
        waitForConsumption();
        Assert.assertEquals(lastConsumption.getStartValue(), 1.1f, 0.01f);
        Assert.assertEquals(lastConsumption.getTotalPower(), 2.3f, 0.01f);
        Assert.assertEquals(lastConsumption.getStopValue(), 3.4f, 0.01f);
    }


    private void waitForConsumption() {
        AsyncWaiter<ESPChargingConsumptionRequest> w = new AsyncWaiter<>(5000);
        w.waitFor(() -> lastConsumption);
        Assert.assertNotNull(lastConsumption);
    }


    @Override
    public void consumptionUpdated(ESPChargingConsumptionRequest req) {
        this.lastConsumption = req;
    }
}
