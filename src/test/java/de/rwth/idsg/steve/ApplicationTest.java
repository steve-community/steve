package de.rwth.idsg.steve;

import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2010._08.AuthorizationStatus;
import ocpp.cs._2010._08.AuthorizeRequest;
import ocpp.cs._2010._08.AuthorizeResponse;
import ocpp.cs._2010._08.BootNotificationRequest;
import ocpp.cs._2010._08.BootNotificationResponse;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import javax.xml.ws.soap.SOAPBinding;
import java.util.UUID;

import static de.rwth.idsg.steve.SteveConfiguration.CONFIG;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 10.03.2018
 */
@Ignore
@Slf4j
public class ApplicationTest {

    private static final String path = getPath();

    /**
     * Tests that the SOAP code paths are working.
     */
    @Test
    public void testSoapPipeline() throws Exception {
        startApp();

        BootNotificationResponse boot12 = getForOcpp12().bootNotification(
                new BootNotificationRequest()
                        .withChargePointVendor(getRandomString())
                        .withChargePointModel(getRandomString()),
                getRandomString());
        Assert.assertNotNull(boot12);
        Assert.assertEquals(ocpp.cs._2010._08.RegistrationStatus.REJECTED, boot12.getStatus());

        ocpp.cs._2012._06.BootNotificationResponse boot15 = getForOcpp15().bootNotification(
                new ocpp.cs._2012._06.BootNotificationRequest()
                        .withChargePointVendor(getRandomString())
                        .withChargePointModel(getRandomString()),
                getRandomString());
        Assert.assertNotNull(boot15);
        Assert.assertEquals(ocpp.cs._2012._06.RegistrationStatus.REJECTED, boot15.getStatus());

        AuthorizeResponse auth12 = getForOcpp12().authorize(
                new AuthorizeRequest()
                        .withIdTag(getRandomString()),
                getRandomString());
        Assert.assertNotNull(auth12);
        Assert.assertEquals(AuthorizationStatus.INVALID, auth12.getIdTagInfo().getStatus());

        ocpp.cs._2012._06.AuthorizeResponse auth15 = getForOcpp15().authorize(
                new ocpp.cs._2012._06.AuthorizeRequest()
                        .withIdTag(getRandomString()),
                getRandomString());
        Assert.assertNotNull(auth15);
        Assert.assertEquals(ocpp.cs._2012._06.AuthorizationStatus.INVALID, auth15.getIdTagInfo().getStatus());
    }

    private Application startApp() throws Exception {
        Application app = new Application();
        app.start();
        return app;
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
