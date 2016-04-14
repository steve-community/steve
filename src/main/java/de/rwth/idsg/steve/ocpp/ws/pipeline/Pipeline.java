package de.rwth.idsg.steve.ocpp.ws.pipeline;

/**
 * Manages the stages and executes them.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 17.03.2015
 */
public interface Pipeline extends Stage {
    void addStages(Stage... stages);
}
