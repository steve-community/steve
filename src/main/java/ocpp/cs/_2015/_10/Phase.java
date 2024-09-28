
package ocpp.cs._2015._10;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Phase.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="Phase"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="L1"/&gt;
 *     &lt;enumeration value="L2"/&gt;
 *     &lt;enumeration value="L3"/&gt;
 *     &lt;enumeration value="N"/&gt;
 *     &lt;enumeration value="L1-N"/&gt;
 *     &lt;enumeration value="L2-N"/&gt;
 *     &lt;enumeration value="L3-N"/&gt;
 *     &lt;enumeration value="L1-L2"/&gt;
 *     &lt;enumeration value="L2-L3"/&gt;
 *     &lt;enumeration value="L3-L1"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "Phase")
@XmlEnum
public enum Phase {

    @XmlEnumValue("L1")
    L_1("L1"),
    @XmlEnumValue("L2")
    L_2("L2"),
    @XmlEnumValue("L3")
    L_3("L3"),
    N("N"),
    @XmlEnumValue("L1-N")
    L_1_N("L1-N"),
    @XmlEnumValue("L2-N")
    L_2_N("L2-N"),
    @XmlEnumValue("L3-N")
    L_3_N("L3-N"),
    @XmlEnumValue("L1-L2")
    L_1_L_2("L1-L2"),
    @XmlEnumValue("L2-L3")
    L_2_L_3("L2-L3"),
    @XmlEnumValue("L3-L1")
    L_3_L_1("L3-L1");
    private final String value;

    Phase(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Phase fromValue(String v) {
        for (Phase c: Phase.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
