
package ocpp._2020._03;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.rwth.idsg.ocpp.jaxb.RequestType;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "status",
    "requestId"
})
@Generated("jsonschema2pojo")
public class LogStatusNotificationRequest implements RequestType
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
     * This contains the status of the log upload.
     * 
     * (Required)
     * 
     */
    @JsonProperty("status")
    @JsonPropertyDescription("This contains the status of the log upload.\r\n")
    @NotNull
    private UploadLogStatusEnum status;
    /**
     * The request id that was provided in GetLogRequest that started this log upload. This field is mandatory,
     * unless the message was triggered by a TriggerMessageRequest AND there is no log upload ongoing.
     * 
     * 
     */
    @JsonProperty("requestId")
    @JsonPropertyDescription("The request id that was provided in GetLogRequest that started this log upload. This field is mandatory,\r\nunless the message was triggered by a TriggerMessageRequest AND there is no log upload ongoing.\r\n")
    private Integer requestId;

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

    public LogStatusNotificationRequest withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * This contains the status of the log upload.
     * 
     * (Required)
     * 
     */
    @JsonProperty("status")
    public UploadLogStatusEnum getStatus() {
        return status;
    }

    /**
     * This contains the status of the log upload.
     * 
     * (Required)
     * 
     */
    @JsonProperty("status")
    public void setStatus(UploadLogStatusEnum status) {
        this.status = status;
    }

    public LogStatusNotificationRequest withStatus(UploadLogStatusEnum status) {
        this.status = status;
        return this;
    }

    /**
     * The request id that was provided in GetLogRequest that started this log upload. This field is mandatory,
     * unless the message was triggered by a TriggerMessageRequest AND there is no log upload ongoing.
     * 
     * 
     */
    @JsonProperty("requestId")
    public Integer getRequestId() {
        return requestId;
    }

    /**
     * The request id that was provided in GetLogRequest that started this log upload. This field is mandatory,
     * unless the message was triggered by a TriggerMessageRequest AND there is no log upload ongoing.
     * 
     * 
     */
    @JsonProperty("requestId")
    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public LogStatusNotificationRequest withRequestId(Integer requestId) {
        this.requestId = requestId;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(LogStatusNotificationRequest.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("status");
        sb.append('=');
        sb.append(((this.status == null)?"<null>":this.status));
        sb.append(',');
        sb.append("requestId");
        sb.append('=');
        sb.append(((this.requestId == null)?"<null>":this.requestId));
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
        result = ((result* 31)+((this.requestId == null)? 0 :this.requestId.hashCode()));
        result = ((result* 31)+((this.status == null)? 0 :this.status.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof LogStatusNotificationRequest) == false) {
            return false;
        }
        LogStatusNotificationRequest rhs = ((LogStatusNotificationRequest) other);
        return ((((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData)))&&((this.requestId == rhs.requestId)||((this.requestId!= null)&&this.requestId.equals(rhs.requestId))))&&((this.status == rhs.status)||((this.status!= null)&&this.status.equals(rhs.status))));
    }

}
