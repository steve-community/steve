
package ocpp.cp._2015._10;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MessageTrigger.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="MessageTrigger"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="BootNotification"/&gt;
 *     &lt;enumeration value="DiagnosticsStatusNotification"/&gt;
 *     &lt;enumeration value="FirmwareStatusNotification"/&gt;
 *     &lt;enumeration value="Heartbeat"/&gt;
 *     &lt;enumeration value="MeterValues"/&gt;
 *     &lt;enumeration value="StatusNotification"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "MessageTrigger")
@XmlEnum
public enum MessageTrigger {

    @XmlEnumValue("BootNotification")
    BOOT_NOTIFICATION("BootNotification"),
    @XmlEnumValue("DiagnosticsStatusNotification")
    DIAGNOSTICS_STATUS_NOTIFICATION("DiagnosticsStatusNotification"),
    @XmlEnumValue("FirmwareStatusNotification")
    FIRMWARE_STATUS_NOTIFICATION("FirmwareStatusNotification"),
    @XmlEnumValue("Heartbeat")
    HEARTBEAT("Heartbeat"),
    @XmlEnumValue("MeterValues")
    METER_VALUES("MeterValues"),
    @XmlEnumValue("StatusNotification")
    STATUS_NOTIFICATION("StatusNotification");
    private final String value;

    MessageTrigger(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MessageTrigger fromValue(String v) {
        for (MessageTrigger c: MessageTrigger.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
