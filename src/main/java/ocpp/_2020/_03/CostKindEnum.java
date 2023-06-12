
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Cost. Cost_ Kind. Cost_ Kind_ Code
 * urn:x-oca:ocpp:uid:1:569243
 * The kind of cost referred to in the message element amount
 * 
 * 
 */
@Generated("jsonschema2pojo")
public enum CostKindEnum {

    CARBON_DIOXIDE_EMISSION("CarbonDioxideEmission"),
    RELATIVE_PRICE_PERCENTAGE("RelativePricePercentage"),
    RENEWABLE_GENERATION_PERCENTAGE("RenewableGenerationPercentage");
    private final String value;
    private final static Map<String, CostKindEnum> CONSTANTS = new HashMap<String, CostKindEnum>();

    static {
        for (CostKindEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    CostKindEnum(String value) {
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
    public static CostKindEnum fromValue(String value) {
        CostKindEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
