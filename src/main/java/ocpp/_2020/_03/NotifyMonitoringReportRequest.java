
package ocpp._2020._03;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.rwth.idsg.ocpp.jaxb.RequestType;
import org.joda.time.DateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "monitor",
    "requestId",
    "tbc",
    "seqNo",
    "generatedAt"
})
@Generated("jsonschema2pojo")
public class NotifyMonitoringReportRequest implements RequestType
{

    /**
     * This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.
     * 
     */
    @JsonProperty("customData")
    @JsonPropertyDescription("This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.")
    @Valid
    private CustomData customData;
    @JsonProperty("monitor")
    @Size(min = 1)
    @Valid
    private List<MonitoringData> monitor = new ArrayList<MonitoringData>();
    /**
     * The id of the GetMonitoringRequest that requested this report.
     * 
     * 
     * (Required)
     * 
     */
    @JsonProperty("requestId")
    @JsonPropertyDescription("The id of the GetMonitoringRequest that requested this report.\r\n\r\n")
    @NotNull
    private Integer requestId;
    /**
     * “to be continued” indicator. Indicates whether another part of the monitoringData follows in an upcoming notifyMonitoringReportRequest message. Default value when omitted is false.
     * 
     * 
     */
    @JsonProperty("tbc")
    @JsonPropertyDescription("\u201cto be continued\u201d indicator. Indicates whether another part of the monitoringData follows in an upcoming notifyMonitoringReportRequest message. Default value when omitted is false.\r\n")
    private Boolean tbc = false;
    /**
     * Sequence number of this message. First message starts at 0.
     * 
     * (Required)
     * 
     */
    @JsonProperty("seqNo")
    @JsonPropertyDescription("Sequence number of this message. First message starts at 0.\r\n")
    @NotNull
    private Integer seqNo;
    /**
     * Timestamp of the moment this message was generated at the Charging Station.
     * 
     * (Required)
     * 
     */
    @JsonProperty("generatedAt")
    @JsonPropertyDescription("Timestamp of the moment this message was generated at the Charging Station.\r\n")
    @NotNull
    private DateTime generatedAt;

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

    public NotifyMonitoringReportRequest withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    @JsonProperty("monitor")
    public List<MonitoringData> getMonitor() {
        return monitor;
    }

    @JsonProperty("monitor")
    public void setMonitor(List<MonitoringData> monitor) {
        this.monitor = monitor;
    }

    public NotifyMonitoringReportRequest withMonitor(List<MonitoringData> monitor) {
        this.monitor = monitor;
        return this;
    }

    /**
     * The id of the GetMonitoringRequest that requested this report.
     * 
     * 
     * (Required)
     * 
     */
    @JsonProperty("requestId")
    public Integer getRequestId() {
        return requestId;
    }

    /**
     * The id of the GetMonitoringRequest that requested this report.
     * 
     * 
     * (Required)
     * 
     */
    @JsonProperty("requestId")
    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public NotifyMonitoringReportRequest withRequestId(Integer requestId) {
        this.requestId = requestId;
        return this;
    }

    /**
     * “to be continued” indicator. Indicates whether another part of the monitoringData follows in an upcoming notifyMonitoringReportRequest message. Default value when omitted is false.
     * 
     * 
     */
    @JsonProperty("tbc")
    public Boolean getTbc() {
        return tbc;
    }

    /**
     * “to be continued” indicator. Indicates whether another part of the monitoringData follows in an upcoming notifyMonitoringReportRequest message. Default value when omitted is false.
     * 
     * 
     */
    @JsonProperty("tbc")
    public void setTbc(Boolean tbc) {
        this.tbc = tbc;
    }

    public NotifyMonitoringReportRequest withTbc(Boolean tbc) {
        this.tbc = tbc;
        return this;
    }

    /**
     * Sequence number of this message. First message starts at 0.
     * 
     * (Required)
     * 
     */
    @JsonProperty("seqNo")
    public Integer getSeqNo() {
        return seqNo;
    }

    /**
     * Sequence number of this message. First message starts at 0.
     * 
     * (Required)
     * 
     */
    @JsonProperty("seqNo")
    public void setSeqNo(Integer seqNo) {
        this.seqNo = seqNo;
    }

    public NotifyMonitoringReportRequest withSeqNo(Integer seqNo) {
        this.seqNo = seqNo;
        return this;
    }

    /**
     * Timestamp of the moment this message was generated at the Charging Station.
     * 
     * (Required)
     * 
     */
    @JsonProperty("generatedAt")
    public DateTime getGeneratedAt() {
        return generatedAt;
    }

    /**
     * Timestamp of the moment this message was generated at the Charging Station.
     * 
     * (Required)
     * 
     */
    @JsonProperty("generatedAt")
    public void setGeneratedAt(DateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public NotifyMonitoringReportRequest withGeneratedAt(DateTime generatedAt) {
        this.generatedAt = generatedAt;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(NotifyMonitoringReportRequest.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("monitor");
        sb.append('=');
        sb.append(((this.monitor == null)?"<null>":this.monitor));
        sb.append(',');
        sb.append("requestId");
        sb.append('=');
        sb.append(((this.requestId == null)?"<null>":this.requestId));
        sb.append(',');
        sb.append("tbc");
        sb.append('=');
        sb.append(((this.tbc == null)?"<null>":this.tbc));
        sb.append(',');
        sb.append("seqNo");
        sb.append('=');
        sb.append(((this.seqNo == null)?"<null>":this.seqNo));
        sb.append(',');
        sb.append("generatedAt");
        sb.append('=');
        sb.append(((this.generatedAt == null)?"<null>":this.generatedAt));
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
        result = ((result* 31)+((this.tbc == null)? 0 :this.tbc.hashCode()));
        result = ((result* 31)+((this.seqNo == null)? 0 :this.seqNo.hashCode()));
        result = ((result* 31)+((this.requestId == null)? 0 :this.requestId.hashCode()));
        result = ((result* 31)+((this.generatedAt == null)? 0 :this.generatedAt.hashCode()));
        result = ((result* 31)+((this.customData == null)? 0 :this.customData.hashCode()));
        result = ((result* 31)+((this.monitor == null)? 0 :this.monitor.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof NotifyMonitoringReportRequest) == false) {
            return false;
        }
        NotifyMonitoringReportRequest rhs = ((NotifyMonitoringReportRequest) other);
        return (((((((this.tbc == rhs.tbc)||((this.tbc!= null)&&this.tbc.equals(rhs.tbc)))&&((this.seqNo == rhs.seqNo)||((this.seqNo!= null)&&this.seqNo.equals(rhs.seqNo))))&&((this.requestId == rhs.requestId)||((this.requestId!= null)&&this.requestId.equals(rhs.requestId))))&&((this.generatedAt == rhs.generatedAt)||((this.generatedAt!= null)&&this.generatedAt.equals(rhs.generatedAt))))&&((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData))))&&((this.monitor == rhs.monitor)||((this.monitor!= null)&&this.monitor.equals(rhs.monitor))));
    }

}
