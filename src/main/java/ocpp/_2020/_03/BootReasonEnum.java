
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * This contains the reason for sending this message to the CSMS.
 * 
 * 
 */
@Generated("jsonschema2pojo")
public enum BootReasonEnum {

    APPLICATION_RESET("ApplicationReset"),
    FIRMWARE_UPDATE("FirmwareUpdate"),
    LOCAL_RESET("LocalReset"),
    POWER_UP("PowerUp"),
    REMOTE_RESET("RemoteReset"),
    SCHEDULED_RESET("ScheduledReset"),
    TRIGGERED("Triggered"),
    UNKNOWN("Unknown"),
    WATCHDOG("Watchdog");
    private final String value;
    private final static Map<String, BootReasonEnum> CONSTANTS = new HashMap<String, BootReasonEnum>();

    static {
        for (BootReasonEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    BootReasonEnum(String value) {
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
    public static BootReasonEnum fromValue(String value) {
        BootReasonEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
