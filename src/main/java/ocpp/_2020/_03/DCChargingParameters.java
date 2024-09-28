
package ocpp._2020._03;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;


/**
 * DC_ Charging_ Parameters
 * urn:x-oca:ocpp:uid:2:233251
 * EV DC charging parameters
 * 
 * 
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "evMaxCurrent",
    "evMaxVoltage",
    "energyAmount",
    "evMaxPower",
    "stateOfCharge",
    "evEnergyCapacity",
    "fullSoC",
    "bulkSoC"
})
public class DCChargingParameters {

    /**
     * This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.
     * 
     */
    @JsonProperty("customData")
    @JsonPropertyDescription("This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.")
    @Valid
    private CustomData customData;
    /**
     * DC_ Charging_ Parameters. EV_ Max. Current
     * urn:x-oca:ocpp:uid:1:569215
     * Maximum current (amps) supported by the electric vehicle. Includes cable capacity.
     * 
     * (Required)
     * 
     */
    @JsonProperty("evMaxCurrent")
    @JsonPropertyDescription("DC_ Charging_ Parameters. EV_ Max. Current\r\nurn:x-oca:ocpp:uid:1:569215\r\nMaximum current (amps) supported by the electric vehicle. Includes cable capacity.\r\n")
    @NotNull
    private Integer evMaxCurrent;
    /**
     * DC_ Charging_ Parameters. EV_ Max. Voltage
     * urn:x-oca:ocpp:uid:1:569216
     * Maximum voltage supported by the electric vehicle
     * 
     * (Required)
     * 
     */
    @JsonProperty("evMaxVoltage")
    @JsonPropertyDescription("DC_ Charging_ Parameters. EV_ Max. Voltage\r\nurn:x-oca:ocpp:uid:1:569216\r\nMaximum voltage supported by the electric vehicle\r\n")
    @NotNull
    private Integer evMaxVoltage;
    /**
     * DC_ Charging_ Parameters. Energy_ Amount. Energy_ Amount
     * urn:x-oca:ocpp:uid:1:569217
     * Amount of energy requested (in Wh). This inludes energy required for preconditioning.
     * 
     * 
     */
    @JsonProperty("energyAmount")
    @JsonPropertyDescription("DC_ Charging_ Parameters. Energy_ Amount. Energy_ Amount\r\nurn:x-oca:ocpp:uid:1:569217\r\nAmount of energy requested (in Wh). This inludes energy required for preconditioning.\r\n")
    private Integer energyAmount;
    /**
     * DC_ Charging_ Parameters. EV_ Max. Power
     * urn:x-oca:ocpp:uid:1:569218
     * Maximum power (in W) supported by the electric vehicle. Required for DC charging.
     * 
     * 
     */
    @JsonProperty("evMaxPower")
    @JsonPropertyDescription("DC_ Charging_ Parameters. EV_ Max. Power\r\nurn:x-oca:ocpp:uid:1:569218\r\nMaximum power (in W) supported by the electric vehicle. Required for DC charging.\r\n")
    private Integer evMaxPower;
    /**
     * DC_ Charging_ Parameters. State_ Of_ Charge. Numeric
     * urn:x-oca:ocpp:uid:1:569219
     * Energy available in the battery (in percent of the battery capacity)
     * 
     * 
     */
    @JsonProperty("stateOfCharge")
    @JsonPropertyDescription("DC_ Charging_ Parameters. State_ Of_ Charge. Numeric\r\nurn:x-oca:ocpp:uid:1:569219\r\nEnergy available in the battery (in percent of the battery capacity)\r\n")
    @DecimalMin("0")
    @DecimalMax("1E+2")
    private Integer stateOfCharge;
    /**
     * DC_ Charging_ Parameters. EV_ Energy_ Capacity. Numeric
     * urn:x-oca:ocpp:uid:1:569220
     * Capacity of the electric vehicle battery (in Wh)
     * 
     * 
     */
    @JsonProperty("evEnergyCapacity")
    @JsonPropertyDescription("DC_ Charging_ Parameters. EV_ Energy_ Capacity. Numeric\r\nurn:x-oca:ocpp:uid:1:569220\r\nCapacity of the electric vehicle battery (in Wh)\r\n")
    private Integer evEnergyCapacity;
    /**
     * DC_ Charging_ Parameters. Full_ SOC. Percentage
     * urn:x-oca:ocpp:uid:1:569221
     * Percentage of SoC at which the EV considers the battery fully charged. (possible values: 0 - 100)
     * 
     * 
     */
    @JsonProperty("fullSoC")
    @JsonPropertyDescription("DC_ Charging_ Parameters. Full_ SOC. Percentage\r\nurn:x-oca:ocpp:uid:1:569221\r\nPercentage of SoC at which the EV considers the battery fully charged. (possible values: 0 - 100)\r\n")
    @DecimalMin("0")
    @DecimalMax("1E+2")
    private Integer fullSoC;
    /**
     * DC_ Charging_ Parameters. Bulk_ SOC. Percentage
     * urn:x-oca:ocpp:uid:1:569222
     * Percentage of SoC at which the EV considers a fast charging process to end. (possible values: 0 - 100)
     * 
     * 
     */
    @JsonProperty("bulkSoC")
    @JsonPropertyDescription("DC_ Charging_ Parameters. Bulk_ SOC. Percentage\r\nurn:x-oca:ocpp:uid:1:569222\r\nPercentage of SoC at which the EV considers a fast charging process to end. (possible values: 0 - 100)\r\n")
    @DecimalMin("0")
    @DecimalMax("1E+2")
    private Integer bulkSoC;

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

    public DCChargingParameters withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * DC_ Charging_ Parameters. EV_ Max. Current
     * urn:x-oca:ocpp:uid:1:569215
     * Maximum current (amps) supported by the electric vehicle. Includes cable capacity.
     * 
     * (Required)
     * 
     */
    @JsonProperty("evMaxCurrent")
    public Integer getEvMaxCurrent() {
        return evMaxCurrent;
    }

    /**
     * DC_ Charging_ Parameters. EV_ Max. Current
     * urn:x-oca:ocpp:uid:1:569215
     * Maximum current (amps) supported by the electric vehicle. Includes cable capacity.
     * 
     * (Required)
     * 
     */
    @JsonProperty("evMaxCurrent")
    public void setEvMaxCurrent(Integer evMaxCurrent) {
        this.evMaxCurrent = evMaxCurrent;
    }

    public DCChargingParameters withEvMaxCurrent(Integer evMaxCurrent) {
        this.evMaxCurrent = evMaxCurrent;
        return this;
    }

    /**
     * DC_ Charging_ Parameters. EV_ Max. Voltage
     * urn:x-oca:ocpp:uid:1:569216
     * Maximum voltage supported by the electric vehicle
     * 
     * (Required)
     * 
     */
    @JsonProperty("evMaxVoltage")
    public Integer getEvMaxVoltage() {
        return evMaxVoltage;
    }

    /**
     * DC_ Charging_ Parameters. EV_ Max. Voltage
     * urn:x-oca:ocpp:uid:1:569216
     * Maximum voltage supported by the electric vehicle
     * 
     * (Required)
     * 
     */
    @JsonProperty("evMaxVoltage")
    public void setEvMaxVoltage(Integer evMaxVoltage) {
        this.evMaxVoltage = evMaxVoltage;
    }

    public DCChargingParameters withEvMaxVoltage(Integer evMaxVoltage) {
        this.evMaxVoltage = evMaxVoltage;
        return this;
    }

    /**
     * DC_ Charging_ Parameters. Energy_ Amount. Energy_ Amount
     * urn:x-oca:ocpp:uid:1:569217
     * Amount of energy requested (in Wh). This inludes energy required for preconditioning.
     * 
     * 
     */
    @JsonProperty("energyAmount")
    public Integer getEnergyAmount() {
        return energyAmount;
    }

    /**
     * DC_ Charging_ Parameters. Energy_ Amount. Energy_ Amount
     * urn:x-oca:ocpp:uid:1:569217
     * Amount of energy requested (in Wh). This inludes energy required for preconditioning.
     * 
     * 
     */
    @JsonProperty("energyAmount")
    public void setEnergyAmount(Integer energyAmount) {
        this.energyAmount = energyAmount;
    }

    public DCChargingParameters withEnergyAmount(Integer energyAmount) {
        this.energyAmount = energyAmount;
        return this;
    }

    /**
     * DC_ Charging_ Parameters. EV_ Max. Power
     * urn:x-oca:ocpp:uid:1:569218
     * Maximum power (in W) supported by the electric vehicle. Required for DC charging.
     * 
     * 
     */
    @JsonProperty("evMaxPower")
    public Integer getEvMaxPower() {
        return evMaxPower;
    }

    /**
     * DC_ Charging_ Parameters. EV_ Max. Power
     * urn:x-oca:ocpp:uid:1:569218
     * Maximum power (in W) supported by the electric vehicle. Required for DC charging.
     * 
     * 
     */
    @JsonProperty("evMaxPower")
    public void setEvMaxPower(Integer evMaxPower) {
        this.evMaxPower = evMaxPower;
    }

    public DCChargingParameters withEvMaxPower(Integer evMaxPower) {
        this.evMaxPower = evMaxPower;
        return this;
    }

    /**
     * DC_ Charging_ Parameters. State_ Of_ Charge. Numeric
     * urn:x-oca:ocpp:uid:1:569219
     * Energy available in the battery (in percent of the battery capacity)
     * 
     * 
     */
    @JsonProperty("stateOfCharge")
    public Integer getStateOfCharge() {
        return stateOfCharge;
    }

    /**
     * DC_ Charging_ Parameters. State_ Of_ Charge. Numeric
     * urn:x-oca:ocpp:uid:1:569219
     * Energy available in the battery (in percent of the battery capacity)
     * 
     * 
     */
    @JsonProperty("stateOfCharge")
    public void setStateOfCharge(Integer stateOfCharge) {
        this.stateOfCharge = stateOfCharge;
    }

    public DCChargingParameters withStateOfCharge(Integer stateOfCharge) {
        this.stateOfCharge = stateOfCharge;
        return this;
    }

    /**
     * DC_ Charging_ Parameters. EV_ Energy_ Capacity. Numeric
     * urn:x-oca:ocpp:uid:1:569220
     * Capacity of the electric vehicle battery (in Wh)
     * 
     * 
     */
    @JsonProperty("evEnergyCapacity")
    public Integer getEvEnergyCapacity() {
        return evEnergyCapacity;
    }

    /**
     * DC_ Charging_ Parameters. EV_ Energy_ Capacity. Numeric
     * urn:x-oca:ocpp:uid:1:569220
     * Capacity of the electric vehicle battery (in Wh)
     * 
     * 
     */
    @JsonProperty("evEnergyCapacity")
    public void setEvEnergyCapacity(Integer evEnergyCapacity) {
        this.evEnergyCapacity = evEnergyCapacity;
    }

    public DCChargingParameters withEvEnergyCapacity(Integer evEnergyCapacity) {
        this.evEnergyCapacity = evEnergyCapacity;
        return this;
    }

    /**
     * DC_ Charging_ Parameters. Full_ SOC. Percentage
     * urn:x-oca:ocpp:uid:1:569221
     * Percentage of SoC at which the EV considers the battery fully charged. (possible values: 0 - 100)
     * 
     * 
     */
    @JsonProperty("fullSoC")
    public Integer getFullSoC() {
        return fullSoC;
    }

    /**
     * DC_ Charging_ Parameters. Full_ SOC. Percentage
     * urn:x-oca:ocpp:uid:1:569221
     * Percentage of SoC at which the EV considers the battery fully charged. (possible values: 0 - 100)
     * 
     * 
     */
    @JsonProperty("fullSoC")
    public void setFullSoC(Integer fullSoC) {
        this.fullSoC = fullSoC;
    }

    public DCChargingParameters withFullSoC(Integer fullSoC) {
        this.fullSoC = fullSoC;
        return this;
    }

    /**
     * DC_ Charging_ Parameters. Bulk_ SOC. Percentage
     * urn:x-oca:ocpp:uid:1:569222
     * Percentage of SoC at which the EV considers a fast charging process to end. (possible values: 0 - 100)
     * 
     * 
     */
    @JsonProperty("bulkSoC")
    public Integer getBulkSoC() {
        return bulkSoC;
    }

    /**
     * DC_ Charging_ Parameters. Bulk_ SOC. Percentage
     * urn:x-oca:ocpp:uid:1:569222
     * Percentage of SoC at which the EV considers a fast charging process to end. (possible values: 0 - 100)
     * 
     * 
     */
    @JsonProperty("bulkSoC")
    public void setBulkSoC(Integer bulkSoC) {
        this.bulkSoC = bulkSoC;
    }

    public DCChargingParameters withBulkSoC(Integer bulkSoC) {
        this.bulkSoC = bulkSoC;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(DCChargingParameters.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("evMaxCurrent");
        sb.append('=');
        sb.append(((this.evMaxCurrent == null)?"<null>":this.evMaxCurrent));
        sb.append(',');
        sb.append("evMaxVoltage");
        sb.append('=');
        sb.append(((this.evMaxVoltage == null)?"<null>":this.evMaxVoltage));
        sb.append(',');
        sb.append("energyAmount");
        sb.append('=');
        sb.append(((this.energyAmount == null)?"<null>":this.energyAmount));
        sb.append(',');
        sb.append("evMaxPower");
        sb.append('=');
        sb.append(((this.evMaxPower == null)?"<null>":this.evMaxPower));
        sb.append(',');
        sb.append("stateOfCharge");
        sb.append('=');
        sb.append(((this.stateOfCharge == null)?"<null>":this.stateOfCharge));
        sb.append(',');
        sb.append("evEnergyCapacity");
        sb.append('=');
        sb.append(((this.evEnergyCapacity == null)?"<null>":this.evEnergyCapacity));
        sb.append(',');
        sb.append("fullSoC");
        sb.append('=');
        sb.append(((this.fullSoC == null)?"<null>":this.fullSoC));
        sb.append(',');
        sb.append("bulkSoC");
        sb.append('=');
        sb.append(((this.bulkSoC == null)?"<null>":this.bulkSoC));
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
        result = ((result* 31)+((this.evMaxCurrent == null)? 0 :this.evMaxCurrent.hashCode()));
        result = ((result* 31)+((this.stateOfCharge == null)? 0 :this.stateOfCharge.hashCode()));
        result = ((result* 31)+((this.evEnergyCapacity == null)? 0 :this.evEnergyCapacity.hashCode()));
        result = ((result* 31)+((this.evMaxVoltage == null)? 0 :this.evMaxVoltage.hashCode()));
        result = ((result* 31)+((this.energyAmount == null)? 0 :this.energyAmount.hashCode()));
        result = ((result* 31)+((this.bulkSoC == null)? 0 :this.bulkSoC.hashCode()));
        result = ((result* 31)+((this.customData == null)? 0 :this.customData.hashCode()));
        result = ((result* 31)+((this.evMaxPower == null)? 0 :this.evMaxPower.hashCode()));
        result = ((result* 31)+((this.fullSoC == null)? 0 :this.fullSoC.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof DCChargingParameters) == false) {
            return false;
        }
        DCChargingParameters rhs = ((DCChargingParameters) other);
        return ((((((((((this.evMaxCurrent == rhs.evMaxCurrent)||((this.evMaxCurrent!= null)&&this.evMaxCurrent.equals(rhs.evMaxCurrent)))&&((this.stateOfCharge == rhs.stateOfCharge)||((this.stateOfCharge!= null)&&this.stateOfCharge.equals(rhs.stateOfCharge))))&&((this.evEnergyCapacity == rhs.evEnergyCapacity)||((this.evEnergyCapacity!= null)&&this.evEnergyCapacity.equals(rhs.evEnergyCapacity))))&&((this.evMaxVoltage == rhs.evMaxVoltage)||((this.evMaxVoltage!= null)&&this.evMaxVoltage.equals(rhs.evMaxVoltage))))&&((this.energyAmount == rhs.energyAmount)||((this.energyAmount!= null)&&this.energyAmount.equals(rhs.energyAmount))))&&((this.bulkSoC == rhs.bulkSoC)||((this.bulkSoC!= null)&&this.bulkSoC.equals(rhs.bulkSoC))))&&((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData))))&&((this.evMaxPower == rhs.evMaxPower)||((this.evMaxPower!= null)&&this.evMaxPower.equals(rhs.evMaxPower))))&&((this.fullSoC == rhs.fullSoC)||((this.fullSoC!= null)&&this.fullSoC.equals(rhs.fullSoC))));
    }

}
