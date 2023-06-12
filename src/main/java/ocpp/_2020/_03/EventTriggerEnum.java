
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Type of monitor that triggered this event, e.g. exceeding a threshold value.
 * 
 * 
 * 
 */
@Generated("jsonschema2pojo")
public enum EventTriggerEnum {

    ALERTING("Alerting"),
    DELTA("Delta"),
    PERIODIC("Periodic");
    private final String value;
    private final static Map<String, EventTriggerEnum> CONSTANTS = new HashMap<String, EventTriggerEnum>();

    static {
        for (EventTriggerEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    EventTriggerEnum(String value) {
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
    public static EventTriggerEnum fromValue(String value) {
        EventTriggerEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
