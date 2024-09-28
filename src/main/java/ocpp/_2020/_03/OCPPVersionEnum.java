
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Communication_ Function. OCPP_ Version. OCPP_ Version_ Code
 * urn:x-oca:ocpp:uid:1:569355
 * Defines the OCPP version used for this communication function.
 * 
 * 
 */
public enum OCPPVersionEnum {

    OCPP_12("OCPP12"),
    OCPP_15("OCPP15"),
    OCPP_16("OCPP16"),
    OCPP_20("OCPP20");
    private final String value;
    private final static Map<String, OCPPVersionEnum> CONSTANTS = new HashMap<String, OCPPVersionEnum>();

    static {
        for (OCPPVersionEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    OCPPVersionEnum(String value) {
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
    public static OCPPVersionEnum fromValue(String value) {
        OCPPVersionEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
