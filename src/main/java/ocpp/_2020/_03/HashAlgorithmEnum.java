
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Used algorithms for the hashes provided.
 * 
 * 
 */
public enum HashAlgorithmEnum {

    SHA_256("SHA256"),
    SHA_384("SHA384"),
    SHA_512("SHA512");
    private final String value;
    private final static Map<String, HashAlgorithmEnum> CONSTANTS = new HashMap<String, HashAlgorithmEnum>();

    static {
        for (HashAlgorithmEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    HashAlgorithmEnum(String value) {
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
    public static HashAlgorithmEnum fromValue(String value) {
        HashAlgorithmEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
