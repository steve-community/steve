
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * This field specifies the connector type.
 * 
 * 
 */
@Generated("jsonschema2pojo")
public enum ConnectorEnum {

    C_CCS_1("cCCS1"),
    C_CCS_2("cCCS2"),
    C_G_105("cG105"),
    C_TESLA("cTesla"),
    C_TYPE_1("cType1"),
    C_TYPE_2("cType2"),
    S_309_1_P_16_A("s309-1P-16A"),
    S_309_1_P_32_A("s309-1P-32A"),
    S_309_3_P_16_A("s309-3P-16A"),
    S_309_3_P_32_A("s309-3P-32A"),
    S_BS_1361("sBS1361"),
    S_CEE_7_7("sCEE-7-7"),
    S_TYPE_2("sType2"),
    S_TYPE_3("sType3"),
    OTHER_1_PH_MAX_16_A("Other1PhMax16A"),
    OTHER_1_PH_OVER_16_A("Other1PhOver16A"),
    OTHER_3_PH("Other3Ph"),
    PAN("Pan"),
    W_INDUCTIVE("wInductive"),
    W_RESONANT("wResonant"),
    UNDETERMINED("Undetermined"),
    UNKNOWN("Unknown");
    private final String value;
    private final static Map<String, ConnectorEnum> CONSTANTS = new HashMap<String, ConnectorEnum>();

    static {
        for (ConnectorEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    ConnectorEnum(String value) {
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
    public static ConnectorEnum fromValue(String value) {
        ConnectorEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
