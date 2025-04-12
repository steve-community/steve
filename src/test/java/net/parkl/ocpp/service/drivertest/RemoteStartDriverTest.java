package net.parkl.ocpp.service.drivertest;

import net.parkl.ocpp.service.driver.ChargeBoxDriver;
import net.parkl.ocpp.service.driver.ChargingDriver;
import net.parkl.ocpp.service.driver.DriverTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static de.rwth.idsg.steve.ocpp.OcppProtocol.V_16_SOAP;

class RemoteStartDriverTest extends DriverTestBase {
    private ChargeBoxDriver chargeBoxDriver;
    private ChargingDriver chargingDriver;

    @BeforeEach
    void setUp() {
        chargeBoxDriver = driverFactory.createChargeBoxDriver();
        chargingDriver = driverFactory.createChargingDriver();
    }
    @Test
    void testRemoteStartConfirmationFirst() {
        chargeBoxDriver.withName("test1")
                .withProtocol(V_16_SOAP)
                .withConnectors(2)
                .createChargeBox();
    }

}
