package de.rwth.idsg.steve.ocpp.ws.custom;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ocpp.cs._2012._06.MeterValue;

import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 27.04.2015
 */
public abstract class MeterValue15Mixin {

    @JsonDeserialize(using = MeterValue15Deserializer.class)
    List<MeterValue> values;
}
