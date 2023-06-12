
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
 * Transaction
 * urn:x-oca:ocpp:uid:2:233318
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "transactionId",
    "chargingState",
    "timeSpentCharging",
    "stoppedReason",
    "remoteStartId"
})
@Generated("jsonschema2pojo")
public class Transaction {

    /**
     * This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.
     * 
     */
    @JsonProperty("customData")
    @JsonPropertyDescription("This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.")
    @Valid
    private CustomData customData;
    /**
     * This contains the Id of the transaction.
     * 
     * (Required)
     * 
     */
    @JsonProperty("transactionId")
    @JsonPropertyDescription("This contains the Id of the transaction.\r\n")
    @Size(max = 36)
    @NotNull
    private String transactionId;
    /**
     * Transaction. State. Transaction_ State_ Code
     * urn:x-oca:ocpp:uid:1:569419
     * Current charging state, is required when state
     * has changed.
     * 
     * 
     */
    @JsonProperty("chargingState")
    @JsonPropertyDescription("Transaction. State. Transaction_ State_ Code\r\nurn:x-oca:ocpp:uid:1:569419\r\nCurrent charging state, is required when state\r\nhas changed.\r\n")
    private ChargingStateEnum chargingState;
    /**
     * Transaction. Time_ Spent_ Charging. Elapsed_ Time
     * urn:x-oca:ocpp:uid:1:569415
     * Contains the total time that energy flowed from EVSE to EV during the transaction (in seconds). Note that timeSpentCharging is smaller or equal to the duration of the transaction.
     * 
     * 
     */
    @JsonProperty("timeSpentCharging")
    @JsonPropertyDescription("Transaction. Time_ Spent_ Charging. Elapsed_ Time\r\nurn:x-oca:ocpp:uid:1:569415\r\nContains the total time that energy flowed from EVSE to EV during the transaction (in seconds). Note that timeSpentCharging is smaller or equal to the duration of the transaction.\r\n")
    private Integer timeSpentCharging;
    /**
     * Transaction. Stopped_ Reason. EOT_ Reason_ Code
     * urn:x-oca:ocpp:uid:1:569413
     * This contains the reason why the transaction was stopped. MAY only be omitted when Reason is "Local".
     * 
     * 
     */
    @JsonProperty("stoppedReason")
    @JsonPropertyDescription("Transaction. Stopped_ Reason. EOT_ Reason_ Code\r\nurn:x-oca:ocpp:uid:1:569413\r\nThis contains the reason why the transaction was stopped. MAY only be omitted when Reason is \"Local\".\r\n")
    private ReasonEnum stoppedReason;
    /**
     * The ID given to remote start request (&lt;&lt;requeststarttransactionrequest, RequestStartTransactionRequest&gt;&gt;. This enables to CSMS to match the started transaction to the given start request.
     * 
     * 
     */
    @JsonProperty("remoteStartId")
    @JsonPropertyDescription("The ID given to remote start request (&lt;&lt;requeststarttransactionrequest, RequestStartTransactionRequest&gt;&gt;. This enables to CSMS to match the started transaction to the given start request.\r\n")
    private Integer remoteStartId;

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

    public Transaction withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * This contains the Id of the transaction.
     * 
     * (Required)
     * 
     */
    @JsonProperty("transactionId")
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * This contains the Id of the transaction.
     * 
     * (Required)
     * 
     */
    @JsonProperty("transactionId")
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Transaction withTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    /**
     * Transaction. State. Transaction_ State_ Code
     * urn:x-oca:ocpp:uid:1:569419
     * Current charging state, is required when state
     * has changed.
     * 
     * 
     */
    @JsonProperty("chargingState")
    public ChargingStateEnum getChargingState() {
        return chargingState;
    }

    /**
     * Transaction. State. Transaction_ State_ Code
     * urn:x-oca:ocpp:uid:1:569419
     * Current charging state, is required when state
     * has changed.
     * 
     * 
     */
    @JsonProperty("chargingState")
    public void setChargingState(ChargingStateEnum chargingState) {
        this.chargingState = chargingState;
    }

    public Transaction withChargingState(ChargingStateEnum chargingState) {
        this.chargingState = chargingState;
        return this;
    }

    /**
     * Transaction. Time_ Spent_ Charging. Elapsed_ Time
     * urn:x-oca:ocpp:uid:1:569415
     * Contains the total time that energy flowed from EVSE to EV during the transaction (in seconds). Note that timeSpentCharging is smaller or equal to the duration of the transaction.
     * 
     * 
     */
    @JsonProperty("timeSpentCharging")
    public Integer getTimeSpentCharging() {
        return timeSpentCharging;
    }

