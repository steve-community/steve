package de.rwth.idsg.steve.ocpp.ws.pipeline;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.ocpp.ws.WebSocketLogger;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonCall;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * This class should remain stateless.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 12.03.2015
 */
@Slf4j
public enum Sender implements Consumer<CommunicationContext> {
    INSTANCE;

    @Override
    public void accept(CommunicationContext context) {
        String outgoingString = context.getOutgoingString();
        String chargeBoxId = context.getChargeBoxId();
        WebSocketSession session = context.getSession();

        WebSocketLogger.sending(chargeBoxId, session, outgoingString);

        TextMessage out = new TextMessage(outgoingString);
        try {
            session.sendMessage(out);
        } catch (IOException e) {

            // Do NOT swallow exceptions for outgoing CALLs. For others just log.
            if (context.getOutgoingMessage() instanceof OcppJsonCall) {
                throw new SteveException(e.getMessage());
            } else {
                log.error("Could not send the outgoing message", e);
            }
        }
    }
}
