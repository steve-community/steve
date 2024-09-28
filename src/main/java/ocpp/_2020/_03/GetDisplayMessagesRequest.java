
package ocpp._2020._03;

import java.util.ArrayList;
import java.util.List;
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
    "id",
    "requestId",
    "priority",
    "state"
})
public class GetDisplayMessagesRequest implements RequestType
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
     * If provided the Charging Station shall return Display Messages of the given ids. This field SHALL NOT contain more ids than set in &lt;&lt;configkey-number-of-display-messages,NumberOfDisplayMessages.maxLimit&gt;&gt;
     * 
     * 
     * 
     */
    @JsonProperty("id")
    @JsonPropertyDescription("If provided the Charging Station shall return Display Messages of the given ids. This field SHALL NOT contain more ids than set in &lt;&lt;configkey-number-of-display-messages,NumberOfDisplayMessages.maxLimit&gt;&gt;\r\n\r\n")
    @Size(min = 1)
    @Valid
    private List<Integer> id = new ArrayList<Integer>();
    /**
     * The Id of this request.
     * 
     * (Required)
     * 
     */
    @JsonProperty("requestId")
    @JsonPropertyDescription("The Id of this request.\r\n")
    @NotNull
    private Integer requestId;
    /**
     * If provided the Charging Station shall return Display Messages with the given priority only.
     * 
     * 
     */
    @JsonProperty("priority")
    @JsonPropertyDescription("If provided the Charging Station shall return Display Messages with the given priority only.\r\n")
    private MessagePriorityEnum priority;
    /**
     * If provided the Charging Station shall return Display Messages with the given state only. 
     * 
     * 
     */
    @JsonProperty("state")
    @JsonPropertyDescription("If provided the Charging Station shall return Display Messages with the given state only. \r\n")
    private MessageStateEnum state;

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

    public GetDisplayMessagesRequest withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * If provided the Charging Station shall return Display Messages of the given ids. This field SHALL NOT contain more ids than set in &lt;&lt;configkey-number-of-display-messages,NumberOfDisplayMessages.maxLimit&gt;&gt;
     * 
     * 
     * 
     */
    @JsonProperty("id")
    public List<Integer> getId() {
        return id;
    }

    /**
     * If provided the Charging Station shall return Display Messages of the given ids. This field SHALL NOT contain more ids than set in &lt;&lt;configkey-number-of-display-messages,NumberOfDisplayMessages.maxLimit&gt;&gt;
     * 
     * 
     * 
     */
    @JsonProperty("id")
    public void setId(List<Integer> id) {
        this.id = id;
    }

    public GetDisplayMessagesRequest withId(List<Integer> id) {
        this.id = id;
        return this;
    }

    /**
     * The Id of this request.
     * 
     * (Required)
     * 
     */
    @JsonProperty("requestId")
    public Integer getRequestId() {
        return requestId;
    }

    /**
     * The Id of this request.
     * 
     * (Required)
     * 
     */
    @JsonProperty("requestId")
    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public GetDisplayMessagesRequest withRequestId(Integer requestId) {
        this.requestId = requestId;
        return this;
    }

    /**
     * If provided the Charging Station shall return Display Messages with the given priority only.
     * 
     * 
     */
    @JsonProperty("priority")
    public MessagePriorityEnum getPriority() {
        return priority;
    }

    /**
     * If provided the Charging Station shall return Display Messages with the given priority only.
     * 
     * 
     */
    @JsonProperty("priority")
    public void setPriority(MessagePriorityEnum priority) {
        this.priority = priority;
    }

    public GetDisplayMessagesRequest withPriority(MessagePriorityEnum priority) {
        this.priority = priority;
        return this;
    }

    /**
     * If provided the Charging Station shall return Display Messages with the given state only. 
     * 
     * 
     */
    @JsonProperty("state")
    public MessageStateEnum getState() {
        return state;
    }

    /**
     * If provided the Charging Station shall return Display Messages with the given state only. 
     * 
     * 
     */
    @JsonProperty("state")
    public void setState(MessageStateEnum state) {
        this.state = state;
    }

    public GetDisplayMessagesRequest withState(MessageStateEnum state) {
        this.state = state;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(GetDisplayMessagesRequest.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null)?"<null>":this.id));
        sb.append(',');
        sb.append("requestId");
        sb.append('=');
        sb.append(((this.requestId == null)?"<null>":this.requestId));
        sb.append(',');
        sb.append("priority");
        sb.append('=');
        sb.append(((this.priority == null)?"<null>":this.priority));
        sb.append(',');
        sb.append("state");
        sb.append('=');
        sb.append(((this.state == null)?"<null>":this.state));
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
        result = ((result* 31)+((this.id == null)? 0 :this.id.hashCode()));
        result = ((result* 31)+((this.state == null)? 0 :this.state.hashCode()));
        result = ((result* 31)+((this.priority == null)? 0 :this.priority.hashCode()));
        result = ((result* 31)+((this.requestId == null)? 0 :this.requestId.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof GetDisplayMessagesRequest) == false) {
            return false;
        }
        GetDisplayMessagesRequest rhs = ((GetDisplayMessagesRequest) other);
        return ((((((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData)))&&((this.id == rhs.id)||((this.id!= null)&&this.id.equals(rhs.id))))&&((this.state == rhs.state)||((this.state!= null)&&this.state.equals(rhs.state))))&&((this.priority == rhs.priority)||((this.priority!= null)&&this.priority.equals(rhs.priority))))&&((this.requestId == rhs.requestId)||((this.requestId!= null)&&this.requestId.equals(rhs.requestId))));
    }

}
