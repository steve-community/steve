
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * If provided the Charging Station shall return Display Messages with the given state only. 
 * 
 * 
 */
@Generated("jsonschema2pojo")
public enum MessageStateEnum {

    CHARGING("Charging"),
    FAULTED("Faulted"),
    IDLE("Idle"),
    UNAVAILABLE("Unavailable");
    private final String value;
    private final static Map<String, MessageStateEnum> CONSTANTS = new HashMap<String, MessageStateEnum>();

    static {
        for (MessageStateEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    MessageStateEnum(String value) {
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
    public static MessageStateEnum fromValue(String value) {
        MessageStateEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
