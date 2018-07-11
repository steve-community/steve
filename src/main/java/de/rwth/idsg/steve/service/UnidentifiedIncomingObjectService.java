package de.rwth.idsg.steve.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.rwth.idsg.steve.service.dto.UnidentifiedIncomingObject;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * The name of this class was inspired by UFO (Unidentified flying object) and enterprise software development.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 20.03.2018
 */
@Slf4j
public class UnidentifiedIncomingObjectService {

    private final Object changeLock = new Object();

    private final Cache<String, UnidentifiedIncomingObject> objectsHolder;

    public UnidentifiedIncomingObjectService(int maxSize) {
        objectsHolder = CacheBuilder.newBuilder()
                                    .maximumSize(maxSize)
                                    .build();
    }

    public List<UnidentifiedIncomingObject> getObjects() {
        return objectsHolder.asMap()
                            .values()
                            .stream()
                            .sorted(Comparator.comparingInt(UnidentifiedIncomingObject::getNumberOfAttempts).reversed())
                            .collect(Collectors.toList());
    }

    public void processNewUnidentified(String key) {
        synchronized (changeLock) {
            try {
                objectsHolder.get(key, () -> new UnidentifiedIncomingObject(key))
                             .updateStats();
            } catch (ExecutionException e) {
                log.error("Error occurred", e);
            }
        }
    }

    public void remove(String key) {
        synchronized (changeLock) {
            try {
                objectsHolder.invalidate(key);
            } catch (Exception e) {
                log.error("Error occurred", e);
            }
        }
    }

    public void removeAll(Iterable<String> keys) {
        synchronized (changeLock) {
            try {
                objectsHolder.invalidateAll(keys);
            } catch (Exception e) {
                log.error("Error occurred", e);
            }
        }
    }
}
