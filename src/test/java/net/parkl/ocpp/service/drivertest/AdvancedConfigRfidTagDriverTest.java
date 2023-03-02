package net.parkl.ocpp.service.drivertest;

import net.parkl.ocpp.entities.OcppChargingProcess;
import net.parkl.ocpp.service.driver.ChargeBoxDriver;
import net.parkl.ocpp.service.driver.ChargingDriver;
import net.parkl.ocpp.service.driver.DriverTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static de.rwth.idsg.steve.ocpp.OcppProtocol.V_16_SOAP;
import static org.assertj.core.api.Assertions.assertThat;

class AdvancedConfigRfidTagDriverTest extends DriverTestBase {

    private ChargeBoxDriver chargeBoxDriver;
    private ChargingDriver chargingDriver;

    @BeforeEach
    public void setUp() {
        chargeBoxDriver = driverFactory.createChargeBoxDriver();
        chargingDriver = driverFactory.createChargingDriver();
    }

    @Test
    void testIntegratedIdTagProved() {
        final String integratedIdTagChargeBox = "integratedIdTagChargeBox";
        chargeBoxDriver.withName(integratedIdTagChargeBox)
                .withProtocol(V_16_SOAP)
                .withConnectors(2)
                .createChargeBox();

        String chargingProcessId =
                chargingDriver.withChargeBoxId(integratedIdTagChargeBox)
                        .withConnectorId(1)
                        .withStartValue(0)
                        .withPlate("TSLA404")
                        .withRfid("TAG_1")
                        .start();

        chargingDriver.waitForChargingProcessStartedWithTransaction();

        OcppChargingProcess started = chargingDriver.getChargingProcess(chargingProcessId);

        assertThat(started.getOcppTag()).isEqualTo("TEST_ID_TAG");
    }
}
