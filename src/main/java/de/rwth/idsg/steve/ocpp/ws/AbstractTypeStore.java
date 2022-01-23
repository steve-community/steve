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
package de.rwth.idsg.steve.ocpp.ws;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import de.rwth.idsg.ocpp.jaxb.RequestType;
import de.rwth.idsg.ocpp.jaxb.ResponseType;
import de.rwth.idsg.steve.ocpp.ws.data.ActionResponsePair;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 14.05.2018
 */
public abstract class AbstractTypeStore implements TypeStore {

    private static final String REQUEST_CLASS_SUFFIX = "Request";
    private static final String RESPONSE_CLASS_SUFFIX = "Response";

    private final Map<String, Class<? extends RequestType>> requestClassMap = new HashMap<>();
    private final Map<Class<? extends RequestType>, ActionResponsePair> actionResponseMap = new HashMap<>();

    public AbstractTypeStore(String packageForRequestClassMap,
                             String packageForActionResponseMap) {
        populateRequestClassMap(packageForRequestClassMap);
        populateActionResponseMap(packageForActionResponseMap);
    }

    @Override
    public Class<? extends RequestType> findRequestClass(String action) {
        return requestClassMap.get(action);
    }

    @Override
    public <T extends RequestType> ActionResponsePair findActionResponse(T requestPayload) {
        return actionResponseMap.get(requestPayload.getClass());
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private void populateRequestClassMap(String packageName) {
        Map<String, Class<RequestType>> classes = getClassesWithInterface(packageName, RequestType.class);
        for (Class<RequestType> clazz : classes.values()) {
            String action = getAction(clazz);
            Preconditions.checkNotNull(action);
            requestClassMap.put(action, clazz);
        }
    }

    private void populateActionResponseMap(String packageName) {
        Map<String, Class<RequestType>> requestClasses = getClassesWithInterface(packageName, RequestType.class);
        Map<String, Class<ResponseType>> responseClasses = getClassesWithInterface(packageName, ResponseType.class);

        for (Class<RequestType> requestClass : requestClasses.values()) {
            String action = getAction(requestClass);
            Preconditions.checkNotNull(action);

            String responseClassSimpleName = action + RESPONSE_CLASS_SUFFIX;
            Class<? extends ResponseType> responseClass = responseClasses.get(responseClassSimpleName);
            Preconditions.checkNotNull(responseClass);

            actionResponseMap.put(requestClass, new ActionResponsePair(action, responseClass));
        }
    }

    /**
     * @return <simple name of class, class>
     */
    @SuppressWarnings("unchecked")
    private static <INTERFACE, IMPL extends INTERFACE> Map<String, Class<IMPL>> getClassesWithInterface(
            String packageName, Class<INTERFACE> interfaceClass) {
        try {
            ImmutableSet<ClassPath.ClassInfo> classInfos =
                    ClassPath.from(Thread.currentThread().getContextClassLoader())
                             .getTopLevelClasses(packageName);

            Map<String, Class<IMPL>> map = new HashMap<>();
            for (ClassPath.ClassInfo classInfo : classInfos) {
                Class<?> clazz = classInfo.load();
                if (interfaceClass.isAssignableFrom(clazz)) {
                    map.put(clazz.getSimpleName(), (Class<IMPL>) clazz);
                }
            }
            return map;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getAction(Class<? extends RequestType> clazz) {
        String s = clazz.getSimpleName();
        if (s.endsWith(REQUEST_CLASS_SUFFIX)) {
            s = s.substring(0, s.length() - REQUEST_CLASS_SUFFIX.length());
        }
        return s;
    }
}
