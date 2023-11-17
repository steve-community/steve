
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
    "ongoingIndicator",
    "messagesInQueue"
})
@Generated("jsonschema2pojo")
public class GetTransactionStatusResponse implements ResponseType
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
     * Whether the transaction is still ongoing.
     * 
     * 
     */
    @JsonProperty("ongoingIndicator")
    @JsonPropertyDescription("Whether the transaction is still ongoing.\r\n")
    private Boolean ongoingIndicator;
    /**
     * Whether there are still message to be delivered.
     * 
     * (Required)
     * 
     */
    @JsonProperty("messagesInQueue")
    @JsonPropertyDescription("Whether there are still message to be delivered.\r\n")
    @NotNull
    private Boolean messagesInQueue;

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

    public GetTransactionStatusResponse withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * Whether the transaction is still ongoing.
     * 
     * 
     */
    @JsonProperty("ongoingIndicator")
    public Boolean getOngoingIndicator() {
        return ongoingIndicator;
    }

    /**
     * Whether the transaction is still ongoing.
     * 
     * 
     */
    @JsonProperty("ongoingIndicator")
    public void setOngoingIndicator(Boolean ongoingIndicator) {
        this.ongoingIndicator = ongoingIndicator;
    }

    public GetTransactionStatusResponse withOngoingIndicator(Boolean ongoingIndicator) {
        this.ongoingIndicator = ongoingIndicator;
        return this;
    }

    /**
     * Whether there are still message to be delivered.
     * 
     * (Required)
     * 
     */
    @JsonProperty("messagesInQueue")
    public Boolean getMessagesInQueue() {
        return messagesInQueue;
    }

    /**
     * Whether there are still message to be delivered.
     * 
     * (Required)
     * 
     */
    @JsonProperty("messagesInQueue")
    public void setMessagesInQueue(Boolean messagesInQueue) {
        this.messagesInQueue = messagesInQueue;
    }

    public GetTransactionStatusResponse withMessagesInQueue(Boolean messagesInQueue) {
        this.messagesInQueue = messagesInQueue;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(GetTransactionStatusResponse.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("ongoingIndicator");
        sb.append('=');
        sb.append(((this.ongoingIndicator == null)?"<null>":this.ongoingIndicator));
        sb.append(',');
        sb.append("messagesInQueue");
        sb.append('=');
        sb.append(((this.messagesInQueue == null)?"<null>":this.messagesInQueue));
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
        result = ((result* 31)+((this.messagesInQueue == null)? 0 :this.messagesInQueue.hashCode()));
        result = ((result* 31)+((this.customData == null)? 0 :this.customData.hashCode()));
        result = ((result* 31)+((this.ongoingIndicator == null)? 0 :this.ongoingIndicator.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof GetTransactionStatusResponse) == false) {
            return false;
        }
        GetTransactionStatusResponse rhs = ((GetTransactionStatusResponse) other);
        return ((((this.messagesInQueue == rhs.messagesInQueue)||((this.messagesInQueue!= null)&&this.messagesInQueue.equals(rhs.messagesInQueue)))&&((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData))))&&((this.ongoingIndicator == rhs.ongoingIndicator)||((this.ongoingIndicator!= null)&&this.ongoingIndicator.equals(rhs.ongoingIndicator))));
    }

}
