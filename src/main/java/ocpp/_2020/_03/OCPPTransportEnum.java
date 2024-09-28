
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Communication_ Function. OCPP_ Transport. OCPP_ Transport_ Code
 * urn:x-oca:ocpp:uid:1:569356
 * Defines the transport protocol (e.g. SOAP or JSON). Note: SOAP is not supported in OCPP 2.0, but is supported by other versions of OCPP.
 * 
 * 
 */
public enum OCPPTransportEnum {

    JSON("JSON"),
    SOAP("SOAP");
    private final String value;
    private final static Map<String, OCPPTransportEnum> CONSTANTS = new HashMap<String, OCPPTransportEnum>();

    static {
        for (OCPPTransportEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    OCPPTransportEnum(String value) {
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
    public static OCPPTransportEnum fromValue(String value) {
        OCPPTransportEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
