
package ocpp._2020._03;

import javax.annotation.Generated;
import javax.validation.Valid;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Charging_ Profile
 * urn:x-oca:ocpp:uid:2:233255
 * A ChargingProfile consists of a ChargingSchedule, describing the amount of power or current that can be delivered per time interval.
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "evseId",
    "chargingProfilePurpose",
    "stackLevel"
})
@Generated("jsonschema2pojo")
public class ClearChargingProfile {

    /**
     * This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.
     * 
     */
    @JsonProperty("customData")
    @JsonPropertyDescription("This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.")
    @Valid
    private CustomData customData;
    /**
     * Identified_ Object. MRID. Numeric_ Identifier
     * urn:x-enexis:ecdm:uid:1:569198
     * Specifies the id of the EVSE for which to clear charging profiles. An evseId of zero (0) specifies the charging profile for the overall Charging Station. Absence of this parameter means the clearing applies to all charging profiles that match the other criteria in the request.
     * 
     * 
     * 
     */
    @JsonProperty("evseId")
    @JsonPropertyDescription("Identified_ Object. MRID. Numeric_ Identifier\r\nurn:x-enexis:ecdm:uid:1:569198\r\nSpecifies the id of the EVSE for which to clear charging profiles. An evseId of zero (0) specifies the charging profile for the overall Charging Station. Absence of this parameter means the clearing applies to all charging profiles that match the other criteria in the request.\r\n\r\n")
    private Integer evseId;
    /**
     * Charging_ Profile. Charging_ Profile_ Purpose. Charging_ Profile_ Purpose_ Code
     * urn:x-oca:ocpp:uid:1:569231
     * Specifies to purpose of the charging profiles that will be cleared, if they meet the other criteria in the request.
     * 
     * 
     */
    @JsonProperty("chargingProfilePurpose")
    @JsonPropertyDescription("Charging_ Profile. Charging_ Profile_ Purpose. Charging_ Profile_ Purpose_ Code\r\nurn:x-oca:ocpp:uid:1:569231\r\nSpecifies to purpose of the charging profiles that will be cleared, if they meet the other criteria in the request.\r\n")
    private ChargingProfilePurposeEnum chargingProfilePurpose;
    /**
     * Charging_ Profile. Stack_ Level. Counter
     * urn:x-oca:ocpp:uid:1:569230
     * Specifies the stackLevel for which charging profiles will be cleared, if they meet the other criteria in the request.
     * 
     * 
     */
    @JsonProperty("stackLevel")
    @JsonPropertyDescription("Charging_ Profile. Stack_ Level. Counter\r\nurn:x-oca:ocpp:uid:1:569230\r\nSpecifies the stackLevel for which charging profiles will be cleared, if they meet the other criteria in the request.\r\n")
    private Integer stackLevel;

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

    public ClearChargingProfile withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * Identified_ Object. MRID. Numeric_ Identifier
     * urn:x-enexis:ecdm:uid:1:569198
     * Specifies the id of the EVSE for which to clear charging profiles. An evseId of zero (0) specifies the charging profile for the overall Charging Station. Absence of this parameter means the clearing applies to all charging profiles that match the other criteria in the request.
     * 
     * 
     * 
     */
    @JsonProperty("evseId")
    public Integer getEvseId() {
        return evseId;
    }

    /**
     * Identified_ Object. MRID. Numeric_ Identifier
     * urn:x-enexis:ecdm:uid:1:569198
     * Specifies the id of the EVSE for which to clear charging profiles. An evseId of zero (0) specifies the charging profile for the overall Charging Station. Absence of this parameter means the clearing applies to all charging profiles that match the other criteria in the request.
     * 
     * 
     * 
     */
    @JsonProperty("evseId")
    public void setEvseId(Integer evseId) {
        this.evseId = evseId;
    }

    public ClearChargingProfile withEvseId(Integer evseId) {
        this.evseId = evseId;
        return this;
    }

    /**
     * Charging_ Profile. Charging_ Profile_ Purpose. Charging_ Profile_ Purpose_ Code
     * urn:x-oca:ocpp:uid:1:569231
     * Specifies to purpose of the charging profiles that will be cleared, if they meet the other criteria in the request.
     * 
     * 
     */
    @JsonProperty("chargingProfilePurpose")
    public ChargingProfilePurposeEnum getChargingProfilePurpose() {
        return chargingProfilePurpose;
    }

    /**
     * Charging_ Profile. Charging_ Profile_ Purpose. Charging_ Profile_ Purpose_ Code
     * urn:x-oca:ocpp:uid:1:569231
     * Specifies to purpose of the charging profiles that will be cleared, if they meet the other criteria in the request.
     * 
     * 
     */
    @JsonProperty("chargingProfilePurpose")
    public void setChargingProfilePurpose(ChargingProfilePurposeEnum chargingProfilePurpose) {
        this.chargingProfilePurpose = chargingProfilePurpose;
    }

    public ClearChargingProfile withChargingProfilePurpose(ChargingProfilePurposeEnum chargingProfilePurpose) {
        this.chargingProfilePurpose = chargingProfilePurpose;
        return this;
    }

    /**
     * Charging_ Profile. Stack_ Level. Counter
     * urn:x-oca:ocpp:uid:1:569230
     * Specifies the stackLevel for which charging profiles will be cleared, if they meet the other criteria in the request.
     * 
     * 
     */
    @JsonProperty("stackLevel")
    public Integer getStackLevel() {
        return stackLevel;
    }

    /**
     * Charging_ Profile. Stack_ Level. Counter
     * urn:x-oca:ocpp:uid:1:569230
     * Specifies the stackLevel for which charging profiles will be cleared, if they meet the other criteria in the request.
     * 
     * 
     */
    @JsonProperty("stackLevel")
    public void setStackLevel(Integer stackLevel) {
        this.stackLevel = stackLevel;
    }

    public ClearChargingProfile withStackLevel(Integer stackLevel) {
        this.stackLevel = stackLevel;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ClearChargingProfile.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("evseId");
        sb.append('=');
        sb.append(((this.evseId == null)?"<null>":this.evseId));
        sb.append(',');
        sb.append("chargingProfilePurpose");
        sb.append('=');
        sb.append(((this.chargingProfilePurpose == null)?"<null>":this.chargingProfilePurpose));
        sb.append(',');
        sb.append("stackLevel");
        sb.append('=');
        sb.append(((this.stackLevel == null)?"<null>":this.stackLevel));
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
        result = ((result* 31)+((this.chargingProfilePurpose == null)? 0 :this.chargingProfilePurpose.hashCode()));
        result = ((result* 31)+((this.customData == null)? 0 :this.customData.hashCode()));
        result = ((result* 31)+((this.stackLevel == null)? 0 :this.stackLevel.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ClearChargingProfile) == false) {
            return false;
        }
        ClearChargingProfile rhs = ((ClearChargingProfile) other);
        return (((((this.evseId == rhs.evseId)||((this.evseId!= null)&&this.evseId.equals(rhs.evseId)))&&((this.chargingProfilePurpose == rhs.chargingProfilePurpose)||((this.chargingProfilePurpose!= null)&&this.chargingProfilePurpose.equals(rhs.chargingProfilePurpose))))&&((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData))))&&((this.stackLevel == rhs.stackLevel)||((this.stackLevel!= null)&&this.stackLevel.equals(rhs.stackLevel))));
    }

}
