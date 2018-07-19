package de.rwth.idsg.steve.issues;

import de.rwth.idsg.steve.StressTest;
import de.rwth.idsg.steve.utils.Helpers;
import de.rwth.idsg.steve.utils.StressTester;
import ocpp.cs._2015._10.BootNotificationRequest;
import ocpp.cs._2015._10.BootNotificationResponse;
import ocpp.cs._2015._10.CentralSystemService;
import ocpp.cs._2015._10.RegistrationStatus;
import ocpp.cs._2015._10.StartTransactionRequest;
import ocpp.cs._2015._10.StartTransactionResponse;
import org.joda.time.DateTime;
import org.junit.Assert;

import java.util.concurrent.ThreadLocalRandom;

import static de.rwth.idsg.steve.utils.Helpers.getForOcpp16;
import static de.rwth.idsg.steve.utils.Helpers.getPath;
import static de.rwth.idsg.steve.utils.Helpers.getRandomString;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 19.07.2018
 */
public class Issue81 extends StressTest {

    private static final String path = getPath();

    public static void main(String[] args) throws Exception {
        new Issue81().attack();
    }

    protected void attackInternal() throws Exception {
        StressTester.Runnable runnable = new StressTester.Runnable() {

            private final ThreadLocal<CentralSystemService> client = new ThreadLocal<>();
            private final ThreadLocal<String> chargeBoxId = new ThreadLocal<>();
            private final ThreadLocal<StartTransactionRequest> txRequest = new ThreadLocal<>();
            private final ThreadLocal<Integer> txId = new ThreadLocal<>();

            @Override
            public void beforeRepeat() {
                client.set(getForOcpp16(path));
                chargeBoxId.set(Helpers.getRandomString());

                BootNotificationResponse boot = getForOcpp16(path).bootNotification(
                        new BootNotificationRequest()
                                .withChargePointVendor(getRandomString())
                                .withChargePointModel(getRandomString()),
                        chargeBoxId.get());
                Assert.assertEquals(RegistrationStatus.ACCEPTED, boot.getStatus());

                StartTransactionRequest req = new StartTransactionRequest()
                        .withConnectorId(ThreadLocalRandom.current().nextInt())
                        .withIdTag(Helpers.getRandomString())
                        .withTimestamp(DateTime.now())
                        .withMeterStart(ThreadLocalRandom.current().nextInt());
                txRequest.set(req);

                Integer t1 = sendStartTx(client.get(), txRequest.get(), chargeBoxId.get());
                txId.set(t1);
            }

            @Override
            public void toRepeat() {
                Integer t2 = sendStartTx(client.get(), txRequest.get(), chargeBoxId.get());
                Assert.assertEquals(txId.get(), t2);
            }

            @Override
            public void afterRepeat() {

            }
        };

        StressTester tester = new StressTester(THREAD_COUNT, REPEAT_COUNT_PER_THREAD);
        tester.test(runnable);
        tester.shutDown();
    }

    private static Integer sendStartTx(CentralSystemService client, StartTransactionRequest req, String chargeBoxId) {
        StartTransactionResponse start = client.startTransaction(req, chargeBoxId);
        Assert.assertNotNull(start);
        return start.getTransactionId();
    }
}