
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
    "statusInfo",
    "schedule"
})
@Generated("jsonschema2pojo")
public class GetCompositeScheduleResponse implements ResponseType
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
     * The Charging Station will indicate if it was
     * able to process the request
     * 
     * (Required)
     * 
     */
    @JsonProperty("status")
    @JsonPropertyDescription("The Charging Station will indicate if it was\r\nable to process the request\r\n")
    @NotNull
    private GenericStatusEnum status;
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
     * Composite_ Schedule
     * urn:x-oca:ocpp:uid:2:233362
     * 
     * 
     */
    @JsonProperty("schedule")
    @JsonPropertyDescription("Composite_ Schedule\r\nurn:x-oca:ocpp:uid:2:233362\r\n")
    @Valid
    private CompositeSchedule schedule;

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

    public GetCompositeScheduleResponse withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * The Charging Station will indicate if it was
     * able to process the request
     * 
     * (Required)
     * 
     */
    @JsonProperty("status")
    public GenericStatusEnum getStatus() {
        return status;
    }

    /**
     * The Charging Station will indicate if it was
     * able to process the request
     * 
     * (Required)
     * 
     */
    @JsonProperty("status")
    public void setStatus(GenericStatusEnum status) {
        this.status = status;
    }

    public GetCompositeScheduleResponse withStatus(GenericStatusEnum status) {
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

    public GetCompositeScheduleResponse withStatusInfo(StatusInfo statusInfo) {
        this.statusInfo = statusInfo;
        return this;
    }

    /**
     * Composite_ Schedule
     * urn:x-oca:ocpp:uid:2:233362
     * 
     * 
     */
    @JsonProperty("schedule")
    public CompositeSchedule getSchedule() {
        return schedule;
    }

    /**
     * Composite_ Schedule
     * urn:x-oca:ocpp:uid:2:233362
     * 
     * 
     */
    @JsonProperty("schedule")
    public void setSchedule(CompositeSchedule schedule) {
        this.schedule = schedule;
    }

    public GetCompositeScheduleResponse withSchedule(CompositeSchedule schedule) {
        this.schedule = schedule;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(GetCompositeScheduleResponse.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
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
        sb.append("schedule");
        sb.append('=');
        sb.append(((this.schedule == null)?"<null>":this.schedule));
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
        result = ((result* 31)+((this.schedule == null)? 0 :this.schedule.hashCode()));
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
        if ((other instanceof GetCompositeScheduleResponse) == false) {
            return false;
        }
        GetCompositeScheduleResponse rhs = ((GetCompositeScheduleResponse) other);
        return (((((this.schedule == rhs.schedule)||((this.schedule!= null)&&this.schedule.equals(rhs.schedule)))&&((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData))))&&((this.statusInfo == rhs.statusInfo)||((this.statusInfo!= null)&&this.statusInfo.equals(rhs.statusInfo))))&&((this.status == rhs.status)||((this.status!= null)&&this.status.equals(rhs.status))));
    }

}
