package de.rwth.idsg.steve.ocpp.ws.pipeline;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.rwth.idsg.steve.ocpp.ws.FutureResponseContextStore;
import de.rwth.idsg.steve.ocpp.ws.TypeStore;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonCall;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonError;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonMessage;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonResult;
import lombok.extern.slf4j.Slf4j;

/**
 * For all incoming message types.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 23.03.2015
 */
@Slf4j
public class IncomingPipeline extends AbstractPipeline {
    private final Deserializer deserializer;
    private final AbstractCallHandler handler;
    private final OutgoingPipeline outgoingPipeline;

    public IncomingPipeline(ObjectMapper mapper, FutureResponseContextStore futureResponseContextStore,
                            TypeStore typeStore, AbstractCallHandler handler,
                            OutgoingPipeline outgoingPipeline) {
        this.deserializer = new Deserializer(mapper, futureResponseContextStore, typeStore);
        this.handler = handler;
        this.outgoingPipeline = outgoingPipeline;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void process(CommunicationContext context) {
        deserializer.process(context);

        // When the incoming could not be deserialized
        if (context.isSetOutgoingError()) {
            outgoingPipeline.process(context);
            return;
        }

        OcppJsonMessage msg = context.getIncomingMessage();

        if (msg instanceof OcppJsonCall) {
            handler.process(context);
            outgoingPipeline.process(context);

        } else if (msg instanceof OcppJsonResult) {
            OcppJsonResult result = (OcppJsonResult) msg;
            context.getHandler().handleResult(result.getPayload());

        } else if (msg instanceof OcppJsonError) {
            OcppJsonError result = (OcppJsonError) msg;
            context.getHandler().handleError(result);
        }
    }

}
