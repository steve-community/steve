
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Charging Station indicates if it can process the request.
 * 
 * 
 */
public enum GetInstalledCertificateStatusEnum {

    ACCEPTED("Accepted"),
    NOT_FOUND("NotFound");
    private final String value;
    private final static Map<String, GetInstalledCertificateStatusEnum> CONSTANTS = new HashMap<String, GetInstalledCertificateStatusEnum>();

    static {
        for (GetInstalledCertificateStatusEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    GetInstalledCertificateStatusEnum(String value) {
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
    public static GetInstalledCertificateStatusEnum fromValue(String value) {
        GetInstalledCertificateStatusEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
