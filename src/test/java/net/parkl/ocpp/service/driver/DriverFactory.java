package net.parkl.ocpp.service.driver;

import net.parkl.ocpp.service.OcppMiddleware;
import net.parkl.ocpp.service.cs.OcppServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DriverFactory {
    @Autowired
    private OcppMiddleware ocppMiddleware;
    @Autowired
    private OcppServerService ocppServerService;

    public ChargeBoxDriver createChargeBoxDriver() {
        return ChargeBoxDriver.createChargeBoxDriver(ocppMiddleware, ocppServerService);
    }
}
