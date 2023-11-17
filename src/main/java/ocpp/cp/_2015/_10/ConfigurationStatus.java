
package ocpp.cp._2015._10;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ConfigurationStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ConfigurationStatus"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Accepted"/&gt;
 *     &lt;enumeration value="NotSupported"/&gt;
 *     &lt;enumeration value="RebootRequired"/&gt;
 *     &lt;enumeration value="Rejected"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ConfigurationStatus")
@XmlEnum
public enum ConfigurationStatus {

    @XmlEnumValue("Accepted")
    ACCEPTED("Accepted"),
    @XmlEnumValue("NotSupported")
    NOT_SUPPORTED("NotSupported"),
    @XmlEnumValue("RebootRequired")
    REBOOT_REQUIRED("RebootRequired"),
    @XmlEnumValue("Rejected")
    REJECTED("Rejected");
    private final String value;

    ConfigurationStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ConfigurationStatus fromValue(String v) {
        for (ConfigurationStatus c: ConfigurationStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
