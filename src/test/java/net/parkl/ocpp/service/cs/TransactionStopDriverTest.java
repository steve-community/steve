package net.parkl.ocpp.service.cs;

import de.rwth.idsg.steve.repository.dto.UpdateTransactionParams;
import net.parkl.ocpp.entities.Transaction;
import net.parkl.ocpp.service.driver.ChargeBoxDriver;
import net.parkl.ocpp.service.driver.ChargingDriver;
import net.parkl.ocpp.service.driver.DriverTestBase;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static de.rwth.idsg.steve.ocpp.OcppProtocol.V_16_SOAP;
import static net.parkl.ocpp.entities.TransactionStopEventActor.station;

public class TransactionStopDriverTest extends DriverTestBase {
    private ChargeBoxDriver chargeBoxDriver;
    private ChargingDriver chargingDriver;

    @Autowired
    TransactionService transactionService;

    @Before
    public void setUp() {
        chargeBoxDriver = driverFactory.createChargeBoxDriver();
        chargingDriver = driverFactory.createChargingDriver();
    }

    @Test
    public void testStopTransactionTwice() throws InterruptedException {
        chargeBoxDriver.withName("stopTest")
                .withProtocol(V_16_SOAP)
                .withConnectors(2)
                .createChargeBox();

        chargingDriver.withChargeBoxId("stopTest")
                .withConnectorId(1)
                .withStartValue(0)
                .withPlate("XXX123")
                .withRfid("TAG_STOP")
                .start();

        chargingDriver.waitForChargingProcessStartedWithTransaction();

        List<Integer> transactionIds = transactionService.getActiveTransactionIds("stopTest");
        Assert.assertEquals(1, transactionIds.size());

        int transactionId = transactionIds.get(0);
        Transaction transaction = transactionService.findTransaction(transactionId).orElse(null);
        Assert.assertNotNull(transaction);

        UpdateTransactionParams params =
                UpdateTransactionParams.builder()
                        .chargeBoxId("stopTest")
                        .transactionId(transactionId)
                        .stopTimestamp(DateTime.now())
                        .stopMeterValue("111")
                        .stopReason("TEST1")
                        .eventTimestamp(DateTime.now())
                        .eventActor(station)
                        .build();

        transactionService.updateTransaction(params);
        Thread.sleep(1000);

        UpdateTransactionParams params2 =
                UpdateTransactionParams.builder()
                        .chargeBoxId("stopTest")
                        .transactionId(transactionId)
                        .stopTimestamp(DateTime.now())
                        .stopMeterValue("111")
                        .stopReason("TEST1")
                        .eventTimestamp(DateTime.now())
                        .eventActor(station)
                        .build();

        transactionService.updateTransaction(params2);
        transaction = transactionService.findTransaction(transactionId).orElse(null);
        Assert.assertNotNull(transaction);

    }
}
