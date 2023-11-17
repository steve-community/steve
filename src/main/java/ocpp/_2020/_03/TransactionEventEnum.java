
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * This contains the type of this event.
 * The first TransactionEvent of a transaction SHALL contain: "Started" The last TransactionEvent of a transaction SHALL contain: "Ended" All others SHALL contain: "Updated"
 * 
 * 
 */
@Generated("jsonschema2pojo")
public enum TransactionEventEnum {

    ENDED("Ended"),
    STARTED("Started"),
    UPDATED("Updated");
    private final String value;
    private final static Map<String, TransactionEventEnum> CONSTANTS = new HashMap<String, TransactionEventEnum>();

    static {
        for (TransactionEventEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    TransactionEventEnum(String value) {
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
    public static TransactionEventEnum fromValue(String value) {
        TransactionEventEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
