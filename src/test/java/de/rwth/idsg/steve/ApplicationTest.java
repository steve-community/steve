package de.rwth.idsg.steve;

import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2010._08.AuthorizationStatus;
import ocpp.cs._2010._08.AuthorizeRequest;
import ocpp.cs._2010._08.AuthorizeResponse;
import ocpp.cs._2010._08.BootNotificationRequest;
import ocpp.cs._2010._08.BootNotificationResponse;
import ocpp.cs._2010._08.RegistrationStatus;
import ocpp.cs._2012._06.CentralSystemService;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static de.rwth.idsg.steve.Utils.getForOcpp12;
import static de.rwth.idsg.steve.Utils.getForOcpp15;
import static de.rwth.idsg.steve.Utils.getForOcpp16;
import static de.rwth.idsg.steve.Utils.getPath;
import static de.rwth.idsg.steve.Utils.getRandomString;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 10.03.2018
 */
@Slf4j
public class ApplicationTest {

    private static final String REGISTERED_CHARGE_BOX_ID = __DatabasePreparer__.getRegisteredChargeBoxId();
    private static final String REGISTERED_OCPP_TAG =  __DatabasePreparer__.getRegisteredOcppTag();
    private static final String path = getPath();

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
    public void testOcpp12() {
        ocpp.cs._2010._08.CentralSystemService client = getForOcpp12(path);

        BootNotificationResponse boot = client.bootNotification(
                new BootNotificationRequest()
                        .withChargePointVendor(getRandomString())
                        .withChargePointModel(getRandomString()),
                REGISTERED_CHARGE_BOX_ID);
        Assert.assertNotNull(boot);
        Assert.assertEquals(RegistrationStatus.ACCEPTED, boot.getStatus());

        AuthorizeResponse auth = client.authorize(
                new AuthorizeRequest()
                        .withIdTag(REGISTERED_OCPP_TAG),
                REGISTERED_CHARGE_BOX_ID);
        Assert.assertNotNull(auth);
        Assert.assertEquals(AuthorizationStatus.ACCEPTED, auth.getIdTagInfo().getStatus());
    }

    @Test
    public void testOcpp15() {
        CentralSystemService client = getForOcpp15(path);

        ocpp.cs._2012._06.BootNotificationResponse boot = client.bootNotification(
                new ocpp.cs._2012._06.BootNotificationRequest()
                        .withChargePointVendor(getRandomString())
                        .withChargePointModel(getRandomString()),
                REGISTERED_CHARGE_BOX_ID);
        Assert.assertNotNull(boot);
        Assert.assertEquals(ocpp.cs._2012._06.RegistrationStatus.ACCEPTED, boot.getStatus());

        ocpp.cs._2012._06.AuthorizeResponse auth = client.authorize(
                new ocpp.cs._2012._06.AuthorizeRequest()
                        .withIdTag(REGISTERED_OCPP_TAG),
                REGISTERED_CHARGE_BOX_ID);
        Assert.assertNotNull(auth);
        Assert.assertEquals(ocpp.cs._2012._06.AuthorizationStatus.ACCEPTED, auth.getIdTagInfo().getStatus());
    }

    @Test
    public void testOcpp16() {
        ocpp.cs._2015._10.CentralSystemService client = getForOcpp16(path);

        ocpp.cs._2015._10.BootNotificationResponse boot = client.bootNotification(
                new ocpp.cs._2015._10.BootNotificationRequest()
                        .withChargePointVendor(getRandomString())
                        .withChargePointModel(getRandomString()),
                getRandomString());
        Assert.assertNotNull(boot);
        Assert.assertEquals(ocpp.cs._2015._10.RegistrationStatus.REJECTED, boot.getStatus());

        ocpp.cs._2015._10.AuthorizeResponse auth = client.authorize(
                new ocpp.cs._2015._10.AuthorizeRequest()
                        .withIdTag(getRandomString()),
                getRandomString());
        Assert.assertNotNull(auth);
        Assert.assertEquals(ocpp.cs._2015._10.AuthorizationStatus.INVALID, auth.getIdTagInfo().getStatus());
    }

}
