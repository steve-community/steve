
package de.rwth.idsg.steve.cps.v12;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ResetType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ResetType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Hard"/>
 *     &lt;enumeration value="Soft"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
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
