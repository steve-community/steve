
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Indicates the certificate type that is sent.
 * 
 * 
 */
public enum InstallCertificateUseEnum {

    V_2_G_ROOT_CERTIFICATE("V2GRootCertificate"),
    MO_ROOT_CERTIFICATE("MORootCertificate"),
    CSMS_ROOT_CERTIFICATE("CSMSRootCertificate"),
    MANUFACTURER_ROOT_CERTIFICATE("ManufacturerRootCertificate");
    private final String value;
    private final static Map<String, InstallCertificateUseEnum> CONSTANTS = new HashMap<String, InstallCertificateUseEnum>();

    static {
        for (InstallCertificateUseEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    InstallCertificateUseEnum(String value) {
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
    public static InstallCertificateUseEnum fromValue(String value) {
        InstallCertificateUseEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
