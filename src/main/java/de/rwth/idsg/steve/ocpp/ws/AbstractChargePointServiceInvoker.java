package de.rwth.idsg.steve.ocpp.ws;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.handler.OcppResponseHandler;
import de.rwth.idsg.steve.ocpp.RequestType;
import de.rwth.idsg.steve.ocpp.ws.data.ActionResponsePair;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import de.rwth.idsg.steve.ocpp.ws.data.FutureResponseContext;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonCall;
import de.rwth.idsg.steve.ocpp.ws.pipeline.OutgoingPipeline;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 20.03.2015
 */
public abstract class AbstractChargePointServiceInvoker {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired private OutgoingPipeline outgoingPipeline;

    @Setter private TypeStore typeStore;
    @Setter private AbstractWebSocketEndpoint endpoint;

    /**
     * Just a wrapper to make try-catch block and exception handling stand out
     */
    public void runPipeline(String chargeBoxId, OcppResponseHandler handler) {
        try {
            run(chargeBoxId, handler);
        } catch (Exception e) {
            log.error("Exception occurred", e);
            // Outgoing call failed due to technical problems. Pass the exception to handler to inform the user
            handler.handleException(e);
        }
    }

    /**
     * Actual processing
     */
    private void run(String chargeBoxId, OcppResponseHandler handler) {
        RequestType request = handler.getRequest();

        String messageId = UUID.randomUUID().toString();
        ActionResponsePair pair = typeStore.findActionResponse(request);
        if (pair == null) {
            throw new SteveException("Action name is not found");
        }

        OcppJsonCall call = new OcppJsonCall();
        call.setMessageId(messageId);
        call.setPayload(request);
        call.setAction(pair.getAction());

        FutureResponseContext frc = new FutureResponseContext(handler, pair.getResponseClass());

        CommunicationContext context = new CommunicationContext();
        context.setChargeBoxId(chargeBoxId);
        context.setOutgoingMessage(call);
        context.setFutureResponseContext(frc);
        context.setSession(endpoint.getSession(chargeBoxId));

        outgoingPipeline.run(context);
    }
}
