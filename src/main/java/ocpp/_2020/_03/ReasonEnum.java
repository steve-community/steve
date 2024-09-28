
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Transaction. Stopped_ Reason. EOT_ Reason_ Code
 * urn:x-oca:ocpp:uid:1:569413
 * This contains the reason why the transaction was stopped. MAY only be omitted when Reason is "Local".
 * 
 * 
 */
public enum ReasonEnum {

    DE_AUTHORIZED("DeAuthorized"),
    EMERGENCY_STOP("EmergencyStop"),
    ENERGY_LIMIT_REACHED("EnergyLimitReached"),
    EV_DISCONNECTED("EVDisconnected"),
    GROUND_FAULT("GroundFault"),
    IMMEDIATE_RESET("ImmediateReset"),
    LOCAL("Local"),
    LOCAL_OUT_OF_CREDIT("LocalOutOfCredit"),
    MASTER_PASS("MasterPass"),
    OTHER("Other"),
    OVERCURRENT_FAULT("OvercurrentFault"),
    POWER_LOSS("PowerLoss"),
    POWER_QUALITY("PowerQuality"),
    REBOOT("Reboot"),
    REMOTE("Remote"),
    SOC_LIMIT_REACHED("SOCLimitReached"),
    STOPPED_BY_EV("StoppedByEV"),
    TIME_LIMIT_REACHED("TimeLimitReached"),
    TIMEOUT("Timeout");
    private final String value;
    private final static Map<String, ReasonEnum> CONSTANTS = new HashMap<String, ReasonEnum>();

    static {
        for (ReasonEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    ReasonEnum(String value) {
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
    public static ReasonEnum fromValue(String value) {
        ReasonEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
