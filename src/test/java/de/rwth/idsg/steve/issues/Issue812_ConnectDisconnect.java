package de.rwth.idsg.steve.issues;

import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.utils.OcppJsonChargePoint;
import ocpp.cs._2012._06.ChargePointErrorCode;
import ocpp.cs._2012._06.ChargePointStatus;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static de.rwth.idsg.steve.utils.Helpers.getRandomString;

public class Issue812_ConnectDisconnect {

    private final OcppJsonChargePoint chargePoint;

    public Issue812_ConnectDisconnect() {
        this.chargePoint = new OcppJsonChargePoint(
            OcppVersion.V_15,
            "issue812",
            "ws://localhost:8080/steve/websocket/CentralSystemService/");

        chargePoint.start();
    }

    public static void main(String[] args) throws Exception {
        int count = 200;
        for (int i = 0; i < count; i++) {
            System.out.println(i);
            Issue812_ConnectDisconnect test = new Issue812_ConnectDisconnect();
            Thread.sleep(TimeUnit.SECONDS.toMillis(1));
            test.close();
        }
    }

    private List<Runnable> getActions() {
        return Arrays.asList(boot, auth, status);
    }

    private void close() {
        chargePoint.close();
    }

    private final Runnable boot = new Runnable() {
        @Override
        public void run() {
            ocpp.cs._2012._06.BootNotificationRequest boot = new ocpp.cs._2012._06.BootNotificationRequest()
                .withChargePointVendor(getRandomString())
                .withChargePointModel(getRandomString());

            chargePoint.send(boot, ocpp.cs._2012._06.BootNotificationResponse.class,
                bootResponse -> Assertions.assertEquals(ocpp.cs._2012._06.RegistrationStatus.ACCEPTED, bootResponse.getStatus()),
                error -> Assertions.fail()
            );
        }
    };

    private final Runnable status = new Runnable() {
        @Override
        public void run() {
            ocpp.cs._2012._06.StatusNotificationRequest stat = new ocpp.cs._2012._06.StatusNotificationRequest()
                .withStatus(ChargePointStatus.AVAILABLE)
                .withConnectorId(ThreadLocalRandom.current().nextInt(5))
                .withErrorCode(ChargePointErrorCode.NO_ERROR)
                .withTimestamp(DateTime.now());

            chargePoint.send(stat, ocpp.cs._2012._06.StatusNotificationResponse.class,
                Assertions::assertNotNull,
                error -> Assertions.fail()
            );
        }
    };

    private final Runnable auth = new Runnable() {
        @Override
        public void run() {
            ocpp.cs._2012._06.AuthorizeRequest auth = new ocpp.cs._2012._06.AuthorizeRequest().withIdTag("user1");

            chargePoint.send(auth, ocpp.cs._2012._06.AuthorizeResponse.class,
                authResponse -> Assertions.assertEquals(ocpp.cs._2012._06.AuthorizationStatus.ACCEPTED, authResponse.getIdTagInfo().getStatus()),
                error -> Assertions.fail()
            );
        }
    };
}
