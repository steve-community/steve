
package ocpp._2020._03;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.rwth.idsg.ocpp.jaxb.RequestType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "chargingStation",
    "reason"
})
public class BootNotificationRequest implements RequestType
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
     * Charge_ Point
     * urn:x-oca:ocpp:uid:2:233122
     * The physical system where an Electrical Vehicle (EV) can be charged.
     * 
     * (Required)
     * 
     */
    @JsonProperty("chargingStation")
    @JsonPropertyDescription("Charge_ Point\r\nurn:x-oca:ocpp:uid:2:233122\r\nThe physical system where an Electrical Vehicle (EV) can be charged.\r\n")
    @Valid
    @NotNull
    private ChargingStation chargingStation;
    /**
     * This contains the reason for sending this message to the CSMS.
     * 
     * (Required)
     * 
     */
    @JsonProperty("reason")
    @JsonPropertyDescription("This contains the reason for sending this message to the CSMS.\r\n")
    @NotNull
    private BootReasonEnum reason;

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

    public BootNotificationRequest withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * Charge_ Point
     * urn:x-oca:ocpp:uid:2:233122
     * The physical system where an Electrical Vehicle (EV) can be charged.
     * 
     * (Required)
     * 
     */
    @JsonProperty("chargingStation")
    public ChargingStation getChargingStation() {
        return chargingStation;
    }

    /**
     * Charge_ Point
     * urn:x-oca:ocpp:uid:2:233122
     * The physical system where an Electrical Vehicle (EV) can be charged.
     * 
     * (Required)
     * 
     */
    @JsonProperty("chargingStation")
    public void setChargingStation(ChargingStation chargingStation) {
        this.chargingStation = chargingStation;
    }

    public BootNotificationRequest withChargingStation(ChargingStation chargingStation) {
        this.chargingStation = chargingStation;
        return this;
    }

    /**
     * This contains the reason for sending this message to the CSMS.
     * 
     * (Required)
     * 
     */
    @JsonProperty("reason")
    public BootReasonEnum getReason() {
        return reason;
    }

    /**
     * This contains the reason for sending this message to the CSMS.
     * 
     * (Required)
     * 
     */
    @JsonProperty("reason")
    public void setReason(BootReasonEnum reason) {
        this.reason = reason;
    }

    public BootNotificationRequest withReason(BootReasonEnum reason) {
        this.reason = reason;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(BootNotificationRequest.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("chargingStation");
        sb.append('=');
        sb.append(((this.chargingStation == null)?"<null>":this.chargingStation));
        sb.append(',');
        sb.append("reason");
        sb.append('=');
        sb.append(((this.reason == null)?"<null>":this.reason));
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
        result = ((result* 31)+((this.reason == null)? 0 :this.reason.hashCode()));
        result = ((result* 31)+((this.customData == null)? 0 :this.customData.hashCode()));
        result = ((result* 31)+((this.chargingStation == null)? 0 :this.chargingStation.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof BootNotificationRequest) == false) {
            return false;
        }
        BootNotificationRequest rhs = ((BootNotificationRequest) other);
        return ((((this.reason == rhs.reason)||((this.reason!= null)&&this.reason.equals(rhs.reason)))&&((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData))))&&((this.chargingStation == rhs.chargingStation)||((this.chargingStation!= null)&&this.chargingStation.equals(rhs.chargingStation))));
    }

}
