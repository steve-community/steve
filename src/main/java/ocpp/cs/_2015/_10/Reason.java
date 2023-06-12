
package ocpp.cs._2015._10;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Reason.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Reason"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="EmergencyStop"/&gt;
 *     &lt;enumeration value="EVDisconnected"/&gt;
 *     &lt;enumeration value="HardReset"/&gt;
 *     &lt;enumeration value="Local"/&gt;
 *     &lt;enumeration value="Other"/&gt;
 *     &lt;enumeration value="PowerLoss"/&gt;
 *     &lt;enumeration value="Reboot"/&gt;
 *     &lt;enumeration value="Remote"/&gt;
 *     &lt;enumeration value="SoftReset"/&gt;
 *     &lt;enumeration value="UnlockCommand"/&gt;
 *     &lt;enumeration value="DeAuthorized"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "Reason")
@XmlEnum
public enum Reason {

    @XmlEnumValue("EmergencyStop")
    EMERGENCY_STOP("EmergencyStop"),
    @XmlEnumValue("EVDisconnected")
    EV_DISCONNECTED("EVDisconnected"),
    @XmlEnumValue("HardReset")
    HARD_RESET("HardReset"),
    @XmlEnumValue("Local")
    LOCAL("Local"),
    @XmlEnumValue("Other")
    OTHER("Other"),
    @XmlEnumValue("PowerLoss")
    POWER_LOSS("PowerLoss"),
    @XmlEnumValue("Reboot")
    REBOOT("Reboot"),
    @XmlEnumValue("Remote")
    REMOTE("Remote"),
    @XmlEnumValue("SoftReset")
    SOFT_RESET("SoftReset"),
    @XmlEnumValue("UnlockCommand")
    UNLOCK_COMMAND("UnlockCommand"),
    @XmlEnumValue("DeAuthorized")
    DE_AUTHORIZED("DeAuthorized");
    private final String value;

    Reason(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Reason fromValue(String v) {
        for (Reason c: Reason.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
