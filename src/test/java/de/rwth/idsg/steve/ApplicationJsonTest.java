package de.rwth.idsg.steve;

import de.rwth.idsg.steve.ocpp.OcppVersion;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2015._10.AuthorizationStatus;
import ocpp.cs._2015._10.AuthorizeRequest;
import ocpp.cs._2015._10.AuthorizeResponse;
import ocpp.cs._2015._10.BootNotificationRequest;
import ocpp.cs._2015._10.BootNotificationResponse;
import ocpp.cs._2015._10.RegistrationStatus;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static de.rwth.idsg.steve.Utils.getRandomString;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 21.03.2018
 */
@Slf4j
public class ApplicationJsonTest {

    private static final String PATH = "ws://localhost:8080/steve/websocket/CentralSystemService/";
    private static final OcppVersion VERSION = OcppVersion.V_16;

    private static final String REGISTERED_CHARGE_BOX_ID = __DatabasePreparer__.getRegisteredChargeBoxId();
    private static final String REGISTERED_OCPP_TAG =  __DatabasePreparer__.getRegisteredOcppTag();

    private static Application app;

    @BeforeClass
    public static void init() throws Exception {
        __DatabasePreparer__.prepare();

        app = new Application();
        app.start();
    }

    @AfterClass
    public static void destroy() throws Exception {
        app.stop();
        __DatabasePreparer__.cleanUp();
    }

    @Test
    public void testOcpp16() {
        OcppJsonChargePoint chargePoint = new OcppJsonChargePoint(VERSION, REGISTERED_CHARGE_BOX_ID, PATH);
        chargePoint.start();

        BootNotificationRequest boot = new BootNotificationRequest()
                .withChargePointVendor(getRandomString())
                .withChargePointModel(getRandomString());

        chargePoint.prepare(boot, BootNotificationResponse.class,
                bootResponse -> Assert.assertEquals(RegistrationStatus.ACCEPTED, bootResponse.getStatus()),
                error -> Assert.fail()
        );

        AuthorizeRequest auth = new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG);

        chargePoint.prepare(auth, AuthorizeResponse.class,
                authResponse -> Assert.assertEquals(AuthorizationStatus.ACCEPTED, authResponse.getIdTagInfo().getStatus()),
                error -> Assert.fail()
        );

        chargePoint.processAndClose();
    }

}
