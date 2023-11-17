
package ocpp.cs._2015._10;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UnitOfMeasure.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="UnitOfMeasure"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Celsius"/&gt;
 *     &lt;enumeration value="Fahrenheit"/&gt;
 *     &lt;enumeration value="Wh"/&gt;
 *     &lt;enumeration value="kWh"/&gt;
 *     &lt;enumeration value="varh"/&gt;
 *     &lt;enumeration value="kvarh"/&gt;
 *     &lt;enumeration value="W"/&gt;
 *     &lt;enumeration value="kW"/&gt;
 *     &lt;enumeration value="VA"/&gt;
 *     &lt;enumeration value="kVA"/&gt;
 *     &lt;enumeration value="var"/&gt;
 *     &lt;enumeration value="kvar"/&gt;
 *     &lt;enumeration value="A"/&gt;
 *     &lt;enumeration value="V"/&gt;
 *     &lt;enumeration value="K"/&gt;
 *     &lt;enumeration value="Percent"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "UnitOfMeasure")
@XmlEnum
public enum UnitOfMeasure {

    @XmlEnumValue("Celsius")
    CELSIUS("Celsius"),
    @XmlEnumValue("Fahrenheit")
    FAHRENHEIT("Fahrenheit"),
    @XmlEnumValue("Wh")
    WH("Wh"),
    @XmlEnumValue("kWh")
    K_WH("kWh"),
    @XmlEnumValue("varh")
    VARH("varh"),
    @XmlEnumValue("kvarh")
    KVARH("kvarh"),
    W("W"),
    @XmlEnumValue("kW")
    K_W("kW"),
    VA("VA"),
    @XmlEnumValue("kVA")
    K_VA("kVA"),
    @XmlEnumValue("var")
    VAR("var"),
    @XmlEnumValue("kvar")
    KVAR("kvar"),
    A("A"),
    V("V"),
    K("K"),
    @XmlEnumValue("Percent")
    PERCENT("Percent");
    private final String value;

    UnitOfMeasure(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static UnitOfMeasure fromValue(String v) {
        for (UnitOfMeasure c: UnitOfMeasure.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
