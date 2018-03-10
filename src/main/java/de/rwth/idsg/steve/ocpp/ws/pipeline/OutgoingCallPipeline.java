package de.rwth.idsg.steve.ocpp.ws.pipeline;

import de.rwth.idsg.steve.ocpp.ws.FutureResponseContextStore;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * For outgoing CALLs, triggered by the user.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 27.03.2015
 */
@Component
@Slf4j
public class OutgoingCallPipeline implements Consumer<CommunicationContext> {

    private final Consumer<CommunicationContext> chainedConsumers;

    @Autowired
    public OutgoingCallPipeline(FutureResponseContextStore store) {
        chainedConsumers = OutgoingCallPipeline.start(Serializer.INSTANCE)
                                               .andThen(Sender.INSTANCE)
                                               .andThen(saveInStore(store));
    }

    @Override
    public void accept(CommunicationContext ctx) {
        chainedConsumers.accept(ctx);
    }

    private static Consumer<CommunicationContext> saveInStore(FutureResponseContextStore store) {
        return context -> {
            // All went well, and the call is sent. Store the response context for later lookup.
            store.add(context.getSession(),
                      context.getOutgoingMessage().getMessageId(),
                      context.getFutureResponseContext());
        };
    }

    private static Consumer<CommunicationContext> start(Consumer<CommunicationContext> starter) {
        return starter;
    }

}
