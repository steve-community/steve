
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * This contains the type of update (full or differential) of this request.
 * 
 * 
 */
public enum UpdateEnum {

    DIFFERENTIAL("Differential"),
    FULL("Full");
    private final String value;
    private final static Map<String, UpdateEnum> CONSTANTS = new HashMap<String, UpdateEnum>();

    static {
        for (UpdateEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    UpdateEnum(String value) {
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
    public static UpdateEnum fromValue(String value) {
        UpdateEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
