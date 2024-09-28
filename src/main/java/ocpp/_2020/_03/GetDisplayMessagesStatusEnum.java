
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Indicates if the Charging Station has Display Messages that match the request criteria in the &lt;&lt;getdisplaymessagesrequest,GetDisplayMessagesRequest&gt;&gt;
 * 
 * 
 */
public enum GetDisplayMessagesStatusEnum {

    ACCEPTED("Accepted"),
    UNKNOWN("Unknown");
    private final String value;
    private final static Map<String, GetDisplayMessagesStatusEnum> CONSTANTS = new HashMap<String, GetDisplayMessagesStatusEnum>();

    static {
        for (GetDisplayMessagesStatusEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    GetDisplayMessagesStatusEnum(String value) {
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
    public static GetDisplayMessagesStatusEnum fromValue(String value) {
        GetDisplayMessagesStatusEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
