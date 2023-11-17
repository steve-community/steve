
package ocpp.cp._2015._10;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ChargingRateUnitType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ChargingRateUnitType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="W"/&gt;
 *     &lt;enumeration value="A"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ChargingRateUnitType")
@XmlEnum
public enum ChargingRateUnitType {

    W,
    A;

    public String value() {
        return name();
    }

    public static ChargingRateUnitType fromValue(String v) {
        return valueOf(v);
    }

}
