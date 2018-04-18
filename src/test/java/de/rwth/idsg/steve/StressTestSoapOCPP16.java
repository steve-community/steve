package de.rwth.idsg.steve;

import de.rwth.idsg.steve.utils.StressTester;
import de.rwth.idsg.steve.utils.__DatabasePreparer__;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2015._10.AuthorizationStatus;
import ocpp.cs._2015._10.AuthorizeRequest;
import ocpp.cs._2015._10.AuthorizeResponse;
import ocpp.cs._2015._10.BootNotificationRequest;
import ocpp.cs._2015._10.BootNotificationResponse;
import ocpp.cs._2015._10.CentralSystemService;
import ocpp.cs._2015._10.RegistrationStatus;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static de.rwth.idsg.steve.utils.Helpers.getForOcpp16;
import static de.rwth.idsg.steve.utils.Helpers.getPath;
import static de.rwth.idsg.steve.utils.Helpers.getRandomString;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 18.04.2018
 */
@Slf4j
public class StressTestSoapOCPP16 {

    private static final String REGISTERED_CHARGE_BOX_ID = __DatabasePreparer__.getRegisteredChargeBoxId();

    private static final String path = getPath();
    private static Application app;

    @BeforeClass
    public static void initClass() throws Exception {
        Assert.assertEquals(ApplicationProfile.TEST, SteveConfiguration.CONFIG.getProfile());
        Assert.assertTrue(SteveConfiguration.CONFIG.getOcpp().isAutoRegisterUnknownStations());

        app = new Application();
        app.start();
    }

    @AfterClass
    public static void destroyClass() throws Exception {
        if (app != null) {
            app.stop();
        }
    }

    @Before
    public void init() {
        __DatabasePreparer__.prepare();
    }

    @After
    public void destroy() {
        __DatabasePreparer__.cleanUp();
    }

    @Test
    public void testBootNotification() throws Exception {
        StressTester tester = new StressTester(50, 5);

        CentralSystemService client = getForOcpp16(path);

        tester.test(() -> {
            BootNotificationResponse boot = client.bootNotification(
                    new BootNotificationRequest()
                            .withChargePointVendor(getRandomString())
                            .withChargePointModel(getRandomString()),
                    getRandomString());
            Assert.assertEquals(RegistrationStatus.ACCEPTED, boot.getStatus());
        });

        tester.shutDown();
    }

    @Test
    public void testAuthorize() throws Exception {
        StressTester tester = new StressTester(100, 10);

        CentralSystemService client = getForOcpp16(path);

        tester.test(() -> {
            AuthorizeResponse auth = client.authorize(
                    new AuthorizeRequest().withIdTag(getRandomString()),
                    REGISTERED_CHARGE_BOX_ID);
            Assert.assertEquals(AuthorizationStatus.INVALID, auth.getIdTagInfo().getStatus());
        });

        tester.shutDown();
    }

}
