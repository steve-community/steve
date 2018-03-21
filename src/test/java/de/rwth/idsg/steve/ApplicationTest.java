package de.rwth.idsg.steve;

import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2010._08.AuthorizationStatus;
import ocpp.cs._2010._08.AuthorizeRequest;
import ocpp.cs._2010._08.AuthorizeResponse;
import ocpp.cs._2010._08.BootNotificationRequest;
import ocpp.cs._2010._08.BootNotificationResponse;
import ocpp.cs._2010._08.RegistrationStatus;
import ocpp.cs._2012._06.CentralSystemService;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.ws.soap.SOAPBinding;
import java.util.UUID;

import static de.rwth.idsg.steve.SteveConfiguration.CONFIG;

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
        ocpp.cs._2010._08.CentralSystemService client = getForOcpp12();

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
        CentralSystemService client = getForOcpp15();

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
        ocpp.cs._2015._10.CentralSystemService client = getForOcpp16();

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

    private static String getRandomString() {
        return UUID.randomUUID().toString();
    }

    private static String getPath() {
        if (CONFIG.getJetty().isHttpEnabled()) {
            return "http://"
                    + CONFIG.getJetty().getServerHost() + ":"
                    + CONFIG.getJetty().getHttpPort()
                    + CONFIG.getContextPath() + "/services"
                    + CONFIG.getRouterEndpointPath();
        } else if (CONFIG.getJetty().isHttpsEnabled()) {
            return "https://"
                    + CONFIG.getJetty().getServerHost() + ":"
                    + CONFIG.getJetty().getHttpsPort()
                    + CONFIG.getContextPath() + "/services"
                    + CONFIG.getRouterEndpointPath();
        } else {
            throw new RuntimeException();
        }
    }

    private static ocpp.cs._2015._10.CentralSystemService getForOcpp16() {
        JaxWsProxyFactoryBean f = getBean(path);
        f.setServiceClass(ocpp.cs._2015._10.CentralSystemService.class);
        return (ocpp.cs._2015._10.CentralSystemService) f.create();
    }

    private static ocpp.cs._2012._06.CentralSystemService getForOcpp15() {
        JaxWsProxyFactoryBean f = getBean(path);
        f.setServiceClass(ocpp.cs._2012._06.CentralSystemService.class);
        return (ocpp.cs._2012._06.CentralSystemService) f.create();
    }

    private static ocpp.cs._2010._08.CentralSystemService getForOcpp12() {
        JaxWsProxyFactoryBean f = getBean(path);
        f.setServiceClass(ocpp.cs._2010._08.CentralSystemService.class);
        return (ocpp.cs._2010._08.CentralSystemService) f.create();
    }

    private static JaxWsProxyFactoryBean getBean(String endpointAddress) {
        JaxWsProxyFactoryBean f = new JaxWsProxyFactoryBean();
        f.setBindingId(SOAPBinding.SOAP12HTTP_BINDING);
        f.getFeatures().add(new WSAddressingFeature());
        f.setAddress(endpointAddress);
        return f;
    }
}
