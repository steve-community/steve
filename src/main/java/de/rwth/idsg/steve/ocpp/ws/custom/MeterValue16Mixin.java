package de.rwth.idsg.steve.ocpp.ws.custom;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ocpp.cs._2015._10.MeterValue;

import java.util.List;

public class MeterValue16Mixin
{
    @JsonDeserialize(using = MeterValue16Deserializer.class)
    List<MeterValue> values;
}
