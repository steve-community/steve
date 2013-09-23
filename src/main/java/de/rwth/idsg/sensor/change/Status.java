
package de.rwth.idsg.sensor.change;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for status.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="status">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Free"/>
 *     &lt;enumeration value="Reserved"/>
 *     &lt;enumeration value="Inoperative"/>
 *     &lt;enumeration value="Trans_Started"/>
 *     &lt;enumeration value="Trans_Stopped"/>
 *     &lt;enumeration value="Trans_Rejected"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "status")
@XmlEnum
public enum Status {

    @XmlEnumValue("Free")
    FREE("Free"),
    @XmlEnumValue("Reserved")
    RESERVED("Reserved"),
    @XmlEnumValue("Inoperative")
    INOPERATIVE("Inoperative"),
    @XmlEnumValue("Trans_Started")
    TRANS_STARTED("Trans_Started"),
    @XmlEnumValue("Trans_Stopped")
    TRANS_STOPPED("Trans_Stopped"),
    @XmlEnumValue("Trans_Rejected")
    TRANS_REJECTED("Trans_Rejected");
    private final String value;

    Status(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Status fromValue(String v) {
        for (Status c: Status.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
