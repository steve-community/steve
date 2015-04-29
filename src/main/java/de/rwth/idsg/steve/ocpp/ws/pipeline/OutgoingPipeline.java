package de.rwth.idsg.steve.ocpp.ws.pipeline;

import de.rwth.idsg.steve.ocpp.ws.FutureResponseContextStore;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * For outgoing CALLs, triggered by the user.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 27.03.2015
 */
@Component
@Slf4j
public class OutgoingPipeline implements Pipeline {

    @Autowired private FutureResponseContextStore futureResponseContextStore;
    @Autowired private Serializer serializer;
    @Autowired private Sender sender;

    @Override
    public void run(CommunicationContext context) {
        try {
            serializer.process(context);
            sender.process(context);

        } catch (Exception e) {
            log.error("Exception occurred", e);
            // Outgoing call failed due to technical problems. Pass the exception to handler to inform the user.
            context.getFutureResponseContext().getHandler().handleException(e);
            return;
        }

        // All went well, and the call is sent. Store the response context for later lookup.
        futureResponseContextStore.add(context.getChargeBoxId(),
                                       context.getOutgoingMessage().getMessageId(),
                                       context.getFutureResponseContext());
    }
}
