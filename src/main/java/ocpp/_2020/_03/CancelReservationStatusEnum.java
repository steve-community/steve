
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * This indicates the success or failure of the canceling of a reservation by CSMS.
 * 
 * 
 */
public enum CancelReservationStatusEnum {

    ACCEPTED("Accepted"),
    REJECTED("Rejected");
    private final String value;
    private final static Map<String, CancelReservationStatusEnum> CONSTANTS = new HashMap<String, CancelReservationStatusEnum>();

    static {
        for (CancelReservationStatusEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    CancelReservationStatusEnum(String value) {
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
    public static CancelReservationStatusEnum fromValue(String value) {
        CancelReservationStatusEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
