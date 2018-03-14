package de.rwth.idsg.steve.ocpp.ws;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import de.rwth.idsg.steve.ocpp.ws.ocpp12.Ocpp12JacksonModule;
import de.rwth.idsg.steve.ocpp.ws.ocpp15.Ocpp15JacksonModule;
import de.rwth.idsg.steve.ocpp.ws.ocpp16.Ocpp16JacksonModule;

/**
 * Because ObjectMapper can and should be reused, if config does not change after init.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 10.03.2018
 */
public enum JsonObjectMapper {
    INSTANCE;

    private final ObjectMapper mapper;

    JsonObjectMapper() {
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

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
