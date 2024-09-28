
package ocpp._2020._03;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


/**
 * Sales_ Tariff_ Entry
 * urn:x-oca:ocpp:uid:2:233271
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "relativeTimeInterval",
    "ePriceLevel",
    "consumptionCost"
})
public class SalesTariffEntry {

    /**
     * This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.
     * 
     */
    @JsonProperty("customData")
    @JsonPropertyDescription("This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.")
    @Valid
    private CustomData customData;
    /**
     * Relative_ Timer_ Interval
     * urn:x-oca:ocpp:uid:2:233270
     * 
     * (Required)
     * 
     */
    @JsonProperty("relativeTimeInterval")
    @JsonPropertyDescription("Relative_ Timer_ Interval\r\nurn:x-oca:ocpp:uid:2:233270\r\n")
    @Valid
    @NotNull
    private RelativeTimeInterval relativeTimeInterval;
    /**
     * Sales_ Tariff_ Entry. E_ Price_ Level. Unsigned_ Integer
     * urn:x-oca:ocpp:uid:1:569281
     * Defines the price level of this SalesTariffEntry (referring to NumEPriceLevels). Small values for the EPriceLevel represent a cheaper TariffEntry. Large values for the EPriceLevel represent a more expensive TariffEntry.
     * 
     * 
     */
    @JsonProperty("ePriceLevel")
    @JsonPropertyDescription("Sales_ Tariff_ Entry. E_ Price_ Level. Unsigned_ Integer\r\nurn:x-oca:ocpp:uid:1:569281\r\nDefines the price level of this SalesTariffEntry (referring to NumEPriceLevels). Small values for the EPriceLevel represent a cheaper TariffEntry. Large values for the EPriceLevel represent a more expensive TariffEntry.\r\n")
    @DecimalMin("0")
    private Integer ePriceLevel;
    @JsonProperty("consumptionCost")
    @Size(min = 1, max = 3)
    @Valid
    private List<ConsumptionCost> consumptionCost = new ArrayList<ConsumptionCost>();

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

    public SalesTariffEntry withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * Relative_ Timer_ Interval
     * urn:x-oca:ocpp:uid:2:233270
     * 
     * (Required)
     * 
     */
    @JsonProperty("relativeTimeInterval")
    public RelativeTimeInterval getRelativeTimeInterval() {
        return relativeTimeInterval;
    }

    /**
     * Relative_ Timer_ Interval
     * urn:x-oca:ocpp:uid:2:233270
     * 
     * (Required)
     * 
     */
    @JsonProperty("relativeTimeInterval")
    public void setRelativeTimeInterval(RelativeTimeInterval relativeTimeInterval) {
        this.relativeTimeInterval = relativeTimeInterval;
    }

    public SalesTariffEntry withRelativeTimeInterval(RelativeTimeInterval relativeTimeInterval) {
        this.relativeTimeInterval = relativeTimeInterval;
        return this;
    }

    /**
     * Sales_ Tariff_ Entry. E_ Price_ Level. Unsigned_ Integer
     * urn:x-oca:ocpp:uid:1:569281
     * Defines the price level of this SalesTariffEntry (referring to NumEPriceLevels). Small values for the EPriceLevel represent a cheaper TariffEntry. Large values for the EPriceLevel represent a more expensive TariffEntry.
     * 
     * 
     */
    @JsonProperty("ePriceLevel")
    public Integer getePriceLevel() {
        return ePriceLevel;
    }

    /**
     * Sales_ Tariff_ Entry. E_ Price_ Level. Unsigned_ Integer
     * urn:x-oca:ocpp:uid:1:569281
     * Defines the price level of this SalesTariffEntry (referring to NumEPriceLevels). Small values for the EPriceLevel represent a cheaper TariffEntry. Large values for the EPriceLevel represent a more expensive TariffEntry.
     * 
     * 
     */
    @JsonProperty("ePriceLevel")
    public void setePriceLevel(Integer ePriceLevel) {
        this.ePriceLevel = ePriceLevel;
    }

    public SalesTariffEntry withePriceLevel(Integer ePriceLevel) {
        this.ePriceLevel = ePriceLevel;
        return this;
    }

    @JsonProperty("consumptionCost")
    public List<ConsumptionCost> getConsumptionCost() {
        return consumptionCost;
    }

    @JsonProperty("consumptionCost")
    public void setConsumptionCost(List<ConsumptionCost> consumptionCost) {
        this.consumptionCost = consumptionCost;
    }

    public SalesTariffEntry withConsumptionCost(List<ConsumptionCost> consumptionCost) {
        this.consumptionCost = consumptionCost;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(SalesTariffEntry.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("relativeTimeInterval");
        sb.append('=');
        sb.append(((this.relativeTimeInterval == null)?"<null>":this.relativeTimeInterval));
        sb.append(',');
        sb.append("ePriceLevel");
        sb.append('=');
        sb.append(((this.ePriceLevel == null)?"<null>":this.ePriceLevel));
        sb.append(',');
        sb.append("consumptionCost");
        sb.append('=');
        sb.append(((this.consumptionCost == null)?"<null>":this.consumptionCost));
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
        result = ((result* 31)+((this.consumptionCost == null)? 0 :this.consumptionCost.hashCode()));
        result = ((result* 31)+((this.customData == null)? 0 :this.customData.hashCode()));
        result = ((result* 31)+((this.relativeTimeInterval == null)? 0 :this.relativeTimeInterval.hashCode()));
        result = ((result* 31)+((this.ePriceLevel == null)? 0 :this.ePriceLevel.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof SalesTariffEntry) == false) {
            return false;
        }
        SalesTariffEntry rhs = ((SalesTariffEntry) other);
        return (((((this.consumptionCost == rhs.consumptionCost)||((this.consumptionCost!= null)&&this.consumptionCost.equals(rhs.consumptionCost)))&&((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData))))&&((this.relativeTimeInterval == rhs.relativeTimeInterval)||((this.relativeTimeInterval!= null)&&this.relativeTimeInterval.equals(rhs.relativeTimeInterval))))&&((this.ePriceLevel == rhs.ePriceLevel)||((this.ePriceLevel!= null)&&this.ePriceLevel.equals(rhs.ePriceLevel))));
    }

}
