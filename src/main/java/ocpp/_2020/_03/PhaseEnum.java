
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Sampled_ Value. Phase. Phase_ Code
 * urn:x-oca:ocpp:uid:1:569264
 * Indicates how the measured value is to be interpreted. For instance between L1 and neutral (L1-N) Please note that not all values of phase are applicable to all Measurands. When phase is absent, the measured value is interpreted as an overall value.
 * 
 * 
 */
public enum PhaseEnum {

    L_1("L1"),
    L_2("L2"),
    L_3("L3"),
    N("N"),
    L_1_N("L1-N"),
    L_2_N("L2-N"),
    L_3_N("L3-N"),
    L_1_L_2("L1-L2"),
    L_2_L_3("L2-L3"),
    L_3_L_1("L3-L1");
    private final String value;
    private final static Map<String, PhaseEnum> CONSTANTS = new HashMap<String, PhaseEnum>();

    static {
        for (PhaseEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    PhaseEnum(String value) {
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
    public static PhaseEnum fromValue(String value) {
        PhaseEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
