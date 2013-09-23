
package ocpp.cs._2012._06;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Measurand.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Measurand">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Energy.Active.Export.Register"/>
 *     &lt;enumeration value="Energy.Active.Import.Register"/>
 *     &lt;enumeration value="Energy.Reactive.Export.Register"/>
 *     &lt;enumeration value="Energy.Reactive.Import.Register"/>
 *     &lt;enumeration value="Energy.Active.Export.Interval"/>
 *     &lt;enumeration value="Energy.Active.Import.Interval"/>
 *     &lt;enumeration value="Energy.Reactive.Export.Interval"/>
 *     &lt;enumeration value="Energy.Reactive.Import.Interval"/>
 *     &lt;enumeration value="Power.Active.Export"/>
 *     &lt;enumeration value="Power.Active.Import"/>
 *     &lt;enumeration value="Power.Reactive.Export"/>
 *     &lt;enumeration value="Power.Reactive.Import"/>
 *     &lt;enumeration value="Current.Export"/>
 *     &lt;enumeration value="Current.Import"/>
 *     &lt;enumeration value="Voltage"/>
 *     &lt;enumeration value="Temperature"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "Measurand")
@XmlEnum
public enum Measurand {

    @XmlEnumValue("Energy.Active.Export.Register")
    ENERGY_ACTIVE_EXPORT_REGISTER("Energy.Active.Export.Register"),
    @XmlEnumValue("Energy.Active.Import.Register")
    ENERGY_ACTIVE_IMPORT_REGISTER("Energy.Active.Import.Register"),
    @XmlEnumValue("Energy.Reactive.Export.Register")
    ENERGY_REACTIVE_EXPORT_REGISTER("Energy.Reactive.Export.Register"),
    @XmlEnumValue("Energy.Reactive.Import.Register")
    ENERGY_REACTIVE_IMPORT_REGISTER("Energy.Reactive.Import.Register"),
    @XmlEnumValue("Energy.Active.Export.Interval")
    ENERGY_ACTIVE_EXPORT_INTERVAL("Energy.Active.Export.Interval"),
    @XmlEnumValue("Energy.Active.Import.Interval")
    ENERGY_ACTIVE_IMPORT_INTERVAL("Energy.Active.Import.Interval"),
    @XmlEnumValue("Energy.Reactive.Export.Interval")
    ENERGY_REACTIVE_EXPORT_INTERVAL("Energy.Reactive.Export.Interval"),
    @XmlEnumValue("Energy.Reactive.Import.Interval")
    ENERGY_REACTIVE_IMPORT_INTERVAL("Energy.Reactive.Import.Interval"),
    @XmlEnumValue("Power.Active.Export")
    POWER_ACTIVE_EXPORT("Power.Active.Export"),
    @XmlEnumValue("Power.Active.Import")
    POWER_ACTIVE_IMPORT("Power.Active.Import"),
    @XmlEnumValue("Power.Reactive.Export")
    POWER_REACTIVE_EXPORT("Power.Reactive.Export"),
    @XmlEnumValue("Power.Reactive.Import")
    POWER_REACTIVE_IMPORT("Power.Reactive.Import"),
    @XmlEnumValue("Current.Export")
    CURRENT_EXPORT("Current.Export"),
    @XmlEnumValue("Current.Import")
    CURRENT_IMPORT("Current.Import"),
    @XmlEnumValue("Voltage")
    VOLTAGE("Voltage"),
    @XmlEnumValue("Temperature")
    TEMPERATURE("Temperature");
    private final String value;

    Measurand(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Measurand fromValue(String v) {
        for (Measurand c: Measurand.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
