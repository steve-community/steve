
package ocpp.cs._2012._06;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ValueFormat.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ValueFormat">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Raw"/>
 *     &lt;enumeration value="SignedData"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ValueFormat")
@XmlEnum
public enum ValueFormat {

    @XmlEnumValue("Raw")
    RAW("Raw"),
    @XmlEnumValue("SignedData")
    SIGNED_DATA("SignedData");
    private final String value;

    ValueFormat(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ValueFormat fromValue(String v) {
        for (ValueFormat c: ValueFormat.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
