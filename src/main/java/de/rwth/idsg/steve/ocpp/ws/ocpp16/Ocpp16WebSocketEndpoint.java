/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rwth.idsg.steve.ocpp.ws.ocpp16;

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
 *
 * @author david
 */

@Component
public class Ocpp16WebSocketEndpoint extends AbstractWebSocketEndpoint
{
    @Autowired private Ocpp16CallHandler handler;
    @Autowired private Ocpp16TypeStore typeStore;

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
