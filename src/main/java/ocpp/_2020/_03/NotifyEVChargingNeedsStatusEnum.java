
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Returns whether the CSMS has been able to process the message successfully. It does not imply that the evChargingNeeds can be met with the current charging profile.
 * 
 * 
 */
public enum NotifyEVChargingNeedsStatusEnum {

    ACCEPTED("Accepted"),
    REJECTED("Rejected"),
    PROCESSING("Processing");
    private final String value;
    private final static Map<String, NotifyEVChargingNeedsStatusEnum> CONSTANTS = new HashMap<String, NotifyEVChargingNeedsStatusEnum>();

    static {
        for (NotifyEVChargingNeedsStatusEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    NotifyEVChargingNeedsStatusEnum(String value) {
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
    public static NotifyEVChargingNeedsStatusEnum fromValue(String value) {
        NotifyEVChargingNeedsStatusEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
