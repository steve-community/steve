package de.rwth.idsg.steve.ocpp.ws.pipeline;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonCall;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 12.03.2015
 */
@Slf4j
@Component
public class Sender implements Stage {

    @Override
    public void process(CommunicationContext context) {
        TextMessage out = new TextMessage(context.getOutgoingString());
        try {
            context.getSession().sendMessage(out);
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
