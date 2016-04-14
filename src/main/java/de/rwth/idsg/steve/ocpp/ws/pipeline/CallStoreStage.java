package de.rwth.idsg.steve.ocpp.ws.pipeline;

import de.rwth.idsg.steve.ocpp.ws.FutureResponseContextStore;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 13.04.2016
 */
class CallStoreStage implements Stage {

    private final FutureResponseContextStore store;

    CallStoreStage(FutureResponseContextStore store) {
        this.store = store;
    }

    @Override
    public void process(CommunicationContext context) {
        // All went well, and the call is sent. Store the response context for later lookup.
        store.add(context.getSession(),
                  context.getOutgoingMessage().getMessageId(),
                  context.getFutureResponseContext());
    }
}
