
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Status indicating whether the Charging Station accepts the request to start a transaction.
 * 
 * 
 */
public enum RequestStartStopStatusEnum {

    ACCEPTED("Accepted"),
    REJECTED("Rejected");
    private final String value;
    private final static Map<String, RequestStartStopStatusEnum> CONSTANTS = new HashMap<String, RequestStartStopStatusEnum>();

    static {
        for (RequestStartStopStatusEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    RequestStartStopStatusEnum(String value) {
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
    public static RequestStartStopStatusEnum fromValue(String value) {
        RequestStartStopStatusEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
