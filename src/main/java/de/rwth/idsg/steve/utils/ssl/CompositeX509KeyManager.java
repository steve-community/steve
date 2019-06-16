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
