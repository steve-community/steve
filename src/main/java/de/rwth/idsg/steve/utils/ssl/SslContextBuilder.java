/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2020 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
 * All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
public final class SslContextBuilder implements
        KeyStoreStep, KeyManagerAlgorithmStep, KeyManagerPasswordStep, ProtocolStep, SslContextStep {

    private char[] keyStorePassword;
    private KeyStore keyStore;

    private String socketProtocol;
    private String keyManagerAlgorithm;
    private char[] keyManagerPassword; // because it is possible that KeyManagerFactory pwd != KeyStore pwd

    private SslContextBuilder() { }

    public static KeyStoreStep builder() {
        return new SslContextBuilder();
    }

    @Override
    public ProtocolStep keyStoreFromStream(InputStream stream, String password) throws IOException, GeneralSecurityException {
        keyStorePassword = (password == null) ? null : password.toCharArray();
        keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(stream, keyStorePassword);
        return this;
    }

    @Override
    public KeyManagerAlgorithmStep usingProtocol(String socketProtocol) {
        this.socketProtocol = socketProtocol;
        return this;
    }

    /**
     * @param keyManagerAlgorithm The algorithm for the custom key store. Defaults to system one, if null.
     */
    @Override
    public KeyManagerPasswordStep usingAlgorithm(String keyManagerAlgorithm) {
        this.keyManagerAlgorithm = (keyManagerAlgorithm == null) ? KeyManagerFactory.getDefaultAlgorithm() : keyManagerAlgorithm;
        return this;
    }

    /**
     * @param keyManagerPwd The password to init {@link KeyManagerFactory}. Defaults to KeyStore password, if null.
     */
    @Override
    public SslContextStep usingKeyManagerPassword(String keyManagerPwd) {
        this.keyManagerPassword = (keyManagerPwd == null) ? keyStorePassword : keyManagerPwd.toCharArray();
        return this;
    }

    @Override
    public SSLContext buildMergedWithSystem() throws GeneralSecurityException {
        String defaultAlgorithm = KeyManagerFactory.getDefaultAlgorithm();

        KeyManager[] keyManagers = {
                new CompositeX509KeyManager(
                        union(
                                getSystemKeyManagers(keyManagerAlgorithm, keyStore, keyManagerPassword),
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
