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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import de.rwth.idsg.steve.ocpp.ws.ocpp12.Ocpp12JacksonModule;
import de.rwth.idsg.steve.ocpp.ws.ocpp15.Ocpp15JacksonModule;
import de.rwth.idsg.steve.ocpp.ws.ocpp16.Ocpp16JacksonModule;

import static com.fasterxml.jackson.core.JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;

/**
 * Because ObjectMapper can and should be reused, if config does not change after init.
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 10.03.2018
 */
public enum JsonObjectMapper {
    INSTANCE;

    private final ObjectMapper mapper;

    JsonObjectMapper() {
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // OCPP messages contain some mandatory primitive fields (like transactionId), that are not allowed
        // to be null. any misinterpretation/mapping of these fields like "null -> 0" is a mistake.
        //
        // true story: while testing with abusive-charge-point, it sends stopTransactions where transactionId=null
        // in communication flows, where a startTransaction before causes an Exception and we cannot send a regular
        // response with a transactionId, but an error message. if we do not fail early, it will fail at the database
        // level which we want to prevent.
        mapper.configure(FAIL_ON_NULL_FOR_PRIMITIVES, true);

        mapper.configure(WRITE_BIGDECIMAL_AS_PLAIN, true);

        mapper.registerModule(new Ocpp12JacksonModule());
        mapper.registerModule(new Ocpp15JacksonModule());
        mapper.registerModule(new Ocpp16JacksonModule());

        mapper.setAnnotationIntrospector(
                AnnotationIntrospector.pair(
                        new JacksonAnnotationIntrospector(),
                        new JaxbAnnotationIntrospector(mapper.getTypeFactory())
                )
        );
    }

    public ObjectMapper getMapper() {
        return mapper;
    }
}
