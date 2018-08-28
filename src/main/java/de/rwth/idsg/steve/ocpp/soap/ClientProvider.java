package de.rwth.idsg.steve.ocpp.soap;

import com.oneandone.compositejks.CompositeX509KeyManager;
import com.oneandone.compositejks.CompositeX509TrustManager;
import com.oneandone.compositejks.KeyStoreLoader;
import com.oneandone.compositejks.SslContextUtils;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.xml.ws.soap.SOAPBinding;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

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
            char[] keyStorePwd = CONFIG.getJetty().getKeyStorePassword().toCharArray();
            KeyStore keyStore = KeyStoreLoader.fromStream(keyStorePath, keyStorePwd);
            ssl = buildMergedWithSystem(keyStore, keyStorePwd);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ssl.getSocketFactory();
    }

    /**
     * Because {@link SslContextUtils#buildMergedWithSystem(java.security.KeyStore)} does not propagate the param for
     * the password of the keystore and we got exceptions like "java.security.UnrecoverableKeyException: Password must not be null".
     *
     * While we are at it, we use the default algorithm instead of hard-coding 'SunX509'.
     */
    private static SSLContext buildMergedWithSystem(KeyStore keyStore, char[] keyStorePwd) throws GeneralSecurityException {
        String defaultAlgorithm = KeyManagerFactory.getDefaultAlgorithm();

        KeyManager[] keyManagers = {new CompositeX509KeyManager(
                SslContextUtils.getSystemKeyManager(defaultAlgorithm, keyStore, keyStorePwd),
                SslContextUtils.getSystemKeyManager(defaultAlgorithm, null, null))};

        TrustManager[] trustManagers = {new CompositeX509TrustManager(
                SslContextUtils.getSystemTrustManager(defaultAlgorithm, keyStore),
                SslContextUtils.getSystemTrustManager(defaultAlgorithm, null))};

        SSLContext context = SSLContext.getInstance("SSL");
        context.init(keyManagers, trustManagers, null);
        return context;
    }
}
