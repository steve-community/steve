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

    public static JaxWsProxyFactoryBean getBean(String endpointAddress) {
        JaxWsProxyFactoryBean f = new JaxWsProxyFactoryBean();
        f.setBindingId(SOAPBinding.SOAP12HTTP_BINDING);
        f.getFeatures().add(LoggingFeatureProxy.INSTANCE.get());
        f.getFeatures().add(new WSAddressingFeature());
        f.setAddress(endpointAddress);
        return f;
    }

    public static <T> T createClient(Class<T> clazz, String endpointAddress) {
        JaxWsProxyFactoryBean bean = getBean(endpointAddress);
        bean.setServiceClass(clazz);

        return clazz.cast(bean.create());
    }
}