    /**
     * Transaction. Time_ Spent_ Charging. Elapsed_ Time
     * urn:x-oca:ocpp:uid:1:569415
     * Contains the total time that energy flowed from EVSE to EV during the transaction (in seconds). Note that timeSpentCharging is smaller or equal to the duration of the transaction.
     * 
     * 
     */
    @JsonProperty("timeSpentCharging")
    public void setTimeSpentCharging(Integer timeSpentCharging) {
        this.timeSpentCharging = timeSpentCharging;
    }

    public Transaction withTimeSpentCharging(Integer timeSpentCharging) {
        this.timeSpentCharging = timeSpentCharging;
        return this;
    }

    /**
     * Transaction. Stopped_ Reason. EOT_ Reason_ Code
     * urn:x-oca:ocpp:uid:1:569413
     * This contains the reason why the transaction was stopped. MAY only be omitted when Reason is "Local".
     * 
     * 
     */
    @JsonProperty("stoppedReason")
    public ReasonEnum getStoppedReason() {
        return stoppedReason;
    }

    /**
     * Transaction. Stopped_ Reason. EOT_ Reason_ Code
     * urn:x-oca:ocpp:uid:1:569413
     * This contains the reason why the transaction was stopped. MAY only be omitted when Reason is "Local".
     * 
     * 
     */
    @JsonProperty("stoppedReason")
    public void setStoppedReason(ReasonEnum stoppedReason) {
        this.stoppedReason = stoppedReason;
    }

    public Transaction withStoppedReason(ReasonEnum stoppedReason) {
        this.stoppedReason = stoppedReason;
        return this;
    }

    /**
     * The ID given to remote start request (&lt;&lt;requeststarttransactionrequest, RequestStartTransactionRequest&gt;&gt;. This enables to CSMS to match the started transaction to the given start request.
     * 
     * 
     */
    @JsonProperty("remoteStartId")
    public Integer getRemoteStartId() {
        return remoteStartId;
    }

    /**
     * The ID given to remote start request (&lt;&lt;requeststarttransactionrequest, RequestStartTransactionRequest&gt;&gt;. This enables to CSMS to match the started transaction to the given start request.
     * 
     * 
     */
    @JsonProperty("remoteStartId")
    public void setRemoteStartId(Integer remoteStartId) {
        this.remoteStartId = remoteStartId;
    }

    public Transaction withRemoteStartId(Integer remoteStartId) {
        this.remoteStartId = remoteStartId;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Transaction.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("transactionId");
        sb.append('=');
        sb.append(((this.transactionId == null)?"<null>":this.transactionId));
        sb.append(',');
        sb.append("chargingState");
        sb.append('=');
        sb.append(((this.chargingState == null)?"<null>":this.chargingState));
        sb.append(',');
        sb.append("timeSpentCharging");
        sb.append('=');
        sb.append(((this.timeSpentCharging == null)?"<null>":this.timeSpentCharging));
        sb.append(',');
        sb.append("stoppedReason");
        sb.append('=');
        sb.append(((this.stoppedReason == null)?"<null>":this.stoppedReason));
        sb.append(',');
        sb.append("remoteStartId");
        sb.append('=');
        sb.append(((this.remoteStartId == null)?"<null>":this.remoteStartId));
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
        result = ((result* 31)+((this.remoteStartId == null)? 0 :this.remoteStartId.hashCode()));
        result = ((result* 31)+((this.stoppedReason == null)? 0 :this.stoppedReason.hashCode()));
        result = ((result* 31)+((this.customData == null)? 0 :this.customData.hashCode()));
        result = ((result* 31)+((this.transactionId == null)? 0 :this.transactionId.hashCode()));
        result = ((result* 31)+((this.timeSpentCharging == null)? 0 :this.timeSpentCharging.hashCode()));
        result = ((result* 31)+((this.chargingState == null)? 0 :this.chargingState.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Transaction) == false) {
            return false;
        }
        Transaction rhs = ((Transaction) other);
        return (((((((this.remoteStartId == rhs.remoteStartId)||((this.remoteStartId!= null)&&this.remoteStartId.equals(rhs.remoteStartId)))&&((this.stoppedReason == rhs.stoppedReason)||((this.stoppedReason!= null)&&this.stoppedReason.equals(rhs.stoppedReason))))&&((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData))))&&((this.transactionId == rhs.transactionId)||((this.transactionId!= null)&&this.transactionId.equals(rhs.transactionId))))&&((this.timeSpentCharging == rhs.timeSpentCharging)||((this.timeSpentCharging!= null)&&this.timeSpentCharging.equals(rhs.timeSpentCharging))))&&((this.chargingState == rhs.chargingState)||((this.chargingState!= null)&&this.chargingState.equals(rhs.chargingState))));
    }

}
