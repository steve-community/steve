
package ocpp.cs._2012._06;

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
 *     &lt;enumeration value="HighTemperature"/&gt;
 *     &lt;enumeration value="Mode3Error"/&gt;
 *     &lt;enumeration value="NoError"/&gt;
 *     &lt;enumeration value="PowerMeterFailure"/&gt;
 *     &lt;enumeration value="PowerSwitchFailure"/&gt;
 *     &lt;enumeration value="ReaderFailure"/&gt;
 *     &lt;enumeration value="ResetFailure"/&gt;
 *     &lt;enumeration value="GroundFailure"/&gt;
 *     &lt;enumeration value="OverCurrentFailure"/&gt;
 *     &lt;enumeration value="UnderVoltage"/&gt;
 *     &lt;enumeration value="WeakSignal"/&gt;
 *     &lt;enumeration value="OtherError"/&gt;
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
    @XmlEnumValue("HighTemperature")
    HIGH_TEMPERATURE("HighTemperature"),
    @XmlEnumValue("Mode3Error")
    MODE_3_ERROR("Mode3Error"),
    @XmlEnumValue("NoError")
    NO_ERROR("NoError"),
    @XmlEnumValue("PowerMeterFailure")
    POWER_METER_FAILURE("PowerMeterFailure"),
    @XmlEnumValue("PowerSwitchFailure")
    POWER_SWITCH_FAILURE("PowerSwitchFailure"),
    @XmlEnumValue("ReaderFailure")
    READER_FAILURE("ReaderFailure"),
    @XmlEnumValue("ResetFailure")
    RESET_FAILURE("ResetFailure"),
    @XmlEnumValue("GroundFailure")
    GROUND_FAILURE("GroundFailure"),
    @XmlEnumValue("OverCurrentFailure")
    OVER_CURRENT_FAILURE("OverCurrentFailure"),
    @XmlEnumValue("UnderVoltage")
    UNDER_VOLTAGE("UnderVoltage"),
    @XmlEnumValue("WeakSignal")
    WEAK_SIGNAL("WeakSignal"),
    @XmlEnumValue("OtherError")
    OTHER_ERROR("OtherError");
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
