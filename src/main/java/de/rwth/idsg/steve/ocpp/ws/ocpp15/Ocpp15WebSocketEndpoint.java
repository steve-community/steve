package de.rwth.idsg.steve.ocpp.ws.ocpp15;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.rwth.idsg.steve.ocpp.ws.AbstractWebSocketEndpoint;
import de.rwth.idsg.steve.ocpp.ws.FutureResponseContextStore;
import de.rwth.idsg.steve.ocpp.ws.pipeline.Deserializer;
import de.rwth.idsg.steve.ocpp.ws.pipeline.IncomingPipeline;
import de.rwth.idsg.steve.ocpp.ws.pipeline.OutgoingPipeline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 12.03.2015
 */
@Component
public class Ocpp15WebSocketEndpoint extends AbstractWebSocketEndpoint {

    @Autowired private Ocpp15CallHandler handler;
    @Autowired private Ocpp15TypeStore typeStore;

    @Autowired private OutgoingPipeline outgoingPipeline;
    @Autowired private ObjectMapper mapper;
    @Autowired private FutureResponseContextStore futureResponseContextStore;

    @PostConstruct
    public void init() {
        Deserializer deserializer = new Deserializer(mapper, futureResponseContextStore, typeStore);
        IncomingPipeline pipeline = new IncomingPipeline(deserializer, handler, outgoingPipeline);
        super.init(pipeline);
    }
}
