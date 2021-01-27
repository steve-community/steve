package net.parkl.ocpp.service.driver;

import net.parkl.ocpp.service.OcppMiddleware;
import net.parkl.ocpp.service.cs.ChargePointService;
import net.parkl.ocpp.service.cs.ConnectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DriverFactory {
    @Autowired
    private OcppMiddleware ocppMiddleware;
    @Autowired
    private ChargePointService chargePointService;
    @Autowired
    private ConnectorService connectorService;

    public ChargeBoxDriver createChargeBoxDriver() {
        return ChargeBoxDriver.createChargeBoxDriver(ocppMiddleware, chargePointService, connectorService);
    }
}
