
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * This field specifies the report base.
 * 
 * 
 */
public enum ReportBaseEnum {

    CONFIGURATION_INVENTORY("ConfigurationInventory"),
    FULL_INVENTORY("FullInventory"),
    SUMMARY_INVENTORY("SummaryInventory");
    private final String value;
    private final static Map<String, ReportBaseEnum> CONSTANTS = new HashMap<String, ReportBaseEnum>();

    static {
        for (ReportBaseEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    ReportBaseEnum(String value) {
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
    public static ReportBaseEnum fromValue(String value) {
        ReportBaseEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
