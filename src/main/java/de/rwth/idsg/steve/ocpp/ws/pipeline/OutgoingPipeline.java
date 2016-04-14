package de.rwth.idsg.steve.ocpp.ws.pipeline;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Because serializer and sender stages are used at multiple places as part of other pipelines. With this, we can
 * bundle and embed them as one item in other pipelines.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 14.04.2016
 */
@Component
public class OutgoingPipeline extends AbstractPipeline {

    @Autowired
    public OutgoingPipeline(Serializer serializer, Sender sender) {
        addStages(
                serializer,
                sender
        );
    }
}
