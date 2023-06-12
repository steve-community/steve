
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Status is OK if a value could be returned. Otherwise this will indicate the reason why a value could not be returned.
 * 
 * 
 */
@Generated("jsonschema2pojo")
public enum SetMonitoringStatusEnum {

    ACCEPTED("Accepted"),
    UNKNOWN_COMPONENT("UnknownComponent"),
    UNKNOWN_VARIABLE("UnknownVariable"),
    UNSUPPORTED_MONITOR_TYPE("UnsupportedMonitorType"),
    REJECTED("Rejected"),
    DUPLICATE("Duplicate");
    private final String value;
    private final static Map<String, SetMonitoringStatusEnum> CONSTANTS = new HashMap<String, SetMonitoringStatusEnum>();

    static {
        for (SetMonitoringStatusEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    SetMonitoringStatusEnum(String value) {
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
    public static SetMonitoringStatusEnum fromValue(String value) {
        SetMonitoringStatusEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
