
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * This indicates the success or failure of the data transfer.
 * 
 * 
 */
public enum DataTransferStatusEnum {

    ACCEPTED("Accepted"),
    REJECTED("Rejected"),
    UNKNOWN_MESSAGE_ID("UnknownMessageId"),
    UNKNOWN_VENDOR_ID("UnknownVendorId");
    private final String value;
    private final static Map<String, DataTransferStatusEnum> CONSTANTS = new HashMap<String, DataTransferStatusEnum>();

    static {
        for (DataTransferStatusEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    DataTransferStatusEnum(String value) {
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
    public static DataTransferStatusEnum fromValue(String value) {
        DataTransferStatusEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
