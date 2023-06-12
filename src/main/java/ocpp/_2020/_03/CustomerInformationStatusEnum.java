
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Indicates whether the request was accepted.
 * 
 * 
 */
@Generated("jsonschema2pojo")
public enum CustomerInformationStatusEnum {

    ACCEPTED("Accepted"),
    REJECTED("Rejected"),
    INVALID("Invalid");
    private final String value;
    private final static Map<String, CustomerInformationStatusEnum> CONSTANTS = new HashMap<String, CustomerInformationStatusEnum>();

    static {
        for (CustomerInformationStatusEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    CustomerInformationStatusEnum(String value) {
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
    public static CustomerInformationStatusEnum fromValue(String value) {
        CustomerInformationStatusEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
