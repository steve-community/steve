package de.rwth.idsg.steve.ocpp.ws.custom;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 27.04.2015
 */
public interface EnumMixin {

    @JsonValue
    String value();
}
