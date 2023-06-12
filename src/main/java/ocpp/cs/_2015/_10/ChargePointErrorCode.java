
package ocpp.cs._2015._10;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ChargePointErrorCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ChargePointErrorCode"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="ConnectorLockFailure"/&gt;
 *     &lt;enumeration value="EVCommunicationError"/&gt;
 *     &lt;enumeration value="GroundFailure"/&gt;
 *     &lt;enumeration value="HighTemperature"/&gt;
 *     &lt;enumeration value="InternalError"/&gt;
 *     &lt;enumeration value="LocalListConflict"/&gt;
 *     &lt;enumeration value="NoError"/&gt;
 *     &lt;enumeration value="OtherError"/&gt;
 *     &lt;enumeration value="OverCurrentFailure"/&gt;
 *     &lt;enumeration value="OverVoltage"/&gt;
 *     &lt;enumeration value="PowerMeterFailure"/&gt;
 *     &lt;enumeration value="PowerSwitchFailure"/&gt;
 *     &lt;enumeration value="ReaderFailure"/&gt;
 *     &lt;enumeration value="ResetFailure"/&gt;
 *     &lt;enumeration value="UnderVoltage"/&gt;
 *     &lt;enumeration value="WeakSignal"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ChargePointErrorCode")
@XmlEnum
public enum ChargePointErrorCode {

    @XmlEnumValue("ConnectorLockFailure")
    CONNECTOR_LOCK_FAILURE("ConnectorLockFailure"),
    @XmlEnumValue("EVCommunicationError")
    EV_COMMUNICATION_ERROR("EVCommunicationError"),
    @XmlEnumValue("GroundFailure")
    GROUND_FAILURE("GroundFailure"),
    @XmlEnumValue("HighTemperature")
    HIGH_TEMPERATURE("HighTemperature"),
    @XmlEnumValue("InternalError")
    INTERNAL_ERROR("InternalError"),
    @XmlEnumValue("LocalListConflict")
    LOCAL_LIST_CONFLICT("LocalListConflict"),
    @XmlEnumValue("NoError")
    NO_ERROR("NoError"),
    @XmlEnumValue("OtherError")
    OTHER_ERROR("OtherError"),
    @XmlEnumValue("OverCurrentFailure")
    OVER_CURRENT_FAILURE("OverCurrentFailure"),
    @XmlEnumValue("OverVoltage")
    OVER_VOLTAGE("OverVoltage"),
    @XmlEnumValue("PowerMeterFailure")
    POWER_METER_FAILURE("PowerMeterFailure"),
    @XmlEnumValue("PowerSwitchFailure")
    POWER_SWITCH_FAILURE("PowerSwitchFailure"),
    @XmlEnumValue("ReaderFailure")
    READER_FAILURE("ReaderFailure"),
    @XmlEnumValue("ResetFailure")
    RESET_FAILURE("ResetFailure"),
    @XmlEnumValue("UnderVoltage")
    UNDER_VOLTAGE("UnderVoltage"),
    @XmlEnumValue("WeakSignal")
    WEAK_SIGNAL("WeakSignal");
    private final String value;

    ChargePointErrorCode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ChargePointErrorCode fromValue(String v) {
        for (ChargePointErrorCode c: ChargePointErrorCode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
