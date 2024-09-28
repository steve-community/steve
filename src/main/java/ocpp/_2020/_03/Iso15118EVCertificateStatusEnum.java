
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Indicates whether the message was processed properly.
 * 
 * 
 */
public enum Iso15118EVCertificateStatusEnum {

    ACCEPTED("Accepted"),
    FAILED("Failed");
    private final String value;
    private final static Map<String, Iso15118EVCertificateStatusEnum> CONSTANTS = new HashMap<String, Iso15118EVCertificateStatusEnum>();

    static {
        for (Iso15118EVCertificateStatusEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    Iso15118EVCertificateStatusEnum(String value) {
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
    public static Iso15118EVCertificateStatusEnum fromValue(String value) {
        Iso15118EVCertificateStatusEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
