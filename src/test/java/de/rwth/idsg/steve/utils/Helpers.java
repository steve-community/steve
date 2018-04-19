package de.rwth.idsg.steve.utils;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;

import javax.xml.ws.soap.SOAPBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static de.rwth.idsg.steve.SteveConfiguration.CONFIG;

/**
 * @author Andreas Heuvels <andreas.heuvels@rwth-aachen.de>
 * @since 06.04.18
 */
public class Helpers {

    public static String getRandomString() {
        return UUID.randomUUID().toString();
    }

    public static List<String> getRandomStrings(int size) {
        List<String> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(getRandomString());
        }
        return list;
    }

    public static String getPath() {
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

    public static ocpp.cs._2015._10.CentralSystemService getForOcpp16(String path) {
        JaxWsProxyFactoryBean f = getBean(path);
        f.setServiceClass(ocpp.cs._2015._10.CentralSystemService.class);
        return (ocpp.cs._2015._10.CentralSystemService) f.create();
    }

    public static ocpp.cs._2012._06.CentralSystemService getForOcpp15(String path) {
        JaxWsProxyFactoryBean f = getBean(path);
        f.setServiceClass(ocpp.cs._2012._06.CentralSystemService.class);
        return (ocpp.cs._2012._06.CentralSystemService) f.create();
    }

    public static ocpp.cs._2010._08.CentralSystemService getForOcpp12(String path) {
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
