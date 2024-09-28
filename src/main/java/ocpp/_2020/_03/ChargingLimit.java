
package ocpp._2020._03;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;


/**
 * Charging_ Limit
 * urn:x-enexis:ecdm:uid:2:234489
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "chargingLimitSource",
    "isGridCritical"
})
public class ChargingLimit {

    /**
     * This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.
     * 
     */
    @JsonProperty("customData")
    @JsonPropertyDescription("This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.")
    @Valid
    private CustomData customData;
    /**
     * Charging_ Limit. Charging_ Limit_ Source. Charging_ Limit_ Source_ Code
     * urn:x-enexis:ecdm:uid:1:570845
     * Represents the source of the charging limit.
     * 
     * (Required)
     * 
     */
    @JsonProperty("chargingLimitSource")
    @JsonPropertyDescription("Charging_ Limit. Charging_ Limit_ Source. Charging_ Limit_ Source_ Code\r\nurn:x-enexis:ecdm:uid:1:570845\r\nRepresents the source of the charging limit.\r\n")
    @NotNull
    private ChargingLimitSourceEnum chargingLimitSource;
    /**
     * Charging_ Limit. Is_ Grid_ Critical. Indicator
     * urn:x-enexis:ecdm:uid:1:570847
     * Indicates whether the charging limit is critical for the grid.
     * 
     * 
     */
    @JsonProperty("isGridCritical")
    @JsonPropertyDescription("Charging_ Limit. Is_ Grid_ Critical. Indicator\r\nurn:x-enexis:ecdm:uid:1:570847\r\nIndicates whether the charging limit is critical for the grid.\r\n")
    private Boolean isGridCritical;

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

    public ChargingLimit withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * Charging_ Limit. Charging_ Limit_ Source. Charging_ Limit_ Source_ Code
     * urn:x-enexis:ecdm:uid:1:570845
     * Represents the source of the charging limit.
     * 
     * (Required)
     * 
     */
    @JsonProperty("chargingLimitSource")
    public ChargingLimitSourceEnum getChargingLimitSource() {
        return chargingLimitSource;
    }

    /**
     * Charging_ Limit. Charging_ Limit_ Source. Charging_ Limit_ Source_ Code
     * urn:x-enexis:ecdm:uid:1:570845
     * Represents the source of the charging limit.
     * 
     * (Required)
     * 
     */
    @JsonProperty("chargingLimitSource")
    public void setChargingLimitSource(ChargingLimitSourceEnum chargingLimitSource) {
        this.chargingLimitSource = chargingLimitSource;
    }

    public ChargingLimit withChargingLimitSource(ChargingLimitSourceEnum chargingLimitSource) {
        this.chargingLimitSource = chargingLimitSource;
        return this;
    }

    /**
     * Charging_ Limit. Is_ Grid_ Critical. Indicator
     * urn:x-enexis:ecdm:uid:1:570847
     * Indicates whether the charging limit is critical for the grid.
     * 
     * 
     */
    @JsonProperty("isGridCritical")
    public Boolean getIsGridCritical() {
        return isGridCritical;
    }

    /**
     * Charging_ Limit. Is_ Grid_ Critical. Indicator
     * urn:x-enexis:ecdm:uid:1:570847
     * Indicates whether the charging limit is critical for the grid.
     * 
     * 
     */
    @JsonProperty("isGridCritical")
    public void setIsGridCritical(Boolean isGridCritical) {
        this.isGridCritical = isGridCritical;
    }

    public ChargingLimit withIsGridCritical(Boolean isGridCritical) {
        this.isGridCritical = isGridCritical;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ChargingLimit.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("chargingLimitSource");
        sb.append('=');
        sb.append(((this.chargingLimitSource == null)?"<null>":this.chargingLimitSource));
        sb.append(',');
        sb.append("isGridCritical");
        sb.append('=');
        sb.append(((this.isGridCritical == null)?"<null>":this.isGridCritical));
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
        result = ((result* 31)+((this.chargingLimitSource == null)? 0 :this.chargingLimitSource.hashCode()));
        result = ((result* 31)+((this.isGridCritical == null)? 0 :this.isGridCritical.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ChargingLimit) == false) {
            return false;
        }
        ChargingLimit rhs = ((ChargingLimit) other);
        return ((((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData)))&&((this.chargingLimitSource == rhs.chargingLimitSource)||((this.chargingLimitSource!= null)&&this.chargingLimitSource.equals(rhs.chargingLimitSource))))&&((this.isGridCritical == rhs.isGridCritical)||((this.isGridCritical!= null)&&this.isGridCritical.equals(rhs.isGridCritical))));
    }

}
