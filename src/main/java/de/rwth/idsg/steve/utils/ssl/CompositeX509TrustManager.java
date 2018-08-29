/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 1&1 Internet SE
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
