package de.rwth.idsg.steve.ocpp.ws;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.RequestType;
import de.rwth.idsg.steve.ocpp.ws.data.ActionResponsePair;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import de.rwth.idsg.steve.ocpp.ws.data.FutureResponseContext;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonCall;
import de.rwth.idsg.steve.ocpp.ws.pipeline.OutgoingCallPipeline;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
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

    @Autowired private OutgoingCallPipeline outgoingCallPipeline;

    @Setter private TypeStore typeStore;
    @Setter private AbstractWebSocketEndpoint endpoint;

    public void runPipeline(ChargePointSelect cps, CommunicationTask task) {
        runPipeline(cps.getChargeBoxId(), task);
    }

    /**
     * Just a wrapper to make try-catch block and exception handling stand out
     */
    public void runPipeline(String chargeBoxId, CommunicationTask task) {
        try {
            run(chargeBoxId, task);
        } catch (Exception e) {
            log.error("Exception occurred", e);
            // Outgoing call failed due to technical problems. Pass the exception to handler to inform the user
            task.defaultCallback().failed(chargeBoxId, e);
        }
    }

    /**
     * Actual processing
     */
    private void run(String chargeBoxId, CommunicationTask task) {
        RequestType request = task.getRequest();

        String messageId = UUID.randomUUID().toString();
        ActionResponsePair pair = typeStore.findActionResponse(request);
        if (pair == null) {
            throw new SteveException("Action name is not found");
        }

        OcppJsonCall call = new OcppJsonCall();
        call.setMessageId(messageId);
        call.setPayload(request);
        call.setAction(pair.getAction());

        FutureResponseContext frc = new FutureResponseContext(task, pair.getResponseClass());

        CommunicationContext context = new CommunicationContext(endpoint.getSession(chargeBoxId), chargeBoxId);
        context.setOutgoingMessage(call);
        context.setFutureResponseContext(frc);

        outgoingCallPipeline.process(context);
    }
}
