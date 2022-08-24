package net.parkl.ocpp.service.cs.cleanup;

import net.parkl.ocpp.service.cs.TransactionService;
import net.parkl.ocpp.service.driver.ChargeBoxDriver;
import net.parkl.ocpp.service.driver.ChargingDriver;
import net.parkl.ocpp.service.driver.DriverTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static de.rwth.idsg.steve.ocpp.OcppProtocol.V_16_SOAP;


public class TransactionCleanupManagerDriverTest extends DriverTestBase {
    private ChargeBoxDriver chargeBoxDriver;
    private ChargingDriver chargingDriver;

    @Autowired
    TransactionService transactionService;
    @Autowired
    TransactionCleanupManager cleanupManager;

    @Before
    public void setUp() {
        chargeBoxDriver = driverFactory.createChargeBoxDriver();
        chargingDriver = driverFactory.createChargingDriver();
    }

    @Test
    public void testCleanupStopped() {
        chargeBoxDriver.withName("cleanupTest")
                .withProtocol(V_16_SOAP)
                .withConnectors(2)
                .createChargeBox();

        chargingDriver.withChargeBoxId("cleanupTest")
                .withConnectorId(1)
                .withStartValue(0)
                .withPlate("XXX123")
                .withRfid("TAG_CLEANUP")
                .start();

        chargingDriver.waitForChargingProcessStartedWithTransaction();

        List<Integer> transactionIds = transactionService.getActiveTransactionIds("cleanupTest");
        Assert.assertEquals(1, transactionIds.size());

        cleanupManager.cleanupTransactions();
    }
}
