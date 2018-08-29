package de.rwth.idsg.steve.utils.ssl;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 29.08.2018
 */
public final class SslContextBuilder implements KeyStoreStep, KeyManagerAlgorithmStep, ProtocolStep, SslContextStep {

    private char[] passwordAsCharArray;
    private String socketProtocol;
    private String keyManagerAlgorithm;
    private KeyStore keyStore;

    private SslContextBuilder() { }

    public static KeyStoreStep builder() {
        return new SslContextBuilder();
    }

    @Override
    public ProtocolStep keyStoreFromStream(InputStream stream, String password) throws IOException, GeneralSecurityException {
        passwordAsCharArray = (password == null) ? null : password.toCharArray();
        keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(stream, passwordAsCharArray);
        return this;
    }

    @Override
    public SslContextStep usingAlgorithm(String keyManagerAlgorithm) {
        this.keyManagerAlgorithm = (keyManagerAlgorithm == null) ? KeyManagerFactory.getDefaultAlgorithm() : keyManagerAlgorithm;
        return this;
    }

    @Override
    public KeyManagerAlgorithmStep usingProtocol(String socketProtocol) {
        this.socketProtocol = socketProtocol;
        return this;
    }

    @Override
    public SSLContext buildMergedWithSystem() throws GeneralSecurityException {
        String defaultAlgorithm = KeyManagerFactory.getDefaultAlgorithm();

        KeyManager[] keyManagers = {
                new CompositeX509KeyManager(
                        union(
                                getSystemKeyManagers(keyManagerAlgorithm, keyStore, passwordAsCharArray),
                                getSystemKeyManagers(defaultAlgorithm, null, null)
                        )
                )
        };

        TrustManager[] trustManagers = {
                new CompositeX509TrustManager(
                        union(
                                getSystemTrustManagers(keyManagerAlgorithm, keyStore),
                                getSystemTrustManagers(defaultAlgorithm, null)
                        )
                )
        };

        SSLContext context = SSLContext.getInstance(socketProtocol);
        context.init(keyManagers, trustManagers, null);
        return context;
    }

    private static List<X509KeyManager> getSystemKeyManagers(String algorithm, KeyStore keystore, char[] password)
            throws GeneralSecurityException {
        KeyManagerFactory factory = KeyManagerFactory.getInstance(algorithm);
        factory.init(keystore, password);
        return Arrays.stream(factory.getKeyManagers())
                     .filter(x -> x instanceof X509KeyManager)
                     .map(x -> (X509KeyManager) x)
                     .collect(Collectors.toList());
    }

    private static List<X509TrustManager> getSystemTrustManagers(String algorithm, KeyStore keystore)
            throws GeneralSecurityException {
        TrustManagerFactory factory = TrustManagerFactory.getInstance(algorithm);
        factory.init(keystore);
        return Arrays.stream(factory.getTrustManagers())
                     .filter(x -> x instanceof X509TrustManager)
                     .map(x -> (X509TrustManager) x)
                     .collect(Collectors.toList());
    }

    private static <T> List<T> union(List<T> col1, List<T> col2) {
        List<T> newList = new ArrayList<>(col1);
        newList.addAll(col2);
        return newList;
    }
}

