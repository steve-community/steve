
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * The type of this monitor, e.g. a threshold, delta or periodic monitor. 
 * 
 * 
 */
@Generated("jsonschema2pojo")
public enum MonitorEnum {

    UPPER_THRESHOLD("UpperThreshold"),
    LOWER_THRESHOLD("LowerThreshold"),
    DELTA("Delta"),
    PERIODIC("Periodic"),
    PERIODIC_CLOCK_ALIGNED("PeriodicClockAligned");
    private final String value;
    private final static Map<String, MonitorEnum> CONSTANTS = new HashMap<String, MonitorEnum>();

    static {
        for (MonitorEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    MonitorEnum(String value) {
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
    public static MonitorEnum fromValue(String value) {
        MonitorEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
