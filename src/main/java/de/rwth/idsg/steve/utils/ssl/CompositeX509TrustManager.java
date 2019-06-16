/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2019 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;

/**
 * Taken from https://github.com/1and1/CompositeJKS and adapted/improved.
 *
 * Merges multiple {@link X509TrustManager}s into a delegating composite.
 */
public class CompositeX509TrustManager implements X509TrustManager {

    private final List<X509TrustManager> children;

    public CompositeX509TrustManager(List<X509TrustManager> children) {
        this.children = children;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        checkTrusted(x -> x.checkClientTrusted(chain, authType));
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        checkTrusted(x -> x.checkServerTrusted(chain, authType));
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return children.stream()
                       .flatMap(x -> Arrays.stream(x.getAcceptedIssuers()))
                       .toArray(X509Certificate[]::new);
    }

    private void checkTrusted(ThrowingProcessor processor) throws CertificateException {
        CertificateException lastError = null;
        for (X509TrustManager manager : children) {
            try {
                processor.process(manager);
                return;
            } catch (CertificateException ex) {
                lastError = ex;
            }
        }

        if (lastError != null) {
            throw lastError;
        }
    }

    private interface ThrowingProcessor {

        void process(X509TrustManager manager) throws CertificateException;
    }
}
