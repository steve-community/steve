
package ocpp.cp._2015._10;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ResetType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="ResetType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Hard"/&gt;
 *     &lt;enumeration value="Soft"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ResetType")
@XmlEnum
public enum ResetType {

    @XmlEnumValue("Hard")
    HARD("Hard"),
    @XmlEnumValue("Soft")
    SOFT("Soft");
    private final String value;

    ResetType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ResetType fromValue(String v) {
        for (ResetType c: ResetType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
