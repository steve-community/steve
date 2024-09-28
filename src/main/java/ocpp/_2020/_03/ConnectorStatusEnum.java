
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * This contains the current status of the Connector.
 * 
 * 
 */
public enum ConnectorStatusEnum {

    AVAILABLE("Available"),
    OCCUPIED("Occupied"),
    RESERVED("Reserved"),
    UNAVAILABLE("Unavailable"),
    FAULTED("Faulted");
    private final String value;
    private final static Map<String, ConnectorStatusEnum> CONSTANTS = new HashMap<String, ConnectorStatusEnum>();

    static {
        for (ConnectorStatusEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    ConnectorStatusEnum(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static ConnectorStatusEnum fromValue(String value) {
        ConnectorStatusEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
