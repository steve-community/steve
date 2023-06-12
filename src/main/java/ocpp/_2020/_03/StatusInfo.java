
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
 * Element providing more information about the status.
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "reasonCode",
    "additionalInfo"
})
@Generated("jsonschema2pojo")
public class StatusInfo {

    /**
     * This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.
     * 
     */
    @JsonProperty("customData")
    @JsonPropertyDescription("This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.")
    @Valid
    private CustomData customData;
    /**
     * A predefined code for the reason why the status is returned in this response. The string is case-insensitive.
     * 
     * (Required)
     * 
     */
    @JsonProperty("reasonCode")
    @JsonPropertyDescription("A predefined code for the reason why the status is returned in this response. The string is case-insensitive.\r\n")
    @Size(max = 20)
    @NotNull
    private String reasonCode;
    /**
     * Additional text to provide detailed information.
     * 
     * 
     */
    @JsonProperty("additionalInfo")
    @JsonPropertyDescription("Additional text to provide detailed information.\r\n")
    @Size(max = 512)
    private String additionalInfo;

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

    public StatusInfo withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * A predefined code for the reason why the status is returned in this response. The string is case-insensitive.
     * 
     * (Required)
     * 
     */
    @JsonProperty("reasonCode")
    public String getReasonCode() {
        return reasonCode;
    }

    /**
     * A predefined code for the reason why the status is returned in this response. The string is case-insensitive.
     * 
     * (Required)
     * 
     */
    @JsonProperty("reasonCode")
    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public StatusInfo withReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
        return this;
    }

    /**
     * Additional text to provide detailed information.
     * 
     * 
     */
    @JsonProperty("additionalInfo")
    public String getAdditionalInfo() {
        return additionalInfo;
    }

    /**
     * Additional text to provide detailed information.
     * 
     * 
     */
    @JsonProperty("additionalInfo")
    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public StatusInfo withAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(StatusInfo.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("reasonCode");
        sb.append('=');
        sb.append(((this.reasonCode == null)?"<null>":this.reasonCode));
        sb.append(',');
        sb.append("additionalInfo");
        sb.append('=');
        sb.append(((this.additionalInfo == null)?"<null>":this.additionalInfo));
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
        result = ((result* 31)+((this.additionalInfo == null)? 0 :this.additionalInfo.hashCode()));
        result = ((result* 31)+((this.customData == null)? 0 :this.customData.hashCode()));
        result = ((result* 31)+((this.reasonCode == null)? 0 :this.reasonCode.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof StatusInfo) == false) {
            return false;
        }
        StatusInfo rhs = ((StatusInfo) other);
        return ((((this.additionalInfo == rhs.additionalInfo)||((this.additionalInfo!= null)&&this.additionalInfo.equals(rhs.additionalInfo)))&&((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData))))&&((this.reasonCode == rhs.reasonCode)||((this.reasonCode!= null)&&this.reasonCode.equals(rhs.reasonCode))));
    }

}
