/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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
package de.rwth.idsg.steve.ocpp.soap;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * http://cxf.apache.org/faq.html#FAQ-AreJAX-WSclientproxiesthreadsafe?
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 27.08.2018
 */
public class ClientProviderWithCache<T> {

    private final ClientProvider delegate;
    private final Cache<String, T> cache;

    public ClientProviderWithCache(ClientProvider delegate) {
        this.delegate = delegate;
        this.cache = CacheBuilder.newBuilder()
                                 .maximumSize(500)
                                 .expireAfterAccess(1, TimeUnit.HOURS)
                                 .build();
    }

    public T createClient(Class<T> clazz, String endpointAddress) {
        try {
            return cache.get(endpointAddress, () -> delegate.createClient(clazz, endpointAddress));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
