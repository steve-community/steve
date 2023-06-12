
package ocpp._2020._03;

import javax.annotation.Generated;
import javax.validation.Valid;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.rwth.idsg.ocpp.jaxb.RequestType;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "chargingProfileId",
    "chargingProfileCriteria"
})
@Generated("jsonschema2pojo")
public class ClearChargingProfileRequest implements RequestType
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
     * The Id of the charging profile to clear.
     * 
     * 
     */
    @JsonProperty("chargingProfileId")
    @JsonPropertyDescription("The Id of the charging profile to clear.\r\n")
    private Integer chargingProfileId;
    /**
     * Charging_ Profile
     * urn:x-oca:ocpp:uid:2:233255
     * A ChargingProfile consists of a ChargingSchedule, describing the amount of power or current that can be delivered per time interval.
     * 
     * 
     */
    @JsonProperty("chargingProfileCriteria")
    @JsonPropertyDescription("Charging_ Profile\r\nurn:x-oca:ocpp:uid:2:233255\r\nA ChargingProfile consists of a ChargingSchedule, describing the amount of power or current that can be delivered per time interval.\r\n")
    @Valid
    private ClearChargingProfile chargingProfileCriteria;

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

    public ClearChargingProfileRequest withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * The Id of the charging profile to clear.
     * 
     * 
     */
    @JsonProperty("chargingProfileId")
    public Integer getChargingProfileId() {
        return chargingProfileId;
    }

    /**
     * The Id of the charging profile to clear.
     * 
     * 
     */
    @JsonProperty("chargingProfileId")
    public void setChargingProfileId(Integer chargingProfileId) {
        this.chargingProfileId = chargingProfileId;
    }

    public ClearChargingProfileRequest withChargingProfileId(Integer chargingProfileId) {
        this.chargingProfileId = chargingProfileId;
        return this;
    }

    /**
     * Charging_ Profile
     * urn:x-oca:ocpp:uid:2:233255
     * A ChargingProfile consists of a ChargingSchedule, describing the amount of power or current that can be delivered per time interval.
     * 
     * 
     */
    @JsonProperty("chargingProfileCriteria")
    public ClearChargingProfile getChargingProfileCriteria() {
        return chargingProfileCriteria;
    }

    /**
     * Charging_ Profile
     * urn:x-oca:ocpp:uid:2:233255
     * A ChargingProfile consists of a ChargingSchedule, describing the amount of power or current that can be delivered per time interval.
     * 
     * 
     */
    @JsonProperty("chargingProfileCriteria")
    public void setChargingProfileCriteria(ClearChargingProfile chargingProfileCriteria) {
        this.chargingProfileCriteria = chargingProfileCriteria;
    }

    public ClearChargingProfileRequest withChargingProfileCriteria(ClearChargingProfile chargingProfileCriteria) {
        this.chargingProfileCriteria = chargingProfileCriteria;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ClearChargingProfileRequest.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("chargingProfileId");
        sb.append('=');
        sb.append(((this.chargingProfileId == null)?"<null>":this.chargingProfileId));
        sb.append(',');
        sb.append("chargingProfileCriteria");
        sb.append('=');
        sb.append(((this.chargingProfileCriteria == null)?"<null>":this.chargingProfileCriteria));
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
        result = ((result* 31)+((this.chargingProfileCriteria == null)? 0 :this.chargingProfileCriteria.hashCode()));
        result = ((result* 31)+((this.chargingProfileId == null)? 0 :this.chargingProfileId.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ClearChargingProfileRequest) == false) {
            return false;
        }
        ClearChargingProfileRequest rhs = ((ClearChargingProfileRequest) other);
        return ((((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData)))&&((this.chargingProfileCriteria == rhs.chargingProfileCriteria)||((this.chargingProfileCriteria!= null)&&this.chargingProfileCriteria.equals(rhs.chargingProfileCriteria))))&&((this.chargingProfileId == rhs.chargingProfileId)||((this.chargingProfileId!= null)&&this.chargingProfileId.equals(rhs.chargingProfileId))));
    }

}
