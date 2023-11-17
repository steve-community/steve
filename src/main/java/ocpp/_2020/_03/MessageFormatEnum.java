
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Message_ Content. Format. Message_ Format_ Code
 * urn:x-enexis:ecdm:uid:1:570848
 * Format of the message.
 * 
 * 
 */
@Generated("jsonschema2pojo")
public enum MessageFormatEnum {

    ASCII("ASCII"),
    HTML("HTML"),
    URI("URI"),
    UTF_8("UTF8");
    private final String value;
    private final static Map<String, MessageFormatEnum> CONSTANTS = new HashMap<String, MessageFormatEnum>();

    static {
        for (MessageFormatEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    MessageFormatEnum(String value) {
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
    public static MessageFormatEnum fromValue(String value) {
        MessageFormatEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
