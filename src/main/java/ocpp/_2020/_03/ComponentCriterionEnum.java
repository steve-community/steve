
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

@Generated("jsonschema2pojo")
public enum ComponentCriterionEnum {

    ACTIVE("Active"),
    AVAILABLE("Available"),
    ENABLED("Enabled"),
    PROBLEM("Problem");
    private final String value;
    private final static Map<String, ComponentCriterionEnum> CONSTANTS = new HashMap<String, ComponentCriterionEnum>();

    static {
        for (ComponentCriterionEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    ComponentCriterionEnum(String value) {
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
    public static ComponentCriterionEnum fromValue(String value) {
        ComponentCriterionEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
