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

import javax.net.ssl.X509KeyManager;
import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Taken from https://github.com/1and1/CompositeJKS and adapted/improved.
 *
 * Merges multiple {@link X509KeyManager}s into a delegating composite.
 */
public class CompositeX509KeyManager implements X509KeyManager {

    private final List<X509KeyManager> children;

    public CompositeX509KeyManager(List<X509KeyManager> children) {
        this.children = children;
    }

    @Override
    public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
        return getFirstNonNull(x -> x.chooseClientAlias(keyType, issuers, socket));
    }

    @Override
    public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
        return getFirstNonNull(x -> x.chooseServerAlias(keyType, issuers, socket));
    }

    @Override
    public X509Certificate[] getCertificateChain(String alias) {
        return getFirstNonNull(x -> x.getCertificateChain(alias));
    }

    @Override
    public PrivateKey getPrivateKey(String alias) {
        return getFirstNonNull(x -> x.getPrivateKey(alias));
    }

    @Override
    public String[] getClientAliases(String keyType, Principal[] issuers) {
        return merge(x -> x.getClientAliases(keyType, issuers));
    }

    @Override
    public String[] getServerAliases(String keyType, Principal[] issuers) {
        return merge(x -> x.getServerAliases(keyType, issuers));
    }

    private <TOut> TOut getFirstNonNull(Function<X509KeyManager, TOut> map) {
        return children.stream()
                       .map(map)
                       .filter(Objects::nonNull)
                       .findFirst()
                       .orElse(null);
    }

    private String[] merge(Function<X509KeyManager, String[]> map) {
        return children.stream()
                       .flatMap(x -> Arrays.stream(map.apply(x)))
                       .toArray(String[]::new);
    }
}
