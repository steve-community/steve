
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum GetCertificateIdUseEnum {

    V_2_G_ROOT_CERTIFICATE("V2GRootCertificate"),
    MO_ROOT_CERTIFICATE("MORootCertificate"),
    CSMS_ROOT_CERTIFICATE("CSMSRootCertificate"),
    V_2_G_CERTIFICATE_CHAIN("V2GCertificateChain"),
    MANUFACTURER_ROOT_CERTIFICATE("ManufacturerRootCertificate");
    private final String value;
    private final static Map<String, GetCertificateIdUseEnum> CONSTANTS = new HashMap<String, GetCertificateIdUseEnum>();

    static {
        for (GetCertificateIdUseEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    GetCertificateIdUseEnum(String value) {
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
    public static GetCertificateIdUseEnum fromValue(String value) {
        GetCertificateIdUseEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
