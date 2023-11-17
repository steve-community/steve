
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * APN. APN_ Authentication. APN_ Authentication_ Code
 * urn:x-oca:ocpp:uid:1:568828
 * Authentication method.
 * 
 * 
 */
@Generated("jsonschema2pojo")
public enum APNAuthenticationEnum {

    CHAP("CHAP"),
    NONE("NONE"),
    PAP("PAP"),
    AUTO("AUTO");
    private final String value;
    private final static Map<String, APNAuthenticationEnum> CONSTANTS = new HashMap<String, APNAuthenticationEnum>();

    static {
        for (APNAuthenticationEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    APNAuthenticationEnum(String value) {
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
    public static APNAuthenticationEnum fromValue(String value) {
        APNAuthenticationEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
