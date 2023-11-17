
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Charging_ Needs. Requested. Energy_ Transfer_ Mode_ Code
 * urn:x-oca:ocpp:uid:1:569209
 * Mode of energy transfer requested by the EV.
 * 
 * 
 */
@Generated("jsonschema2pojo")
public enum EnergyTransferModeEnum {

    DC("DC"),
    AC_SINGLE_PHASE("AC_single_phase"),
    AC_TWO_PHASE("AC_two_phase"),
    AC_THREE_PHASE("AC_three_phase");
    private final String value;
    private final static Map<String, EnergyTransferModeEnum> CONSTANTS = new HashMap<String, EnergyTransferModeEnum>();

    static {
        for (EnergyTransferModeEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    EnergyTransferModeEnum(String value) {
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
    public static EnergyTransferModeEnum fromValue(String value) {
        EnergyTransferModeEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
