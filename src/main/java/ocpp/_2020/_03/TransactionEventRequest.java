
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
import org.joda.time.DateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "eventType",
    "meterValue",
    "timestamp",
    "triggerReason",
    "seqNo",
    "offline",
    "numberOfPhasesUsed",
    "cableMaxCurrent",
    "reservationId",
    "transactionInfo",
    "evse",
    "idToken"
})
public class TransactionEventRequest implements RequestType
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
     * This contains the type of this event.
     * The first TransactionEvent of a transaction SHALL contain: "Started" The last TransactionEvent of a transaction SHALL contain: "Ended" All others SHALL contain: "Updated"
     * 
     * (Required)
     * 
     */
    @JsonProperty("eventType")
    @JsonPropertyDescription("This contains the type of this event.\r\nThe first TransactionEvent of a transaction SHALL contain: \"Started\" The last TransactionEvent of a transaction SHALL contain: \"Ended\" All others SHALL contain: \"Updated\"\r\n")
    @NotNull
    private TransactionEventEnum eventType;
    @JsonProperty("meterValue")
    @Size(min = 1)
    @Valid
    private List<MeterValue> meterValue = new ArrayList<MeterValue>();
    /**
     * The date and time at which this transaction event occurred.
     * 
     * (Required)
     * 
     */
    @JsonProperty("timestamp")
    @JsonPropertyDescription("The date and time at which this transaction event occurred.\r\n")
    @NotNull
    private DateTime timestamp;
    /**
     * Reason the Charging Station sends this message to the CSMS
     * 
     * (Required)
     * 
     */
    @JsonProperty("triggerReason")
    @JsonPropertyDescription("Reason the Charging Station sends this message to the CSMS\r\n")
    @NotNull
    private TriggerReasonEnum triggerReason;
    /**
     * Incremental sequence number, helps with determining if all messages of a transaction have been received.
     * 
     * (Required)
     * 
     */
    @JsonProperty("seqNo")
    @JsonPropertyDescription("Incremental sequence number, helps with determining if all messages of a transaction have been received.\r\n")
    @NotNull
    private Integer seqNo;
    /**
     * Indication that this transaction event happened when the Charging Station was offline. Default = false, meaning: the event occurred when the Charging Station was online.
     * 
     * 
     */
    @JsonProperty("offline")
    @JsonPropertyDescription("Indication that this transaction event happened when the Charging Station was offline. Default = false, meaning: the event occurred when the Charging Station was online.\r\n")
    private Boolean offline = false;
    /**
     * If the Charging Station is able to report the number of phases used, then it SHALL provide it. When omitted the CSMS may be able to determine the number of phases used via device management.
     * 
     * 
     */
    @JsonProperty("numberOfPhasesUsed")
    @JsonPropertyDescription("If the Charging Station is able to report the number of phases used, then it SHALL provide it. When omitted the CSMS may be able to determine the number of phases used via device management.\r\n")
    private Integer numberOfPhasesUsed;
    /**
     * The maximum current of the connected cable in Ampere (A).
     * 
     * 
     */
    @JsonProperty("cableMaxCurrent")
    @JsonPropertyDescription("The maximum current of the connected cable in Ampere (A).\r\n")
    private Integer cableMaxCurrent;
    /**
     * This contains the Id of the reservation that terminates as a result of this transaction.
     * 
     * 
     */
    @JsonProperty("reservationId")
    @JsonPropertyDescription("This contains the Id of the reservation that terminates as a result of this transaction.\r\n")
    private Integer reservationId;
    /**
     * Transaction
     * urn:x-oca:ocpp:uid:2:233318
     * 
     * (Required)
     * 
     */
    @JsonProperty("transactionInfo")
    @JsonPropertyDescription("Transaction\r\nurn:x-oca:ocpp:uid:2:233318\r\n")
    @Valid
    @NotNull
    private Transaction transactionInfo;
    /**
     * EVSE
     * urn:x-oca:ocpp:uid:2:233123
     * Electric Vehicle Supply Equipment
     * 
     * 
     */
    @JsonProperty("evse")
    @JsonPropertyDescription("EVSE\r\nurn:x-oca:ocpp:uid:2:233123\r\nElectric Vehicle Supply Equipment\r\n")
    @Valid
    private EVSE evse;
    /**
     * Contains a case insensitive identifier to use for the authorization and the type of authorization to support multiple forms of identifiers.
     * 
     * 
     */
    @JsonProperty("idToken")
    @JsonPropertyDescription("Contains a case insensitive identifier to use for the authorization and the type of authorization to support multiple forms of identifiers.\r\n")
    @Valid
    private IdToken idToken;

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

    public TransactionEventRequest withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * This contains the type of this event.
     * The first TransactionEvent of a transaction SHALL contain: "Started" The last TransactionEvent of a transaction SHALL contain: "Ended" All others SHALL contain: "Updated"
     * 
     * (Required)
     * 
     */
    @JsonProperty("eventType")
    public TransactionEventEnum getEventType() {
        return eventType;
    }

    /**
     * This contains the type of this event.
     * The first TransactionEvent of a transaction SHALL contain: "Started" The last TransactionEvent of a transaction SHALL contain: "Ended" All others SHALL contain: "Updated"
     * 
     * (Required)
     * 
     */
    @JsonProperty("eventType")
    public void setEventType(TransactionEventEnum eventType) {
        this.eventType = eventType;
    }

    public TransactionEventRequest withEventType(TransactionEventEnum eventType) {
        this.eventType = eventType;
        return this;
    }

    @JsonProperty("meterValue")
    public List<MeterValue> getMeterValue() {
        return meterValue;
    }

    @JsonProperty("meterValue")
    public void setMeterValue(List<MeterValue> meterValue) {
        this.meterValue = meterValue;
    }

    public TransactionEventRequest withMeterValue(List<MeterValue> meterValue) {
        this.meterValue = meterValue;
        return this;
    }

    /**
     * The date and time at which this transaction event occurred.
     * 
     * (Required)
     * 
     */
    @JsonProperty("timestamp")
    public DateTime getTimestamp() {
        return timestamp;
    }

    /**
     * The date and time at which this transaction event occurred.
     * 
     * (Required)
     * 
     */
    @JsonProperty("timestamp")
    public void setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
    }

    public TransactionEventRequest withTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    /**
     * Reason the Charging Station sends this message to the CSMS
     * 
     * (Required)
     * 
     */
    @JsonProperty("triggerReason")
    public TriggerReasonEnum getTriggerReason() {
        return triggerReason;
    }

    /**
     * Reason the Charging Station sends this message to the CSMS
     * 
     * (Required)
     * 
     */
    @JsonProperty("triggerReason")
    public void setTriggerReason(TriggerReasonEnum triggerReason) {
        this.triggerReason = triggerReason;
    }

    public TransactionEventRequest withTriggerReason(TriggerReasonEnum triggerReason) {
        this.triggerReason = triggerReason;
        return this;
    }

    /**
     * Incremental sequence number, helps with determining if all messages of a transaction have been received.
     * 
     * (Required)
     * 
     */
    @JsonProperty("seqNo")
    public Integer getSeqNo() {
        return seqNo;
    }

    /**
     * Incremental sequence number, helps with determining if all messages of a transaction have been received.
     * 
     * (Required)
     * 
     */
    @JsonProperty("seqNo")
    public void setSeqNo(Integer seqNo) {
        this.seqNo = seqNo;
    }

    public TransactionEventRequest withSeqNo(Integer seqNo) {
        this.seqNo = seqNo;
        return this;
    }

    /**
     * Indication that this transaction event happened when the Charging Station was offline. Default = false, meaning: the event occurred when the Charging Station was online.
     * 
     * 
     */
    @JsonProperty("offline")
    public Boolean getOffline() {
        return offline;
    }

    /**
     * Indication that this transaction event happened when the Charging Station was offline. Default = false, meaning: the event occurred when the Charging Station was online.
     * 
     * 
     */
    @JsonProperty("offline")
    public void setOffline(Boolean offline) {
        this.offline = offline;
    }

    public TransactionEventRequest withOffline(Boolean offline) {
        this.offline = offline;
        return this;
    }

    /**
     * If the Charging Station is able to report the number of phases used, then it SHALL provide it. When omitted the CSMS may be able to determine the number of phases used via device management.
     * 
     * 
     */
    @JsonProperty("numberOfPhasesUsed")
    public Integer getNumberOfPhasesUsed() {
        return numberOfPhasesUsed;
    }

    /**
     * If the Charging Station is able to report the number of phases used, then it SHALL provide it. When omitted the CSMS may be able to determine the number of phases used via device management.
     * 
     * 
     */
    @JsonProperty("numberOfPhasesUsed")
    public void setNumberOfPhasesUsed(Integer numberOfPhasesUsed) {
        this.numberOfPhasesUsed = numberOfPhasesUsed;
    }

    public TransactionEventRequest withNumberOfPhasesUsed(Integer numberOfPhasesUsed) {
        this.numberOfPhasesUsed = numberOfPhasesUsed;
        return this;
    }

    /**
     * The maximum current of the connected cable in Ampere (A).
     * 
     * 
     */
    @JsonProperty("cableMaxCurrent")
    public Integer getCableMaxCurrent() {
        return cableMaxCurrent;
    }

    /**
     * The maximum current of the connected cable in Ampere (A).
     * 
     * 
     */
    @JsonProperty("cableMaxCurrent")
    public void setCableMaxCurrent(Integer cableMaxCurrent) {
        this.cableMaxCurrent = cableMaxCurrent;
    }

    public TransactionEventRequest withCableMaxCurrent(Integer cableMaxCurrent) {
        this.cableMaxCurrent = cableMaxCurrent;
        return this;
    }

    /**
     * This contains the Id of the reservation that terminates as a result of this transaction.
     * 
     * 
     */
    @JsonProperty("reservationId")
    public Integer getReservationId() {
        return reservationId;
    }

    /**
     * This contains the Id of the reservation that terminates as a result of this transaction.
     * 
     * 
     */
    @JsonProperty("reservationId")
    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public TransactionEventRequest withReservationId(Integer reservationId) {
        this.reservationId = reservationId;
        return this;
    }

    /**
     * Transaction
     * urn:x-oca:ocpp:uid:2:233318
     * 
     * (Required)
     * 
     */
    @JsonProperty("transactionInfo")
    public Transaction getTransactionInfo() {
        return transactionInfo;
    }

    /**
     * Transaction
     * urn:x-oca:ocpp:uid:2:233318
     * 
     * (Required)
     * 
     */
    @JsonProperty("transactionInfo")
    public void setTransactionInfo(Transaction transactionInfo) {
        this.transactionInfo = transactionInfo;
    }

    public TransactionEventRequest withTransactionInfo(Transaction transactionInfo) {
        this.transactionInfo = transactionInfo;
        return this;
    }

    /**
     * EVSE
     * urn:x-oca:ocpp:uid:2:233123
     * Electric Vehicle Supply Equipment
     * 
     * 
     */
    @JsonProperty("evse")
    public EVSE getEvse() {
        return evse;
    }

    /**
     * EVSE
     * urn:x-oca:ocpp:uid:2:233123
     * Electric Vehicle Supply Equipment
     * 
     * 
     */
    @JsonProperty("evse")
    public void setEvse(EVSE evse) {
        this.evse = evse;
    }

    public TransactionEventRequest withEvse(EVSE evse) {
        this.evse = evse;
        return this;
    }

    /**
     * Contains a case insensitive identifier to use for the authorization and the type of authorization to support multiple forms of identifiers.
     * 
     * 
     */
    @JsonProperty("idToken")
    public IdToken getIdToken() {
        return idToken;
    }

    /**
     * Contains a case insensitive identifier to use for the authorization and the type of authorization to support multiple forms of identifiers.
     * 
     * 
     */
    @JsonProperty("idToken")
    public void setIdToken(IdToken idToken) {
        this.idToken = idToken;
    }

    public TransactionEventRequest withIdToken(IdToken idToken) {
        this.idToken = idToken;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(TransactionEventRequest.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("eventType");
        sb.append('=');
        sb.append(((this.eventType == null)?"<null>":this.eventType));
        sb.append(',');
        sb.append("meterValue");
        sb.append('=');
        sb.append(((this.meterValue == null)?"<null>":this.meterValue));
        sb.append(',');
        sb.append("timestamp");
        sb.append('=');
        sb.append(((this.timestamp == null)?"<null>":this.timestamp));
        sb.append(',');
        sb.append("triggerReason");
        sb.append('=');
        sb.append(((this.triggerReason == null)?"<null>":this.triggerReason));
        sb.append(',');
        sb.append("seqNo");
        sb.append('=');
        sb.append(((this.seqNo == null)?"<null>":this.seqNo));
        sb.append(',');
        sb.append("offline");
        sb.append('=');
        sb.append(((this.offline == null)?"<null>":this.offline));
        sb.append(',');
        sb.append("numberOfPhasesUsed");
        sb.append('=');
        sb.append(((this.numberOfPhasesUsed == null)?"<null>":this.numberOfPhasesUsed));
        sb.append(',');
        sb.append("cableMaxCurrent");
        sb.append('=');
        sb.append(((this.cableMaxCurrent == null)?"<null>":this.cableMaxCurrent));
        sb.append(',');
        sb.append("reservationId");
        sb.append('=');
        sb.append(((this.reservationId == null)?"<null>":this.reservationId));
        sb.append(',');
        sb.append("transactionInfo");
        sb.append('=');
        sb.append(((this.transactionInfo == null)?"<null>":this.transactionInfo));
        sb.append(',');
        sb.append("evse");
        sb.append('=');
        sb.append(((this.evse == null)?"<null>":this.evse));
        sb.append(',');
        sb.append("idToken");
        sb.append('=');
        sb.append(((this.idToken == null)?"<null>":this.idToken));
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
        result = ((result* 31)+((this.seqNo == null)? 0 :this.seqNo.hashCode()));
        result = ((result* 31)+((this.customData == null)? 0 :this.customData.hashCode()));
        result = ((result* 31)+((this.eventType == null)? 0 :this.eventType.hashCode()));
        result = ((result* 31)+((this.evse == null)? 0 :this.evse.hashCode()));
        result = ((result* 31)+((this.transactionInfo == null)? 0 :this.transactionInfo.hashCode()));
        result = ((result* 31)+((this.offline == null)? 0 :this.offline.hashCode()));
        result = ((result* 31)+((this.reservationId == null)? 0 :this.reservationId.hashCode()));
        result = ((result* 31)+((this.triggerReason == null)? 0 :this.triggerReason.hashCode()));
        result = ((result* 31)+((this.idToken == null)? 0 :this.idToken.hashCode()));
        result = ((result* 31)+((this.meterValue == null)? 0 :this.meterValue.hashCode()));
        result = ((result* 31)+((this.cableMaxCurrent == null)? 0 :this.cableMaxCurrent.hashCode()));
        result = ((result* 31)+((this.numberOfPhasesUsed == null)? 0 :this.numberOfPhasesUsed.hashCode()));
        result = ((result* 31)+((this.timestamp == null)? 0 :this.timestamp.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof TransactionEventRequest) == false) {
            return false;
        }
        TransactionEventRequest rhs = ((TransactionEventRequest) other);
        return ((((((((((((((this.seqNo == rhs.seqNo)||((this.seqNo!= null)&&this.seqNo.equals(rhs.seqNo)))&&((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData))))&&((this.eventType == rhs.eventType)||((this.eventType!= null)&&this.eventType.equals(rhs.eventType))))&&((this.evse == rhs.evse)||((this.evse!= null)&&this.evse.equals(rhs.evse))))&&((this.transactionInfo == rhs.transactionInfo)||((this.transactionInfo!= null)&&this.transactionInfo.equals(rhs.transactionInfo))))&&((this.offline == rhs.offline)||((this.offline!= null)&&this.offline.equals(rhs.offline))))&&((this.reservationId == rhs.reservationId)||((this.reservationId!= null)&&this.reservationId.equals(rhs.reservationId))))&&((this.triggerReason == rhs.triggerReason)||((this.triggerReason!= null)&&this.triggerReason.equals(rhs.triggerReason))))&&((this.idToken == rhs.idToken)||((this.idToken!= null)&&this.idToken.equals(rhs.idToken))))&&((this.meterValue == rhs.meterValue)||((this.meterValue!= null)&&this.meterValue.equals(rhs.meterValue))))&&((this.cableMaxCurrent == rhs.cableMaxCurrent)||((this.cableMaxCurrent!= null)&&this.cableMaxCurrent.equals(rhs.cableMaxCurrent))))&&((this.numberOfPhasesUsed == rhs.numberOfPhasesUsed)||((this.numberOfPhasesUsed!= null)&&this.numberOfPhasesUsed.equals(rhs.numberOfPhasesUsed))))&&((this.timestamp == rhs.timestamp)||((this.timestamp!= null)&&this.timestamp.equals(rhs.timestamp))));
    }

}
