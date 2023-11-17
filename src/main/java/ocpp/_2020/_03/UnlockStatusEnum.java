
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * This indicates whether the Charging Station has unlocked the connector.
 * 
 * 
 */
@Generated("jsonschema2pojo")
public enum UnlockStatusEnum {

    UNLOCKED("Unlocked"),
    UNLOCK_FAILED("UnlockFailed"),
    ONGOING_AUTHORIZED_TRANSACTION("OngoingAuthorizedTransaction"),
    UNKNOWN_CONNECTOR("UnknownConnector");
    private final String value;
    private final static Map<String, UnlockStatusEnum> CONSTANTS = new HashMap<String, UnlockStatusEnum>();

    static {
        for (UnlockStatusEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    UnlockStatusEnum(String value) {
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
    public static UnlockStatusEnum fromValue(String value) {
        UnlockStatusEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
