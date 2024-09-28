
package ocpp._2020._03;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.rwth.idsg.ocpp.jaxb.RequestType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "messageId",
    "data",
    "vendorId"
})
public class DataTransferRequest implements RequestType
{

    /**
     * This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.
     * 
     */
    @JsonProperty("customData")
    @JsonPropertyDescription("This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.")
    @Valid
    private CustomData customData;
    /**
     * May be used to indicate a specific message or implementation.
     * 
     * 
     */
    @JsonProperty("messageId")
    @JsonPropertyDescription("May be used to indicate a specific message or implementation.\r\n")
    @Size(max = 50)
    private String messageId;
    /**
     * Data without specified length or format. This needs to be decided by both parties (Open to implementation).
     * 
     * 
     */
    @JsonProperty("data")
    @JsonPropertyDescription("Data without specified length or format. This needs to be decided by both parties (Open to implementation).\r\n")
    private Object data;
    /**
     * This identifies the Vendor specific implementation
     * 
     * 
     * (Required)
     * 
     */
    @JsonProperty("vendorId")
    @JsonPropertyDescription("This identifies the Vendor specific implementation\r\n\r\n")
    @Size(max = 255)
    @NotNull
    private String vendorId;

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

    public DataTransferRequest withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * May be used to indicate a specific message or implementation.
     * 
     * 
     */
    @JsonProperty("messageId")
    public String getMessageId() {
        return messageId;
    }

    /**
     * May be used to indicate a specific message or implementation.
     * 
     * 
     */
    @JsonProperty("messageId")
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public DataTransferRequest withMessageId(String messageId) {
        this.messageId = messageId;
        return this;
    }

    /**
     * Data without specified length or format. This needs to be decided by both parties (Open to implementation).
     * 
     * 
     */
    @JsonProperty("data")
    public Object getData() {
        return data;
    }

    /**
     * Data without specified length or format. This needs to be decided by both parties (Open to implementation).
     * 
     * 
     */
    @JsonProperty("data")
    public void setData(Object data) {
        this.data = data;
    }

    public DataTransferRequest withData(Object data) {
        this.data = data;
        return this;
    }

    /**
     * This identifies the Vendor specific implementation
     * 
     * 
     * (Required)
     * 
     */
    @JsonProperty("vendorId")
    public String getVendorId() {
        return vendorId;
    }

    /**
     * This identifies the Vendor specific implementation
     * 
     * 
     * (Required)
     * 
     */
    @JsonProperty("vendorId")
    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public DataTransferRequest withVendorId(String vendorId) {
        this.vendorId = vendorId;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(DataTransferRequest.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("messageId");
        sb.append('=');
        sb.append(((this.messageId == null)?"<null>":this.messageId));
        sb.append(',');
        sb.append("data");
        sb.append('=');
        sb.append(((this.data == null)?"<null>":this.data));
        sb.append(',');
        sb.append("vendorId");
        sb.append('=');
        sb.append(((this.vendorId == null)?"<null>":this.vendorId));
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
        result = ((result* 31)+((this.messageId == null)? 0 :this.messageId.hashCode()));
        result = ((result* 31)+((this.vendorId == null)? 0 :this.vendorId.hashCode()));
        result = ((result* 31)+((this.customData == null)? 0 :this.customData.hashCode()));
        result = ((result* 31)+((this.data == null)? 0 :this.data.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof DataTransferRequest) == false) {
            return false;
        }
        DataTransferRequest rhs = ((DataTransferRequest) other);
        return (((((this.messageId == rhs.messageId)||((this.messageId!= null)&&this.messageId.equals(rhs.messageId)))&&((this.vendorId == rhs.vendorId)||((this.vendorId!= null)&&this.vendorId.equals(rhs.vendorId))))&&((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData))))&&((this.data == rhs.data)||((this.data!= null)&&this.data.equals(rhs.data))));
    }

}
