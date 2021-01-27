package net.parkl.ocpp.service.driver;

import net.parkl.ocpp.service.EmobilityServiceProviderFacade;
import net.parkl.ocpp.service.cs.OcppServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DriverFactory {
    @Autowired
    private EmobilityServiceProviderFacade emobilityServiceProviderFacade;
    @Autowired
    private OcppServerService ocppServerService;

    public ChargeBoxDriver createChargeBoxDriver() {
        return ChargeBoxDriver.createChargeBoxDriver(emobilityServiceProviderFacade, ocppServerService);
    }
}
