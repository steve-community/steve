
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Sampled_ Value. Measurand. Measurand_ Code
 * urn:x-oca:ocpp:uid:1:569263
 * Type of measurement. Default = "Energy.Active.Import.Register"
 * 
 * 
 */
public enum MeasurandEnum {

    CURRENT_EXPORT("Current.Export"),
    CURRENT_IMPORT("Current.Import"),
    CURRENT_OFFERED("Current.Offered"),
    ENERGY_ACTIVE_EXPORT_REGISTER("Energy.Active.Export.Register"),
    ENERGY_ACTIVE_IMPORT_REGISTER("Energy.Active.Import.Register"),
    ENERGY_REACTIVE_EXPORT_REGISTER("Energy.Reactive.Export.Register"),
    ENERGY_REACTIVE_IMPORT_REGISTER("Energy.Reactive.Import.Register"),
    ENERGY_ACTIVE_EXPORT_INTERVAL("Energy.Active.Export.Interval"),
    ENERGY_ACTIVE_IMPORT_INTERVAL("Energy.Active.Import.Interval"),
    ENERGY_ACTIVE_NET("Energy.Active.Net"),
    ENERGY_REACTIVE_EXPORT_INTERVAL("Energy.Reactive.Export.Interval"),
    ENERGY_REACTIVE_IMPORT_INTERVAL("Energy.Reactive.Import.Interval"),
    ENERGY_REACTIVE_NET("Energy.Reactive.Net"),
    ENERGY_APPARENT_NET("Energy.Apparent.Net"),
    ENERGY_APPARENT_IMPORT("Energy.Apparent.Import"),
    ENERGY_APPARENT_EXPORT("Energy.Apparent.Export"),
    FREQUENCY("Frequency"),
    POWER_ACTIVE_EXPORT("Power.Active.Export"),
    POWER_ACTIVE_IMPORT("Power.Active.Import"),
    POWER_FACTOR("Power.Factor"),
    POWER_OFFERED("Power.Offered"),
    POWER_REACTIVE_EXPORT("Power.Reactive.Export"),
    POWER_REACTIVE_IMPORT("Power.Reactive.Import"),
    SO_C("SoC"),
    VOLTAGE("Voltage");
    private final String value;
    private final static Map<String, MeasurandEnum> CONSTANTS = new HashMap<String, MeasurandEnum>();

    static {
        for (MeasurandEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    MeasurandEnum(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static MeasurandEnum fromValue(String value) {
        MeasurandEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
