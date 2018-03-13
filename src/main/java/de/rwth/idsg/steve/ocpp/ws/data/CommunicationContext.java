package de.rwth.idsg.steve.ocpp.ws.data;

import de.rwth.idsg.steve.handler.OcppJsonResponseHandler;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

/**
 * Default holder/context of incoming and outgoing messages.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 23.03.2015
 */
@Getter
@Setter
public class CommunicationContext {
    private WebSocketSession session;
    private String chargeBoxId;

    private String incomingString;
    private String outgoingString;

    private OcppJsonMessage incomingMessage;
    private OcppJsonMessage outgoingMessage;

    private OcppJsonResponseHandler handler;
    private FutureResponseContext futureResponseContext;

    public boolean isSetOutgoingError() {
        return (outgoingMessage != null) && (outgoingMessage instanceof OcppJsonError);
    }
}
