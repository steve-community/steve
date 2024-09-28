
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * This indicates whether the Charging Station is able to accept this request.
 * 
 * 
 */
public enum GenericDeviceModelStatusEnum {

    ACCEPTED("Accepted"),
    REJECTED("Rejected"),
    NOT_SUPPORTED("NotSupported"),
    EMPTY_RESULT_SET("EmptyResultSet");
    private final String value;
    private final static Map<String, GenericDeviceModelStatusEnum> CONSTANTS = new HashMap<String, GenericDeviceModelStatusEnum>();

    static {
        for (GenericDeviceModelStatusEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    GenericDeviceModelStatusEnum(String value) {
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
    public static GenericDeviceModelStatusEnum fromValue(String value) {
        GenericDeviceModelStatusEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
