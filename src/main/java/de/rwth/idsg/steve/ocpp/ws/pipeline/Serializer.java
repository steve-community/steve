package de.rwth.idsg.steve.ocpp.ws.pipeline;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.ocpp.JsonObjectMapper;
import de.rwth.idsg.steve.ocpp.ws.ErrorFactory;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import de.rwth.idsg.steve.ocpp.ws.data.MessageType;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonCall;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonError;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonMessage;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Outgoing OcppJsonMessage --> String.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 17.03.2015
 */
@Slf4j
@Component
public class Serializer implements Consumer<CommunicationContext> {

    private final ObjectMapper mapper = JsonObjectMapper.INSTANCE.getMapper();

    @Override
    public void accept(CommunicationContext context) {
        OcppJsonMessage message = context.getOutgoingMessage();

        ArrayNode str;
        MessageType messageType = message.getMessageType();
        switch (messageType) {
            case CALL:
                str = handleCall((OcppJsonCall) message);
                break;

            case CALL_RESULT:
                str = handleResult((OcppJsonResult) message);
                break;

            case CALL_ERROR:
                str = handleError((OcppJsonError) message);
                break;

            default:
                throw new SteveException("Unknown enum type");
        }

        try {
            String result = mapper.writeValueAsString(str);
            context.setOutgoingString(result);
        } catch (IOException e) {
            throw new SteveException("The outgoing message could not be serialized", e);
        }
    }

    // -------------------------------------------------------------------------
    // Private Helpers
    // -------------------------------------------------------------------------

    /**
     * Do NOT catch and handle exceptions for outgoing CALLs. Do NOT send the message.
     * Let the processing fail and acknowledge the user.
     */
    private ArrayNode handleCall(OcppJsonCall call) {
        JsonNode payloadNode;
        try {
            payloadNode = mapper.valueToTree(call.getPayload());
        } catch (IllegalArgumentException e) {
            throw new SteveException("The payload of the outgoing call could not be converted to JSON", e);
        }

        return mapper.createArrayNode()
                     .add(call.getMessageType().getTypeNr())
                     .add(call.getMessageId())
                     .add(call.getAction())
                     .add(payloadNode);
    }

    /**
     * Catch exceptions and wrap them in outgoing ERRORs for outgoing RESPONSEs.
     */
    private ArrayNode handleResult(OcppJsonResult result) {
        JsonNode payloadNode;
        try {
            payloadNode = mapper.valueToTree(result.getPayload());
        } catch (IllegalArgumentException e) {
            log.error("Exception occurred", e);
            return handleError(ErrorFactory.payloadSerializeError(result.getMessageId(), e.getMessage()));
        }

        return mapper.createArrayNode()
                     .add(result.getMessageType().getTypeNr())
                     .add(result.getMessageId())
                     .add(payloadNode);
    }

    /**
     * No exception to catch during serialization, since the fields of the error are simple Strings.
     */
    private ArrayNode handleError(OcppJsonError error) {
        // From spec:
        // ErrorDescription - Should be filled in if possible, otherwise a clear empty string "".
        String description;
        if (error.isSetDescription()) {
            description = error.getErrorDescription();
        } else {
            description = "";
        }

        // From soec:
        // ErrorDetails - This JSON object describes error details in an undefined way.
        // If there are no error details you should fill in an empty object {}, missing or null is not allowed
        JsonNode detailsNode;
        if (error.isSetDetails()) {
            detailsNode = mapper.getNodeFactory().textNode(error.toStringErrorDetails());
        } else {
            detailsNode = mapper.createObjectNode();
        }

        return mapper.createArrayNode()
                     .add(error.getMessageType().getTypeNr())
                     .add(error.getMessageId())
                     .add(error.getErrorCode().name())
                     .add(description)
                     .add(detailsNode);
    }
}
