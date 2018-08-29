package de.rwth.idsg.steve.ocpp.soap;

import de.rwth.idsg.steve.utils.ssl.SslContextBuilder;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.ws.soap.SOAPBinding;

import static de.rwth.idsg.steve.SteveConfiguration.CONFIG;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 21.10.2015
 */
@Component
public class ClientProvider {

    @Nullable private TLSClientParameters tlsClientParams;

    @PostConstruct
    private void init() {
        if (shouldInitSSL()) {
            tlsClientParams = new TLSClientParameters();
            tlsClientParams.setSSLSocketFactory(setupSSL());
        } else {
            tlsClientParams = null;
        }
    }

    public <T> T createClient(Class<T> clazz, String endpointAddress) {
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
        SSLContext ssl;
        try {
            String keyStorePath = CONFIG.getJetty().getKeyStorePath();
            String keyStorePwd = CONFIG.getJetty().getKeyStorePassword();
            ssl = SslContextBuilder.builder()
                                   .keyStoreFromFile(keyStorePath, keyStorePwd)
                                   .usingTLS()
                                   .usingDefaultAlgorithm()
                                   .usingKeyManagerPasswordFromKeyStore()
                                   .buildMergedWithSystem();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ssl.getSocketFactory();
    }
}
