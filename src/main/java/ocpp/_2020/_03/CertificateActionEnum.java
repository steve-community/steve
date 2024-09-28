
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Defines whether certificate needs to be installed or updated.
 * 
 * 
 */
public enum CertificateActionEnum {

    INSTALL("Install"),
    UPDATE("Update");
    private final String value;
    private final static Map<String, CertificateActionEnum> CONSTANTS = new HashMap<String, CertificateActionEnum>();

    static {
        for (CertificateActionEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    CertificateActionEnum(String value) {
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
    public static CertificateActionEnum fromValue(String value) {
        CertificateActionEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
