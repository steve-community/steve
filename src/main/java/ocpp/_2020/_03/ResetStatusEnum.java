
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * This indicates whether the Charging Station is able to perform the reset.
 * 
 * 
 */
@Generated("jsonschema2pojo")
public enum ResetStatusEnum {

    ACCEPTED("Accepted"),
    REJECTED("Rejected"),
    SCHEDULED("Scheduled");
    private final String value;
    private final static Map<String, ResetStatusEnum> CONSTANTS = new HashMap<String, ResetStatusEnum>();

    static {
        for (ResetStatusEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    ResetStatusEnum(String value) {
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
    public static ResetStatusEnum fromValue(String value) {
        ResetStatusEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
