package net.parkl.ocpp.service;

import net.parkl.ocpp.module.esp.model.ESPChargingConsumptionRequest;
import net.parkl.ocpp.service.driver.ChargeBoxDriver;
import net.parkl.ocpp.service.driver.DriverTestBase;
import org.junit.Before;


//TODO create test consumption listener
public class OcppConsumptionDriverTest extends DriverTestBase implements OcppConsumptionListener {
    private ChargeBoxDriver chargeBoxDriver;

    @Before
    public void setUp() {
        chargeBoxDriver = driverFactory.createChargeBoxDriver();
    }


    @Override
    public void consumptionUpdated(ESPChargingConsumptionRequest req) {

    }
}
