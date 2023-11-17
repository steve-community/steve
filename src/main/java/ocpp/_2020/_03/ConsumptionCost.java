
package ocpp._2020._03;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Consumption_ Cost
 * urn:x-oca:ocpp:uid:2:233259
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "startValue",
    "cost"
})
@Generated("jsonschema2pojo")
public class ConsumptionCost {

    /**
     * This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.
     * 
     */
    @JsonProperty("customData")
    @JsonPropertyDescription("This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.")
    @Valid
    private CustomData customData;
    /**
     * Consumption_ Cost. Start_ Value. Numeric
     * urn:x-oca:ocpp:uid:1:569246
     * The lowest level of consumption that defines the starting point of this consumption block. The block interval extends to the start of the next interval.
     * 
     * (Required)
     * 
     */
    @JsonProperty("startValue")
    @JsonPropertyDescription("Consumption_ Cost. Start_ Value. Numeric\r\nurn:x-oca:ocpp:uid:1:569246\r\nThe lowest level of consumption that defines the starting point of this consumption block. The block interval extends to the start of the next interval.\r\n")
    @NotNull
    private Double startValue;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("cost")
    @Size(min = 1, max = 3)
    @Valid
    @NotNull
    private List<Cost> cost = new ArrayList<Cost>();

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

    public ConsumptionCost withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * Consumption_ Cost. Start_ Value. Numeric
     * urn:x-oca:ocpp:uid:1:569246
     * The lowest level of consumption that defines the starting point of this consumption block. The block interval extends to the start of the next interval.
     * 
     * (Required)
     * 
     */
    @JsonProperty("startValue")
    public Double getStartValue() {
        return startValue;
    }

    /**
     * Consumption_ Cost. Start_ Value. Numeric
     * urn:x-oca:ocpp:uid:1:569246
     * The lowest level of consumption that defines the starting point of this consumption block. The block interval extends to the start of the next interval.
     * 
     * (Required)
     * 
     */
    @JsonProperty("startValue")
    public void setStartValue(Double startValue) {
        this.startValue = startValue;
    }

    public ConsumptionCost withStartValue(Double startValue) {
        this.startValue = startValue;
        return this;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("cost")
    public List<Cost> getCost() {
        return cost;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("cost")
    public void setCost(List<Cost> cost) {
        this.cost = cost;
    }

    public ConsumptionCost withCost(List<Cost> cost) {
        this.cost = cost;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ConsumptionCost.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("startValue");
        sb.append('=');
        sb.append(((this.startValue == null)?"<null>":this.startValue));
        sb.append(',');
        sb.append("cost");
        sb.append('=');
        sb.append(((this.cost == null)?"<null>":this.cost));
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
        result = ((result* 31)+((this.startValue == null)? 0 :this.startValue.hashCode()));
        result = ((result* 31)+((this.cost == null)? 0 :this.cost.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ConsumptionCost) == false) {
            return false;
        }
        ConsumptionCost rhs = ((ConsumptionCost) other);
        return ((((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData)))&&((this.startValue == rhs.startValue)||((this.startValue!= null)&&this.startValue.equals(rhs.startValue))))&&((this.cost == rhs.cost)||((this.cost!= null)&&this.cost.equals(rhs.cost))));
    }

}
