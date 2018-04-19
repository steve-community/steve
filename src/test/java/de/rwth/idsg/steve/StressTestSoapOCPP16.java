package de.rwth.idsg.steve;

import de.rwth.idsg.steve.utils.StressTester;
import de.rwth.idsg.steve.utils.__DatabasePreparer__;
import ocpp.cs._2015._10.AuthorizationStatus;
import ocpp.cs._2015._10.AuthorizeRequest;
import ocpp.cs._2015._10.AuthorizeResponse;
import ocpp.cs._2015._10.BootNotificationRequest;
import ocpp.cs._2015._10.BootNotificationResponse;
import ocpp.cs._2015._10.CentralSystemService;
import ocpp.cs._2015._10.ChargePointErrorCode;
import ocpp.cs._2015._10.ChargePointStatus;
import ocpp.cs._2015._10.HeartbeatRequest;
import ocpp.cs._2015._10.HeartbeatResponse;
import ocpp.cs._2015._10.MeterValue;
import ocpp.cs._2015._10.MeterValuesRequest;
import ocpp.cs._2015._10.MeterValuesResponse;
import ocpp.cs._2015._10.RegistrationStatus;
import ocpp.cs._2015._10.SampledValue;
import ocpp.cs._2015._10.StartTransactionRequest;
import ocpp.cs._2015._10.StartTransactionResponse;
import ocpp.cs._2015._10.StatusNotificationRequest;
import ocpp.cs._2015._10.StatusNotificationResponse;
import ocpp.cs._2015._10.StopTransactionRequest;
import ocpp.cs._2015._10.StopTransactionResponse;
import org.joda.time.DateTime;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static de.rwth.idsg.steve.utils.Helpers.getForOcpp16;
import static de.rwth.idsg.steve.utils.Helpers.getPath;
import static de.rwth.idsg.steve.utils.Helpers.getRandomString;
import static de.rwth.idsg.steve.utils.Helpers.getRandomStrings;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 18.04.2018
 */
public class StressTestSoapOCPP16 {

    // higher values -> more stress
    //
    private static final int THREAD_COUNT = 50;
    private static final int REPEAT_COUNT_PER_THREAD = 50;

    // lower values -> more stress
    //
    // reason: these only specify the size of the values "bag" from which a test picks a value randomly. if there is
    // less values to pick from, it is more likely that tests will use the same value at the same time. this produces
    // more overhead for steve (especially db) when multiple threads "fight" for inserting/updating a db row/cell.
    //
    private static final int ID_TAG_COUNT = 50;
    private static final int CHARGE_BOX_COUNT = 100;
    private static final int CONNECTOR_COUNT_PER_CHARGE_BOX = 25;

    private static final String path = getPath();

    public static void main(String[] args) throws Exception {
        new StressTestSoapOCPP16().attack();
    }

    private void attack() throws Exception {
        Assert.assertEquals(ApplicationProfile.TEST, SteveConfiguration.CONFIG.getProfile());
        Assert.assertTrue(SteveConfiguration.CONFIG.getOcpp().isAutoRegisterUnknownStations());

        __DatabasePreparer__.prepare();

        Application app = new Application();
        try {
            app.start();
            attackInternal();
        } finally {
            try {
                app.stop();
            } finally {
                __DatabasePreparer__.cleanUp();
            }
        }
    }

