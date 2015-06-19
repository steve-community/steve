package de.rwth.idsg.steve.ocpp.ws.pipeline;

import de.rwth.idsg.steve.ocpp.RequestType;
import de.rwth.idsg.steve.ocpp.ResponseType;
import de.rwth.idsg.steve.ocpp.ws.ErrorFactory;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonCall;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 17.03.2015
 */
public abstract class AbstractCallHandler implements Stage {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void process(CommunicationContext context) {
        OcppJsonCall call = (OcppJsonCall) context.getIncomingMessage();
        String messageId = call.getMessageId();

        ResponseType response;
        try {
            response = dispatch(call.getPayload(), context.getChargeBoxId());
        } catch (Exception e) {
            log.error("Exception occurred", e);
            context.setOutgoingMessage(ErrorFactory.payloadProcessingError(messageId, e.getMessage()));
            return;
        }

        OcppJsonResult result = new OcppJsonResult();
        result.setPayload(response);
        result.setMessageId(messageId);
        context.setOutgoingMessage(result);
    }

    public ResponseType dispatch(RequestType params, String chargeBoxId) {
        // Override this method!
        return null;
    }
}
