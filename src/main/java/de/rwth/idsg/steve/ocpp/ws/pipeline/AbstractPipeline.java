package de.rwth.idsg.steve.ocpp.ws.pipeline;

import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;

import java.util.Arrays;
import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 14.04.2016
 */
abstract class AbstractPipeline implements Pipeline {

    private List<Stage> stages;

    @Override
    public void addStages(Stage... stages) {
        this.stages = Arrays.asList(stages);
    }

    @Override
    public void process(CommunicationContext context) {
        for (Stage stage : stages) {
            stage.process(context);
        }
    }
}

