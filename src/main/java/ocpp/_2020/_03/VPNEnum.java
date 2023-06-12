
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * VPN. Type. VPN_ Code
 * urn:x-oca:ocpp:uid:1:569277
 * Type of VPN
 * 
 * 
 */
@Generated("jsonschema2pojo")
public enum VPNEnum {

    IK_EV_2("IKEv2"),
    IP_SEC("IPSec"),
    L_2_TP("L2TP"),
    PPTP("PPTP");
    private final String value;
    private final static Map<String, VPNEnum> CONSTANTS = new HashMap<String, VPNEnum>();

    static {
        for (VPNEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    VPNEnum(String value) {
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
    public static VPNEnum fromValue(String value) {
        VPNEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
