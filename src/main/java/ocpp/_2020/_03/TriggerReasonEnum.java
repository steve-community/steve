
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Reason the Charging Station sends this message to the CSMS
 * 
 * 
 */
public enum TriggerReasonEnum {

    AUTHORIZED("Authorized"),
    CABLE_PLUGGED_IN("CablePluggedIn"),
    CHARGING_RATE_CHANGED("ChargingRateChanged"),
    CHARGING_STATE_CHANGED("ChargingStateChanged"),
    DEAUTHORIZED("Deauthorized"),
    ENERGY_LIMIT_REACHED("EnergyLimitReached"),
    EV_COMMUNICATION_LOST("EVCommunicationLost"),
    EV_CONNECT_TIMEOUT("EVConnectTimeout"),
    METER_VALUE_CLOCK("MeterValueClock"),
    METER_VALUE_PERIODIC("MeterValuePeriodic"),
    TIME_LIMIT_REACHED("TimeLimitReached"),
    TRIGGER("Trigger"),
    UNLOCK_COMMAND("UnlockCommand"),
    STOP_AUTHORIZED("StopAuthorized"),
    EV_DEPARTED("EVDeparted"),
    EV_DETECTED("EVDetected"),
    REMOTE_STOP("RemoteStop"),
    REMOTE_START("RemoteStart"),
    ABNORMAL_CONDITION("AbnormalCondition"),
    SIGNED_DATA_RECEIVED("SignedDataReceived"),
    RESET_COMMAND("ResetCommand");
    private final String value;
    private final static Map<String, TriggerReasonEnum> CONSTANTS = new HashMap<String, TriggerReasonEnum>();

    static {
        for (TriggerReasonEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    TriggerReasonEnum(String value) {
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
    public static TriggerReasonEnum fromValue(String value) {
        TriggerReasonEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
