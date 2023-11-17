
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Transaction. State. Transaction_ State_ Code
 * urn:x-oca:ocpp:uid:1:569419
 * Current charging state, is required when state
 * has changed.
 * 
 * 
 */
@Generated("jsonschema2pojo")
public enum ChargingStateEnum {

    CHARGING("Charging"),
    EV_CONNECTED("EVConnected"),
    SUSPENDED_EV("SuspendedEV"),
    SUSPENDED_EVSE("SuspendedEVSE"),
    IDLE("Idle");
    private final String value;
    private final static Map<String, ChargingStateEnum> CONSTANTS = new HashMap<String, ChargingStateEnum>();

    static {
        for (ChargingStateEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    ChargingStateEnum(String value) {
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
    public static ChargingStateEnum fromValue(String value) {
        ChargingStateEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
