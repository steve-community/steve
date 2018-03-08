package de.rwth.idsg.steve.ocpp.soap;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;

import javax.xml.ws.soap.SOAPBinding;

/**
 * TODO: Is it expensive to create the proxies every time?
 * Cache the proxies (endpointAddress, service) in a map-like structure maybe? Probably use one of these:
 *
 * 1) http://docs.guava-libraries.googlecode.com/git/javadoc/com/google/common/cache/CacheBuilder.html
 * 2) https://github.com/ben-manes/caffeine
 *
 * Why not a simple hash map: We need an eviction mechanism. In case the endpoint address changes after a while,
 * the older proxy is not needed.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 21.10.2015
 */
public final class ClientProvider {

    private ClientProvider() { }

    public static ocpp.cp._2010._08.ChargePointService getForOcpp12(String endpointAddress) {
        JaxWsProxyFactoryBean f = getBean(endpointAddress);

        f.setServiceClass(ocpp.cp._2010._08.ChargePointService.class);
        return (ocpp.cp._2010._08.ChargePointService) f.create();
    }

    public static ocpp.cp._2012._06.ChargePointService getForOcpp15(String endpointAddress) {
        JaxWsProxyFactoryBean f = getBean(endpointAddress);

        f.setServiceClass(ocpp.cp._2012._06.ChargePointService.class);
        return (ocpp.cp._2012._06.ChargePointService) f.create();
    }

    private static JaxWsProxyFactoryBean getBean(String endpointAddress) {
        JaxWsProxyFactoryBean f = new JaxWsProxyFactoryBean();
        f.setBindingId(SOAPBinding.SOAP12HTTP_BINDING);
        f.getFeatures().add(new WSAddressingFeature());
        f.setAddress(endpointAddress);
        return f;
    }
}
