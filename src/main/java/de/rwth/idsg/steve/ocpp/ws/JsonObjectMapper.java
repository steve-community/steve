package de.rwth.idsg.steve.ocpp.ws;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import de.rwth.idsg.steve.ocpp.ws.ocpp12.Ocpp12JacksonModule;
import de.rwth.idsg.steve.ocpp.ws.ocpp15.Ocpp15JacksonModule;
import de.rwth.idsg.steve.ocpp.ws.ocpp16.Ocpp16JacksonModule;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;

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

        // OCPP messages contain some mandatory primitive fields (like transactionId), that are not allowed
        // to be null. any misinterpretation/mapping of these fields like "null -> 0" is a mistake.
        //
        // true story: while testing with abusive-charge-point, it sends stopTransactions where transactionId=null
        // in communication flows, where a startTransaction before causes an Exception and we cannot send a regular
        // response with a transactionId, but an error message. if we do not fail early, it will fail at the database
        // level which we want to prevent.
        mapper.configure(FAIL_ON_NULL_FOR_PRIMITIVES, true);

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
