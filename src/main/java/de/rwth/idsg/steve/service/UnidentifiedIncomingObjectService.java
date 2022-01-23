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
 * @author Sevket Goekay <sevketgokay@gmail.com>
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
