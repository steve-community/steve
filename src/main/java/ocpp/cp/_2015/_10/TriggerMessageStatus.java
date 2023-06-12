
package ocpp.cp._2015._10;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TriggerMessageStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TriggerMessageStatus"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Accepted"/&gt;
 *     &lt;enumeration value="Rejected"/&gt;
 *     &lt;enumeration value="NotImplemented"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "TriggerMessageStatus")
@XmlEnum
public enum TriggerMessageStatus {

    @XmlEnumValue("Accepted")
    ACCEPTED("Accepted"),
    @XmlEnumValue("Rejected")
    REJECTED("Rejected"),
    @XmlEnumValue("NotImplemented")
    NOT_IMPLEMENTED("NotImplemented");
    private final String value;

    TriggerMessageStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TriggerMessageStatus fromValue(String v) {
        for (TriggerMessageStatus c: TriggerMessageStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
