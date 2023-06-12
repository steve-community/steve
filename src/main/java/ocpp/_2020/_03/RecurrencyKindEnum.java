
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Charging_ Profile. Recurrency_ Kind. Recurrency_ Kind_ Code
 * urn:x-oca:ocpp:uid:1:569233
 * Indicates the start point of a recurrence.
 * 
 * 
 */
@Generated("jsonschema2pojo")
public enum RecurrencyKindEnum {

    DAILY("Daily"),
    WEEKLY("Weekly");
    private final String value;
    private final static Map<String, RecurrencyKindEnum> CONSTANTS = new HashMap<String, RecurrencyKindEnum>();

    static {
        for (RecurrencyKindEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    RecurrencyKindEnum(String value) {
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
    public static RecurrencyKindEnum fromValue(String value) {
        RecurrencyKindEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
