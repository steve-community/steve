
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
    "reservationId"
})
@Generated("jsonschema2pojo")
public class CancelReservationRequest implements RequestType
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
     * Id of the reservation to cancel.
     * 
     * (Required)
     * 
     */
    @JsonProperty("reservationId")
    @JsonPropertyDescription("Id of the reservation to cancel.\r\n")
    @NotNull
    private Integer reservationId;

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

    public CancelReservationRequest withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * Id of the reservation to cancel.
     * 
     * (Required)
     * 
     */
    @JsonProperty("reservationId")
    public Integer getReservationId() {
        return reservationId;
    }

    /**
     * Id of the reservation to cancel.
     * 
     * (Required)
     * 
     */
    @JsonProperty("reservationId")
    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public CancelReservationRequest withReservationId(Integer reservationId) {
        this.reservationId = reservationId;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(CancelReservationRequest.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("reservationId");
        sb.append('=');
        sb.append(((this.reservationId == null)?"<null>":this.reservationId));
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
        result = ((result* 31)+((this.reservationId == null)? 0 :this.reservationId.hashCode()));
        result = ((result* 31)+((this.customData == null)? 0 :this.customData.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof CancelReservationRequest) == false) {
            return false;
        }
        CancelReservationRequest rhs = ((CancelReservationRequest) other);
        return (((this.reservationId == rhs.reservationId)||((this.reservationId!= null)&&this.reservationId.equals(rhs.reservationId)))&&((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData))));
    }

}
