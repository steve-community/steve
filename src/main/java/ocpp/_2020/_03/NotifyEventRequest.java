
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
    "generatedAt",
    "tbc",
    "seqNo",
    "eventData"
})
@Generated("jsonschema2pojo")
public class NotifyEventRequest implements RequestType
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
     * “to be continued” indicator. Indicates whether another part of the report follows in an upcoming notifyEventRequest message. Default value when omitted is false. 
     * 
     * 
     */
    @JsonProperty("tbc")
    @JsonPropertyDescription("\u201cto be continued\u201d indicator. Indicates whether another part of the report follows in an upcoming notifyEventRequest message. Default value when omitted is false. \r\n")
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
     * 
     * (Required)
     * 
     */
    @JsonProperty("eventData")
    @Size(min = 1)
    @Valid
    @NotNull
    private List<EventData> eventData = new ArrayList<EventData>();

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

    public NotifyEventRequest withCustomData(CustomData customData) {
        this.customData = customData;
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

    public NotifyEventRequest withGeneratedAt(DateTime generatedAt) {
        this.generatedAt = generatedAt;
        return this;
    }

    /**
     * “to be continued” indicator. Indicates whether another part of the report follows in an upcoming notifyEventRequest message. Default value when omitted is false. 
     * 
     * 
     */
    @JsonProperty("tbc")
    public Boolean getTbc() {
        return tbc;
    }

    /**
     * “to be continued” indicator. Indicates whether another part of the report follows in an upcoming notifyEventRequest message. Default value when omitted is false. 
     * 
     * 
     */
    @JsonProperty("tbc")
    public void setTbc(Boolean tbc) {
        this.tbc = tbc;
    }

    public NotifyEventRequest withTbc(Boolean tbc) {
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

    public NotifyEventRequest withSeqNo(Integer seqNo) {
        this.seqNo = seqNo;
        return this;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("eventData")
    public List<EventData> getEventData() {
        return eventData;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("eventData")
    public void setEventData(List<EventData> eventData) {
        this.eventData = eventData;
    }

    public NotifyEventRequest withEventData(List<EventData> eventData) {
        this.eventData = eventData;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(NotifyEventRequest.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("generatedAt");
        sb.append('=');
        sb.append(((this.generatedAt == null)?"<null>":this.generatedAt));
        sb.append(',');
        sb.append("tbc");
        sb.append('=');
        sb.append(((this.tbc == null)?"<null>":this.tbc));
        sb.append(',');
        sb.append("seqNo");
        sb.append('=');
        sb.append(((this.seqNo == null)?"<null>":this.seqNo));
        sb.append(',');
        sb.append("eventData");
        sb.append('=');
        sb.append(((this.eventData == null)?"<null>":this.eventData));
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
        result = ((result* 31)+((this.generatedAt == null)? 0 :this.generatedAt.hashCode()));
        result = ((result* 31)+((this.customData == null)? 0 :this.customData.hashCode()));
        result = ((result* 31)+((this.eventData == null)? 0 :this.eventData.hashCode()));
        result = ((result* 31)+((this.tbc == null)? 0 :this.tbc.hashCode()));
        result = ((result* 31)+((this.seqNo == null)? 0 :this.seqNo.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof NotifyEventRequest) == false) {
            return false;
        }
        NotifyEventRequest rhs = ((NotifyEventRequest) other);
        return ((((((this.generatedAt == rhs.generatedAt)||((this.generatedAt!= null)&&this.generatedAt.equals(rhs.generatedAt)))&&((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData))))&&((this.eventData == rhs.eventData)||((this.eventData!= null)&&this.eventData.equals(rhs.eventData))))&&((this.tbc == rhs.tbc)||((this.tbc!= null)&&this.tbc.equals(rhs.tbc))))&&((this.seqNo == rhs.seqNo)||((this.seqNo!= null)&&this.seqNo.equals(rhs.seqNo))));
    }

}