    private static void attackInternal() throws Exception {
        final List<String> idTags = getRandomStrings(ID_TAG_COUNT);
        final List<String> chargeBoxIds = getRandomStrings(CHARGE_BOX_COUNT);

        StressTester tester = new StressTester(THREAD_COUNT, REPEAT_COUNT_PER_THREAD);

        tester.test(() -> {
            CentralSystemService client = getForOcpp16(path);
            ThreadLocalRandom localRandom = ThreadLocalRandom.current();

            String idTag = idTags.get(localRandom.nextInt(idTags.size()));
            String chargeBoxId = chargeBoxIds.get(localRandom.nextInt(chargeBoxIds.size()));
            int connectorId = localRandom.nextInt(1, CONNECTOR_COUNT_PER_CHARGE_BOX + 1);

            int transactionStart = localRandom.nextInt(0, Integer.MAX_VALUE);
            int transactionStop = localRandom.nextInt(transactionStart + 1, Integer.MAX_VALUE);

            // to insert chargeBoxId into db
            BootNotificationResponse boot = client.bootNotification(
                    new BootNotificationRequest()
                            .withChargePointVendor(getRandomString())
                            .withChargePointModel(getRandomString()),
                    chargeBoxId);
            Assert.assertEquals(RegistrationStatus.ACCEPTED, boot.getStatus());

            HeartbeatResponse heartbeat = client.heartbeat(
                    new HeartbeatRequest(),
                    chargeBoxId
            );
            Assert.assertNotNull(heartbeat);

            for (int i = 0; i <= CONNECTOR_COUNT_PER_CHARGE_BOX; i++) {
                StatusNotificationResponse status = client.statusNotification(
                        new StatusNotificationRequest()
                                .withErrorCode(ChargePointErrorCode.NO_ERROR)
                                .withStatus(ChargePointStatus.AVAILABLE)
                                .withConnectorId(i)
                                .withTimestamp(DateTime.now()),
                        chargeBoxId
                );
                Assert.assertNotNull(status);
            }

            AuthorizeResponse auth = client.authorize(
                    new AuthorizeRequest().withIdTag(idTag),
                    chargeBoxId
            );
            Assert.assertNotEquals(AuthorizationStatus.ACCEPTED, auth.getIdTagInfo().getStatus());

            StartTransactionResponse start = client.startTransaction(
                    new StartTransactionRequest()
                            .withConnectorId(connectorId)
                            .withIdTag(idTag)
                            .withTimestamp(DateTime.now())
                            .withMeterStart(transactionStart),
                    chargeBoxId
            );
            Assert.assertNotNull(start);

            StatusNotificationResponse statusStart = client.statusNotification(
                    new StatusNotificationRequest()
                            .withErrorCode(ChargePointErrorCode.NO_ERROR)
                            .withStatus(ChargePointStatus.CHARGING)
                            .withConnectorId(connectorId)
                            .withTimestamp(DateTime.now()),
                    chargeBoxId
            );
            Assert.assertNotNull(statusStart);

            MeterValuesResponse meter = client.meterValues(
                    new MeterValuesRequest()
                            .withConnectorId(connectorId)
                            .withTransactionId(start.getTransactionId())
                            .withMeterValue(getMeterValues(transactionStart, transactionStop)),
                    chargeBoxId
            );
            Assert.assertNotNull(meter);

            StopTransactionResponse stop = client.stopTransaction(
                    new StopTransactionRequest()
                            .withTransactionId(start.getTransactionId())
                            .withTimestamp(DateTime.now())
                            .withIdTag(idTag)
                            .withMeterStop(transactionStop),
                    chargeBoxId
            );
            Assert.assertNotNull(stop);

            StatusNotificationResponse statusStop = client.statusNotification(
                    new StatusNotificationRequest()
                            .withErrorCode(ChargePointErrorCode.NO_ERROR)
                            .withStatus(ChargePointStatus.AVAILABLE)
                            .withConnectorId(connectorId)
                            .withTimestamp(DateTime.now()),
                    chargeBoxId
            );
            Assert.assertNotNull(statusStop);
        });

        tester.shutDown();
    }

    private static List<MeterValue> getMeterValues(int transactionStart, int transactionStop) {
        final int size = 4;
        int delta = (transactionStop - transactionStart) / size;
        if (delta == 0) {
            return Collections.emptyList();
        }

        List<MeterValue> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            int meterValue = transactionStart + delta * (i + 1);
            list.add(createMeterValue(meterValue));
        }
        return list;
    }

    private static MeterValue createMeterValue(int val) {
        return new MeterValue().withTimestamp(DateTime.now())
                               .withSampledValue(new SampledValue().withValue(Integer.toString(val)));
    }

}