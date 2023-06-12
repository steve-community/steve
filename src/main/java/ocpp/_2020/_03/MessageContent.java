
package ocpp._2020._03;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Message_ Content
 * urn:x-enexis:ecdm:uid:2:234490
 * Contains message details, for a message to be displayed on a Charging Station.
 * 
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "format",
    "language",
    "content"
})
@Generated("jsonschema2pojo")
public class MessageContent {

    /**
     * This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.
     * 
     */
    @JsonProperty("customData")
    @JsonPropertyDescription("This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.")
    @Valid
    private CustomData customData;
    /**
     * Message_ Content. Format. Message_ Format_ Code
     * urn:x-enexis:ecdm:uid:1:570848
     * Format of the message.
     * 
     * (Required)
     * 
     */
    @JsonProperty("format")
    @JsonPropertyDescription("Message_ Content. Format. Message_ Format_ Code\r\nurn:x-enexis:ecdm:uid:1:570848\r\nFormat of the message.\r\n")
    @NotNull
    private MessageFormatEnum format;
    /**
     * Message_ Content. Language. Language_ Code
     * urn:x-enexis:ecdm:uid:1:570849
     * Message language identifier. Contains a language code as defined in &lt;&lt;ref-RFC5646,[RFC5646]&gt;&gt;.
     * 
     * 
     */
    @JsonProperty("language")
    @JsonPropertyDescription("Message_ Content. Language. Language_ Code\r\nurn:x-enexis:ecdm:uid:1:570849\r\nMessage language identifier. Contains a language code as defined in &lt;&lt;ref-RFC5646,[RFC5646]&gt;&gt;.\r\n")
    @Size(max = 8)
    private String language;
    /**
     * Message_ Content. Content. Message
     * urn:x-enexis:ecdm:uid:1:570852
     * Message contents.
     * 
     * 
     * (Required)
     * 
     */
    @JsonProperty("content")
    @JsonPropertyDescription("Message_ Content. Content. Message\r\nurn:x-enexis:ecdm:uid:1:570852\r\nMessage contents.\r\n\r\n")
    @Size(max = 512)
    @NotNull
    private String content;

    /**
     * This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.
     * 
     */
    @JsonProperty("customData")
    public CustomData getCustomData() {
        return customData;
    }

    /**
     * This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.
     * 
     */
    @JsonProperty("customData")
    public void setCustomData(CustomData customData) {
        this.customData = customData;
    }

    public MessageContent withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * Message_ Content. Format. Message_ Format_ Code
     * urn:x-enexis:ecdm:uid:1:570848
     * Format of the message.
     * 
     * (Required)
     * 
     */
    @JsonProperty("format")
    public MessageFormatEnum getFormat() {
        return format;
    }

    /**
     * Message_ Content. Format. Message_ Format_ Code
     * urn:x-enexis:ecdm:uid:1:570848
     * Format of the message.
     * 
     * (Required)
     * 
     */
    @JsonProperty("format")
    public void setFormat(MessageFormatEnum format) {
        this.format = format;
    }

    public MessageContent withFormat(MessageFormatEnum format) {
        this.format = format;
        return this;
    }

    /**
     * Message_ Content. Language. Language_ Code
     * urn:x-enexis:ecdm:uid:1:570849
     * Message language identifier. Contains a language code as defined in &lt;&lt;ref-RFC5646,[RFC5646]&gt;&gt;.
     * 
     * 
     */
    @JsonProperty("language")
    public String getLanguage() {
        return language;
    }

    /**
     * Message_ Content. Language. Language_ Code
     * urn:x-enexis:ecdm:uid:1:570849
     * Message language identifier. Contains a language code as defined in &lt;&lt;ref-RFC5646,[RFC5646]&gt;&gt;.
     * 
     * 
     */
    @JsonProperty("language")
    public void setLanguage(String language) {
        this.language = language;
    }

    public MessageContent withLanguage(String language) {
        this.language = language;
        return this;
    }

    /**
     * Message_ Content. Content. Message
     * urn:x-enexis:ecdm:uid:1:570852
     * Message contents.
     * 
     * 
     * (Required)
     * 
     */
    @JsonProperty("content")
    public String getContent() {
        return content;
    }

    /**
     * Message_ Content. Content. Message
     * urn:x-enexis:ecdm:uid:1:570852
     * Message contents.
     * 
     * 
     * (Required)
     * 
     */
    @JsonProperty("content")
    public void setContent(String content) {
        this.content = content;
    }

    public MessageContent withContent(String content) {
        this.content = content;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MessageContent.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("format");
        sb.append('=');
        sb.append(((this.format == null)?"<null>":this.format));
        sb.append(',');
        sb.append("language");
        sb.append('=');
        sb.append(((this.language == null)?"<null>":this.language));
        sb.append(',');
        sb.append("content");
        sb.append('=');
        sb.append(((this.content == null)?"<null>":this.content));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.format == null)? 0 :this.format.hashCode()));
        result = ((result* 31)+((this.customData == null)? 0 :this.customData.hashCode()));
        result = ((result* 31)+((this.language == null)? 0 :this.language.hashCode()));
        result = ((result* 31)+((this.content == null)? 0 :this.content.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof MessageContent) == false) {
            return false;
        }
        MessageContent rhs = ((MessageContent) other);
        return (((((this.format == rhs.format)||((this.format!= null)&&this.format.equals(rhs.format)))&&((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData))))&&((this.language == rhs.language)||((this.language!= null)&&this.language.equals(rhs.language))))&&((this.content == rhs.content)||((this.content!= null)&&this.content.equals(rhs.content))));
    }

}
