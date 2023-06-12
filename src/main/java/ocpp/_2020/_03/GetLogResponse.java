
package ocpp._2020._03;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.rwth.idsg.ocpp.jaxb.ResponseType;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "status",
    "statusInfo",
    "filename"
})
@Generated("jsonschema2pojo")
public class GetLogResponse implements ResponseType
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
     * This field indicates whether the Charging Station was able to accept the request.
     * 
     * (Required)
     * 
     */
    @JsonProperty("status")
    @JsonPropertyDescription("This field indicates whether the Charging Station was able to accept the request.\r\n")
    @NotNull
    private LogStatusEnum status;
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
     * This contains the name of the log file that will be uploaded. This field is not present when no logging information is available.
     * 
     * 
     */
    @JsonProperty("filename")
    @JsonPropertyDescription("This contains the name of the log file that will be uploaded. This field is not present when no logging information is available.\r\n")
    @Size(max = 255)
    private String filename;

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

    public GetLogResponse withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * This field indicates whether the Charging Station was able to accept the request.
     * 
     * (Required)
     * 
     */
    @JsonProperty("status")
    public LogStatusEnum getStatus() {
        return status;
    }

    /**
     * This field indicates whether the Charging Station was able to accept the request.
     * 
     * (Required)
     * 
     */
    @JsonProperty("status")
    public void setStatus(LogStatusEnum status) {
        this.status = status;
    }

    public GetLogResponse withStatus(LogStatusEnum status) {
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

    public GetLogResponse withStatusInfo(StatusInfo statusInfo) {
        this.statusInfo = statusInfo;
        return this;
    }

    /**
     * This contains the name of the log file that will be uploaded. This field is not present when no logging information is available.
     * 
     * 
     */
    @JsonProperty("filename")
    public String getFilename() {
        return filename;
    }

    /**
     * This contains the name of the log file that will be uploaded. This field is not present when no logging information is available.
     * 
     * 
     */
    @JsonProperty("filename")
    public void setFilename(String filename) {
        this.filename = filename;
    }

    public GetLogResponse withFilename(String filename) {
        this.filename = filename;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(GetLogResponse.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
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
        sb.append("filename");
        sb.append('=');
        sb.append(((this.filename == null)?"<null>":this.filename));
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
        result = ((result* 31)+((this.filename == null)? 0 :this.filename.hashCode()));
        result = ((result* 31)+((this.status == null)? 0 :this.status.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof GetLogResponse) == false) {
            return false;
        }
        GetLogResponse rhs = ((GetLogResponse) other);
        return (((((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData)))&&((this.statusInfo == rhs.statusInfo)||((this.statusInfo!= null)&&this.statusInfo.equals(rhs.statusInfo))))&&((this.filename == rhs.filename)||((this.filename!= null)&&this.filename.equals(rhs.filename))))&&((this.status == rhs.status)||((this.status!= null)&&this.status.equals(rhs.status))));
    }

}
