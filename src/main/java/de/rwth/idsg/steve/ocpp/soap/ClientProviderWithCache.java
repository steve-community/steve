package de.rwth.idsg.steve.ocpp.soap;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * http://cxf.apache.org/faq.html#FAQ-AreJAX-WSclientproxiesthreadsafe?
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
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
