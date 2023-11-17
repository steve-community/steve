
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
    "reservationId",
    "reservationUpdateStatus"
})
@Generated("jsonschema2pojo")
public class ReservationStatusUpdateRequest implements RequestType
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
     * The ID of the reservation.
     * 
     * (Required)
     * 
     */
    @JsonProperty("reservationId")
    @JsonPropertyDescription("The ID of the reservation.\r\n")
    @NotNull
    private Integer reservationId;
    /**
     * The updated reservation status.
     * 
     * (Required)
     * 
     */
    @JsonProperty("reservationUpdateStatus")
    @JsonPropertyDescription("The updated reservation status.\r\n")
    @NotNull
    private ReservationUpdateStatusEnum reservationUpdateStatus;

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

    public ReservationStatusUpdateRequest withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * The ID of the reservation.
     * 
     * (Required)
     * 
     */
    @JsonProperty("reservationId")
    public Integer getReservationId() {
        return reservationId;
    }

    /**
     * The ID of the reservation.
     * 
     * (Required)
     * 
     */
    @JsonProperty("reservationId")
    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public ReservationStatusUpdateRequest withReservationId(Integer reservationId) {
        this.reservationId = reservationId;
        return this;
    }

    /**
     * The updated reservation status.
     * 
     * (Required)
     * 
     */
    @JsonProperty("reservationUpdateStatus")
    public ReservationUpdateStatusEnum getReservationUpdateStatus() {
        return reservationUpdateStatus;
    }

    /**
     * The updated reservation status.
     * 
     * (Required)
     * 
     */
    @JsonProperty("reservationUpdateStatus")
    public void setReservationUpdateStatus(ReservationUpdateStatusEnum reservationUpdateStatus) {
        this.reservationUpdateStatus = reservationUpdateStatus;
    }

    public ReservationStatusUpdateRequest withReservationUpdateStatus(ReservationUpdateStatusEnum reservationUpdateStatus) {
        this.reservationUpdateStatus = reservationUpdateStatus;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ReservationStatusUpdateRequest.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("reservationId");
        sb.append('=');
        sb.append(((this.reservationId == null)?"<null>":this.reservationId));
        sb.append(',');
        sb.append("reservationUpdateStatus");
        sb.append('=');
        sb.append(((this.reservationUpdateStatus == null)?"<null>":this.reservationUpdateStatus));
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
        result = ((result* 31)+((this.reservationId == null)? 0 :this.reservationId.hashCode()));
        result = ((result* 31)+((this.reservationUpdateStatus == null)? 0 :this.reservationUpdateStatus.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ReservationStatusUpdateRequest) == false) {
            return false;
        }
        ReservationStatusUpdateRequest rhs = ((ReservationStatusUpdateRequest) other);
        return ((((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData)))&&((this.reservationId == rhs.reservationId)||((this.reservationId!= null)&&this.reservationId.equals(rhs.reservationId))))&&((this.reservationUpdateStatus == rhs.reservationUpdateStatus)||((this.reservationUpdateStatus!= null)&&this.reservationUpdateStatus.equals(rhs.reservationUpdateStatus))));
    }

}
