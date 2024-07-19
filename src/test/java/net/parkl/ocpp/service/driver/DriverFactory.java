package net.parkl.ocpp.service.driver;

import net.parkl.ocpp.service.ChargingProcessService;
import net.parkl.ocpp.service.chargepoint.TestChargePoint;
import net.parkl.ocpp.service.cs.ChargePointService;
import net.parkl.ocpp.service.cs.ConnectorService;
import net.parkl.ocpp.service.middleware.OcppChargePointMiddleware;
import net.parkl.ocpp.service.middleware.OcppChargingMiddleware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DriverFactory {
    @Autowired
    private OcppChargePointMiddleware chargePointMiddleware;
    @Autowired
    private OcppChargingMiddleware chargingMiddleware;
    @Autowired
    private ChargePointService chargePointService;
    @Autowired
    private ConnectorService connectorService;
    @Autowired
    private TestChargePoint testChargePoint;
    @Autowired
    private ChargingProcessService chargingProcessService;

    public ChargeBoxDriver createChargeBoxDriver() {
        return ChargeBoxDriver.createChargeBoxDriver(chargePointMiddleware, chargePointService, connectorService);
    }

    public ChargingDriver createChargingDriver() {
        return ChargingDriver.createChargingDriver(chargingMiddleware, testChargePoint, chargingProcessService);
    }
}
