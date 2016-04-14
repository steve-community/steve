package de.rwth.idsg.steve.ocpp.ws.pipeline;

import de.rwth.idsg.steve.ocpp.ws.FutureResponseContextStore;
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
public class OutgoingCallPipeline extends AbstractPipeline {

    @Autowired
    public OutgoingCallPipeline(OutgoingPipeline outgoingPipeline, FutureResponseContextStore store) {
        // Order is important => Sequential execution of stages
        addStages(
                outgoingPipeline,
                new OutgoingCallStoreStage(store)
        );
    }
}
