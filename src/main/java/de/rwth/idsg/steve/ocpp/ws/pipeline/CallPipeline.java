package de.rwth.idsg.steve.ocpp.ws.pipeline;

import de.rwth.idsg.steve.ocpp.ws.FutureResponseContextStore;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * For outgoing CALLs, triggered by the user.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 27.03.2015
 */
@Component
@Slf4j
public class CallPipeline implements Pipeline {

    private final List<Stage> stages;

    @Autowired
    public CallPipeline(Serializer serializer, Sender sender, FutureResponseContextStore store) {
        // Order is important => Sequential execution of stages
        stages = Arrays.asList(
                serializer,
                sender,
                new CallStoreStage(store)
        );
    }

    @Override
    public void run(CommunicationContext context) {
        for (Stage stage : stages) {
            stage.process(context);
        }
    }
}
