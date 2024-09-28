
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * The Charging Station will indicate if it was
 * able to process the request
 * 
 * 
 */
public enum GenericStatusEnum {

    ACCEPTED("Accepted"),
    REJECTED("Rejected");
    private final String value;
    private final static Map<String, GenericStatusEnum> CONSTANTS = new HashMap<String, GenericStatusEnum>();

    static {
        for (GenericStatusEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    GenericStatusEnum(String value) {
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
    public static GenericStatusEnum fromValue(String value) {
        GenericStatusEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
