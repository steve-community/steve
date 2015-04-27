package de.rwth.idsg.steve.ocpp.ws.custom;

import com.fasterxml.jackson.annotation.JsonProperty;
import ocpp.cs._2012._06.MeterValue;

import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 27.04.2015
 */
public interface MeterValue15Mixin {

    @JsonProperty("values")
    List<MeterValue.Value> getValue();
}
