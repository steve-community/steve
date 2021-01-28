package net.parkl.ocpp.service.driver;

import net.parkl.ocpp.service.OcppMiddleware;
import net.parkl.ocpp.service.chargepoint.TestChargePoint;
import net.parkl.ocpp.service.config.AdvancedChargeBoxConfigService;
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
    @Autowired
    private AdvancedChargeBoxConfigService advancedChargeBoxConfigService;
    @Autowired
    private TestChargePoint testChargePoint;

    public ChargeBoxDriver createChargeBoxDriver() {
        return ChargeBoxDriver.createChargeBoxDriver(ocppMiddleware,
                chargePointService, connectorService, advancedChargeBoxConfigService);
    }

    public ChargingDriver createChargingDriver() {
        return ChargingDriver.createChargingDriver(ocppMiddleware, testChargePoint);
    }
}
