
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Returns whether certificate signing has been accepted, otherwise rejected.
 * 
 * 
 */
public enum CertificateSignedStatusEnum {

    ACCEPTED("Accepted"),
    REJECTED("Rejected");
    private final String value;
    private final static Map<String, CertificateSignedStatusEnum> CONSTANTS = new HashMap<String, CertificateSignedStatusEnum>();

    static {
        for (CertificateSignedStatusEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    CertificateSignedStatusEnum(String value) {
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
    public static CertificateSignedStatusEnum fromValue(String value) {
        CertificateSignedStatusEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
