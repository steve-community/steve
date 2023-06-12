
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
    "status",
    "statusInfo"
})
@Generated("jsonschema2pojo")
public class SetDisplayMessageResponse implements ResponseType
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
     * This indicates whether the Charging Station is able to display the message.
     * 
     * (Required)
     * 
     */
    @JsonProperty("status")
    @JsonPropertyDescription("This indicates whether the Charging Station is able to display the message.\r\n")
    @NotNull
    private DisplayMessageStatusEnum status;
    /**
     * Element providing more information about the status.
     * 
     * 
     */
    @JsonProperty("statusInfo")
    @JsonPropertyDescription("Element providing more information about the status.\r\n")
    @Valid
    private StatusInfo statusInfo;

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

    public SetDisplayMessageResponse withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * This indicates whether the Charging Station is able to display the message.
     * 
     * (Required)
     * 
     */
    @JsonProperty("status")
    public DisplayMessageStatusEnum getStatus() {
        return status;
    }

    /**
     * This indicates whether the Charging Station is able to display the message.
     * 
     * (Required)
     * 
     */
    @JsonProperty("status")
    public void setStatus(DisplayMessageStatusEnum status) {
        this.status = status;
    }

    public SetDisplayMessageResponse withStatus(DisplayMessageStatusEnum status) {
        this.status = status;
        return this;
    }

    /**
     * Element providing more information about the status.
     * 
     * 
     */
    @JsonProperty("statusInfo")
    public StatusInfo getStatusInfo() {
        return statusInfo;
    }

    /**
     * Element providing more information about the status.
     * 
     * 
     */
    @JsonProperty("statusInfo")
    public void setStatusInfo(StatusInfo statusInfo) {
        this.statusInfo = statusInfo;
    }

    public SetDisplayMessageResponse withStatusInfo(StatusInfo statusInfo) {
        this.statusInfo = statusInfo;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(SetDisplayMessageResponse.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("status");
        sb.append('=');
        sb.append(((this.status == null)?"<null>":this.status));
        sb.append(',');
        sb.append("statusInfo");
        sb.append('=');
        sb.append(((this.statusInfo == null)?"<null>":this.statusInfo));
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
        result = ((result* 31)+((this.statusInfo == null)? 0 :this.statusInfo.hashCode()));
        result = ((result* 31)+((this.status == null)? 0 :this.status.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof SetDisplayMessageResponse) == false) {
            return false;
        }
        SetDisplayMessageResponse rhs = ((SetDisplayMessageResponse) other);
        return ((((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData)))&&((this.statusInfo == rhs.statusInfo)||((this.statusInfo!= null)&&this.statusInfo.equals(rhs.statusInfo))))&&((this.status == rhs.status)||((this.status!= null)&&this.status.equals(rhs.status))));
    }

}
