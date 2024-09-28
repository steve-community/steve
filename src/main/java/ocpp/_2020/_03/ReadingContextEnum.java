
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Sampled_ Value. Context. Reading_ Context_ Code
 * urn:x-oca:ocpp:uid:1:569261
 * Type of detail value: start, end or sample. Default = "Sample.Periodic"
 * 
 * 
 */
public enum ReadingContextEnum {

    INTERRUPTION_BEGIN("Interruption.Begin"),
    INTERRUPTION_END("Interruption.End"),
    OTHER("Other"),
    SAMPLE_CLOCK("Sample.Clock"),
    SAMPLE_PERIODIC("Sample.Periodic"),
    TRANSACTION_BEGIN("Transaction.Begin"),
    TRANSACTION_END("Transaction.End"),
    TRIGGER("Trigger");
    private final String value;
    private final static Map<String, ReadingContextEnum> CONSTANTS = new HashMap<String, ReadingContextEnum>();

    static {
        for (ReadingContextEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    ReadingContextEnum(String value) {
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
    public static ReadingContextEnum fromValue(String value) {
        ReadingContextEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
