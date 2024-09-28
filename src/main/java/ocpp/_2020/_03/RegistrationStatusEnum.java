
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * This contains whether the Charging Station has been registered
 * within the CSMS.
 * 
 * 
 */
public enum RegistrationStatusEnum {

    ACCEPTED("Accepted"),
    PENDING("Pending"),
    REJECTED("Rejected");
    private final String value;
    private final static Map<String, RegistrationStatusEnum> CONSTANTS = new HashMap<String, RegistrationStatusEnum>();

    static {
        for (RegistrationStatusEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    RegistrationStatusEnum(String value) {
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
    public static RegistrationStatusEnum fromValue(String value) {
        RegistrationStatusEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
