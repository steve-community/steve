package de.rwth.idsg.steve.ocpp.ws.ocpp12;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.rwth.idsg.steve.ocpp.ws.*;
import de.rwth.idsg.steve.ocpp.ws.pipeline.IncomingPipeline;
import de.rwth.idsg.steve.ocpp.ws.pipeline.Sender;
import de.rwth.idsg.steve.ocpp.ws.pipeline.Serializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 12.03.2015
 */
@Component
public class Ocpp12WebSocketEndpoint extends AbstractWebSocketEndpoint {

    @Autowired private Ocpp12CallHandler handler;
    @Autowired private Ocpp12TypeStore typeStore;

    @Autowired private Serializer serializer;
    @Autowired private Sender sender;
    @Autowired private ObjectMapper mapper;
    @Autowired private FutureResponseContextStore futureResponseContextStore;

    @PostConstruct
    public void init() {
        pipeline = new IncomingPipeline(mapper, futureResponseContextStore,
                                        typeStore, handler,
                                        serializer, sender);
    }
}
