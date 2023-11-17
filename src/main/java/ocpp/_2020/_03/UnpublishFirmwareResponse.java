
package ocpp._2020._03;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.rwth.idsg.ocpp.jaxb.ResponseType;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "status"
})
@Generated("jsonschema2pojo")
public class UnpublishFirmwareResponse implements ResponseType
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
     * Indicates whether the Local Controller succeeded in unpublishing the firmware.
     * 
     * (Required)
     * 
     */
    @JsonProperty("status")
    @JsonPropertyDescription("Indicates whether the Local Controller succeeded in unpublishing the firmware.\r\n")
    @NotNull
    private UnpublishFirmwareStatusEnum status;

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

    public UnpublishFirmwareResponse withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * Indicates whether the Local Controller succeeded in unpublishing the firmware.
     * 
     * (Required)
     * 
     */
    @JsonProperty("status")
    public UnpublishFirmwareStatusEnum getStatus() {
        return status;
    }

    /**
     * Indicates whether the Local Controller succeeded in unpublishing the firmware.
     * 
     * (Required)
     * 
     */
    @JsonProperty("status")
    public void setStatus(UnpublishFirmwareStatusEnum status) {
        this.status = status;
    }

    public UnpublishFirmwareResponse withStatus(UnpublishFirmwareStatusEnum status) {
        this.status = status;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(UnpublishFirmwareResponse.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("status");
        sb.append('=');
        sb.append(((this.status == null)?"<null>":this.status));
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
        result = ((result* 31)+((this.customData == null)? 0 :this.customData.hashCode()));
        result = ((result* 31)+((this.status == null)? 0 :this.status.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof UnpublishFirmwareResponse) == false) {
            return false;
        }
        UnpublishFirmwareResponse rhs = ((UnpublishFirmwareResponse) other);
        return (((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData)))&&((this.status == rhs.status)||((this.status!= null)&&this.status.equals(rhs.status))));
    }

}
