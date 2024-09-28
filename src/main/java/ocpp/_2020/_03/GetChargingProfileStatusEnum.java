
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * This indicates whether the Charging Station is able to process this request and will send &lt;&lt;reportchargingprofilesrequest, ReportChargingProfilesRequest&gt;&gt; messages.
 * 
 * 
 */
public enum GetChargingProfileStatusEnum {

    ACCEPTED("Accepted"),
    NO_PROFILES("NoProfiles");
    private final String value;
    private final static Map<String, GetChargingProfileStatusEnum> CONSTANTS = new HashMap<String, GetChargingProfileStatusEnum>();

    static {
        for (GetChargingProfileStatusEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    GetChargingProfileStatusEnum(String value) {
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
    public static GetChargingProfileStatusEnum fromValue(String value) {
        GetChargingProfileStatusEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
