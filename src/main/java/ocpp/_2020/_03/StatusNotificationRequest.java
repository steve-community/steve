
package ocpp._2020._03;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.rwth.idsg.ocpp.jaxb.RequestType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.joda.time.DateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "timestamp",
    "connectorStatus",
    "evseId",
    "connectorId"
})
public class StatusNotificationRequest implements RequestType
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
     * The time for which the status is reported. If absent time of receipt of the message will be assumed.
     * 
     * (Required)
     * 
     */
    @JsonProperty("timestamp")
    @JsonPropertyDescription("The time for which the status is reported. If absent time of receipt of the message will be assumed.\r\n")
    @NotNull
    private DateTime timestamp;
    /**
     * This contains the current status of the Connector.
     * 
     * (Required)
     * 
     */
    @JsonProperty("connectorStatus")
    @JsonPropertyDescription("This contains the current status of the Connector.\r\n")
    @NotNull
    private ConnectorStatusEnum connectorStatus;
    /**
     * The id of the EVSE to which the connector belongs for which the the status is reported.
     * 
     * (Required)
     * 
     */
    @JsonProperty("evseId")
    @JsonPropertyDescription("The id of the EVSE to which the connector belongs for which the the status is reported.\r\n")
    @NotNull
    private Integer evseId;
    /**
     * The id of the connector within the EVSE for which the status is reported.
     * 
     * (Required)
     * 
     */
    @JsonProperty("connectorId")
    @JsonPropertyDescription("The id of the connector within the EVSE for which the status is reported.\r\n")
    @NotNull
    private Integer connectorId;

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

    public StatusNotificationRequest withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * The time for which the status is reported. If absent time of receipt of the message will be assumed.
     * 
     * (Required)
     * 
     */
    @JsonProperty("timestamp")
    public DateTime getTimestamp() {
        return timestamp;
    }

    /**
     * The time for which the status is reported. If absent time of receipt of the message will be assumed.
     * 
     * (Required)
     * 
     */
    @JsonProperty("timestamp")
    public void setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
    }

    public StatusNotificationRequest withTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    /**
     * This contains the current status of the Connector.
     * 
     * (Required)
     * 
     */
    @JsonProperty("connectorStatus")
    public ConnectorStatusEnum getConnectorStatus() {
        return connectorStatus;
    }

    /**
     * This contains the current status of the Connector.
     * 
     * (Required)
     * 
     */
    @JsonProperty("connectorStatus")
    public void setConnectorStatus(ConnectorStatusEnum connectorStatus) {
        this.connectorStatus = connectorStatus;
    }

    public StatusNotificationRequest withConnectorStatus(ConnectorStatusEnum connectorStatus) {
        this.connectorStatus = connectorStatus;
        return this;
    }

    /**
     * The id of the EVSE to which the connector belongs for which the the status is reported.
     * 
     * (Required)
     * 
     */
    @JsonProperty("evseId")
    public Integer getEvseId() {
        return evseId;
    }

    /**
     * The id of the EVSE to which the connector belongs for which the the status is reported.
     * 
     * (Required)
     * 
     */
    @JsonProperty("evseId")
    public void setEvseId(Integer evseId) {
        this.evseId = evseId;
    }

    public StatusNotificationRequest withEvseId(Integer evseId) {
        this.evseId = evseId;
        return this;
    }

    /**
     * The id of the connector within the EVSE for which the status is reported.
     * 
     * (Required)
     * 
     */
    @JsonProperty("connectorId")
    public Integer getConnectorId() {
        return connectorId;
    }

    /**
     * The id of the connector within the EVSE for which the status is reported.
     * 
     * (Required)
     * 
     */
    @JsonProperty("connectorId")
    public void setConnectorId(Integer connectorId) {
        this.connectorId = connectorId;
    }

    public StatusNotificationRequest withConnectorId(Integer connectorId) {
        this.connectorId = connectorId;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(StatusNotificationRequest.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("timestamp");
        sb.append('=');
        sb.append(((this.timestamp == null)?"<null>":this.timestamp));
        sb.append(',');
        sb.append("connectorStatus");
        sb.append('=');
        sb.append(((this.connectorStatus == null)?"<null>":this.connectorStatus));
        sb.append(',');
        sb.append("evseId");
        sb.append('=');
        sb.append(((this.evseId == null)?"<null>":this.evseId));
        sb.append(',');
        sb.append("connectorId");
        sb.append('=');
        sb.append(((this.connectorId == null)?"<null>":this.connectorId));
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
        result = ((result* 31)+((this.evseId == null)? 0 :this.evseId.hashCode()));
        result = ((result* 31)+((this.customData == null)? 0 :this.customData.hashCode()));
        result = ((result* 31)+((this.connectorStatus == null)? 0 :this.connectorStatus.hashCode()));
        result = ((result* 31)+((this.connectorId == null)? 0 :this.connectorId.hashCode()));
        result = ((result* 31)+((this.timestamp == null)? 0 :this.timestamp.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof StatusNotificationRequest) == false) {
            return false;
        }
        StatusNotificationRequest rhs = ((StatusNotificationRequest) other);
        return ((((((this.evseId == rhs.evseId)||((this.evseId!= null)&&this.evseId.equals(rhs.evseId)))&&((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData))))&&((this.connectorStatus == rhs.connectorStatus)||((this.connectorStatus!= null)&&this.connectorStatus.equals(rhs.connectorStatus))))&&((this.connectorId == rhs.connectorId)||((this.connectorId!= null)&&this.connectorId.equals(rhs.connectorId))))&&((this.timestamp == rhs.timestamp)||((this.timestamp!= null)&&this.timestamp.equals(rhs.timestamp))));
    }

}
