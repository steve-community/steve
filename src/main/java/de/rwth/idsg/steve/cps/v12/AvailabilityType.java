
package de.rwth.idsg.steve.cps.v12;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AvailabilityType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AvailabilityType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Inoperative"/>
 *     &lt;enumeration value="Operative"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AvailabilityType")
@XmlEnum
public enum AvailabilityType {

    @XmlEnumValue("Inoperative")
    INOPERATIVE("Inoperative"),
    @XmlEnumValue("Operative")
    OPERATIVE("Operative");
    private final String value;

    AvailabilityType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AvailabilityType fromValue(String v) {
        for (AvailabilityType c: AvailabilityType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
