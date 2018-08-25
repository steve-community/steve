package de.rwth.idsg.steve.ocpp.soap;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.SSLSocketFactory;
import javax.xml.ws.soap.SOAPBinding;

import static de.rwth.idsg.steve.SteveConfiguration.CONFIG;

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

    @Nullable private static TLSClientParameters tlsClientParams;

    static {
        if (shouldInitSSL()) {
            tlsClientParams = new TLSClientParameters();
            tlsClientParams.setSSLSocketFactory(setupSSL());
        } else {
            tlsClientParams = null;
        }
    }

    private ClientProvider() { }

    public static <T> T createClient(Class<T> clazz, String endpointAddress) {
        JaxWsProxyFactoryBean bean = getBean(endpointAddress);
        bean.setServiceClass(clazz);
        T clientObject = clazz.cast(bean.create());

        if (tlsClientParams != null) {
            Client client = ClientProxy.getClient(clientObject);
            HTTPConduit http = (HTTPConduit) client.getConduit();
            http.setTlsClientParameters(tlsClientParams);
        }

        return clientObject;
    }

    private static JaxWsProxyFactoryBean getBean(String endpointAddress) {
        JaxWsProxyFactoryBean f = new JaxWsProxyFactoryBean();
        f.setBindingId(SOAPBinding.SOAP12HTTP_BINDING);
        f.getFeatures().add(LoggingFeatureProxy.INSTANCE.get());
        f.getFeatures().add(new WSAddressingFeature());
        f.setAddress(endpointAddress);
        return f;
    }

    private static boolean shouldInitSSL() {
        return CONFIG.getJetty().getKeyStorePath() != null && CONFIG.getJetty().getKeyStorePassword() != null;
    }

    private static SSLSocketFactory setupSSL() {
        SslContextFactory ssl = new SslContextFactory(CONFIG.getJetty().getKeyStorePath());
        ssl.setKeyStorePassword(CONFIG.getJetty().getKeyStorePassword());
        ssl.setKeyManagerPassword(CONFIG.getJetty().getKeyStorePassword());
        try {
            ssl.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return ssl.getSslContext().getSocketFactory();
    }
}
