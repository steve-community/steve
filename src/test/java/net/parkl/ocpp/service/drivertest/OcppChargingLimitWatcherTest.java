package net.parkl.ocpp.service.drivertest;

import net.parkl.ocpp.entities.OcppChargingProcess;
import net.parkl.ocpp.service.driver.ChargeBoxDriver;
import net.parkl.ocpp.service.driver.ChargingDriver;
import net.parkl.ocpp.service.driver.DriverTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;

import java.util.List;

import static de.rwth.idsg.steve.ocpp.OcppProtocol.V_16_SOAP;
import static org.assertj.core.api.Assertions.assertThat;

@Component
public class OcppChargingLimitWatcherTest extends DriverTestBase {
    private ChargeBoxDriver chargeBoxDriver;
    private ChargingDriver chargingDriver;

    @BeforeEach
    public void setUp() {
        chargeBoxDriver = driverFactory.createChargeBoxDriver();
        chargingDriver = driverFactory.createChargingDriver();
    }

    @Test
    public void testFindKwhLimitProcess() {
        chargeBoxDriver.withName("kwhLimitTest")
                .withProtocol(V_16_SOAP)
                .withConnectors(2)
                .createChargeBox();

        chargingDriver.withChargeBoxId("kwhLimitTest")
                .withConnectorId(1)
                .withStartValue(0)
                .withPlate("KWHLIMIT1")
                .withRfid("TAG_KWH")
                .withLimitKwh(1f)
                .start();

        chargingDriver.waitForChargingProcessStartedWithTransaction();

        List<OcppChargingProcess> processesWithLimitKwh = chargingDriver.findOpenChargingProcessesWithLimitKwh();
        assertThat(processesWithLimitKwh).isNotNull().hasSize(1);
    }

    @Test
    public void testFindMinuteLimitProcess() {
        chargeBoxDriver.withName("minuteLimitTest")
                .withProtocol(V_16_SOAP)
                .withConnectors(2)
                .createChargeBox();

        chargingDriver.withChargeBoxId("minuteLimitTest")
                .withConnectorId(1)
                .withStartValue(0)
                .withPlate("MINUTELIMIT1")
                .withRfid("TAG_MINUTE")
                .withLimitMinute(1)
                .start();

        chargingDriver.waitForChargingProcessStartedWithTransaction();

        List<OcppChargingProcess> processesWithLimitMinute = chargingDriver.findOpenChargingProcessesWithLimitMinute();
        assertThat(processesWithLimitMinute).isNotNull().hasSize(1);
    }
}
