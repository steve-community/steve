
package ocpp._2020._03;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Attribute data of a variable.
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "type",
    "value",
    "mutability",
    "persistent",
    "constant"
})
@Generated("jsonschema2pojo")
public class VariableAttribute {

    /**
     * This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.
     * 
     */
    @JsonProperty("customData")
    @JsonPropertyDescription("This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.")
    @Valid
    private CustomData customData;
    /**
     * Attribute: Actual, MinSet, MaxSet, etc.
     * Defaults to Actual if absent.
     * 
     * 
     */
    @JsonProperty("type")
    @JsonPropertyDescription("Attribute: Actual, MinSet, MaxSet, etc.\r\nDefaults to Actual if absent.\r\n")
    private AttributeEnum type = AttributeEnum.fromValue("Actual");
    /**
     * Value of the attribute. May only be omitted when mutability is set to 'WriteOnly'.
     * 
     * The Configuration Variable &lt;&lt;configkey-reporting-value-size,ReportingValueSize&gt;&gt; can be used to limit GetVariableResult.attributeValue, VariableAttribute.value and EventData.actualValue. The max size of these values will always remain equal. 
     * 
     * 
     */
    @JsonProperty("value")
    @JsonPropertyDescription("Value of the attribute. May only be omitted when mutability is set to 'WriteOnly'.\r\n\r\nThe Configuration Variable &lt;&lt;configkey-reporting-value-size,ReportingValueSize&gt;&gt; can be used to limit GetVariableResult.attributeValue, VariableAttribute.value and EventData.actualValue. The max size of these values will always remain equal. \r\n")
    @Size(max = 2500)
    private String value;
    /**
     * Defines the mutability of this attribute. Default is ReadWrite when omitted.
     * 
     * 
     */
    @JsonProperty("mutability")
    @JsonPropertyDescription("Defines the mutability of this attribute. Default is ReadWrite when omitted.\r\n")
    private MutabilityEnum mutability = MutabilityEnum.fromValue("ReadWrite");
    /**
     * If true, value will be persistent across system reboots or power down. Default when omitted is false.
     * 
     * 
     */
    @JsonProperty("persistent")
    @JsonPropertyDescription("If true, value will be persistent across system reboots or power down. Default when omitted is false.\r\n")
    private Boolean persistent = false;
    /**
     * If true, value that will never be changed by the Charging Station at runtime. Default when omitted is false.
     * 
     * 
     */
    @JsonProperty("constant")
    @JsonPropertyDescription("If true, value that will never be changed by the Charging Station at runtime. Default when omitted is false.\r\n")
    private Boolean constant = false;

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

    public VariableAttribute withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * Attribute: Actual, MinSet, MaxSet, etc.
     * Defaults to Actual if absent.
     * 
     * 
     */
    @JsonProperty("type")
    public AttributeEnum getType() {
        return type;
    }

    /**
     * Attribute: Actual, MinSet, MaxSet, etc.
     * Defaults to Actual if absent.
     * 
     * 
     */
    @JsonProperty("type")
    public void setType(AttributeEnum type) {
        this.type = type;
    }

    public VariableAttribute withType(AttributeEnum type) {
        this.type = type;
        return this;
    }

    /**
     * Value of the attribute. May only be omitted when mutability is set to 'WriteOnly'.
     * 
     * The Configuration Variable &lt;&lt;configkey-reporting-value-size,ReportingValueSize&gt;&gt; can be used to limit GetVariableResult.attributeValue, VariableAttribute.value and EventData.actualValue. The max size of these values will always remain equal. 
     * 
     * 
     */
    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    /**
     * Value of the attribute. May only be omitted when mutability is set to 'WriteOnly'.
     * 
     * The Configuration Variable &lt;&lt;configkey-reporting-value-size,ReportingValueSize&gt;&gt; can be used to limit GetVariableResult.attributeValue, VariableAttribute.value and EventData.actualValue. The max size of these values will always remain equal. 
     * 
     * 
     */
    @JsonProperty("value")
    public void setValue(String value) {
        this.value = value;
    }

    public VariableAttribute withValue(String value) {
        this.value = value;
        return this;
    }

    /**
     * Defines the mutability of this attribute. Default is ReadWrite when omitted.
     * 
     * 
     */
    @JsonProperty("mutability")
    public MutabilityEnum getMutability() {
        return mutability;
    }

    /**
     * Defines the mutability of this attribute. Default is ReadWrite when omitted.
     * 
     * 
     */
    @JsonProperty("mutability")
    public void setMutability(MutabilityEnum mutability) {
        this.mutability = mutability;
    }

    public VariableAttribute withMutability(MutabilityEnum mutability) {
        this.mutability = mutability;
        return this;
    }

    /**
     * If true, value will be persistent across system reboots or power down. Default when omitted is false.
     * 
     * 
     */
    @JsonProperty("persistent")
    public Boolean getPersistent() {
        return persistent;
    }

    /**
     * If true, value will be persistent across system reboots or power down. Default when omitted is false.
     * 
     * 
     */
    @JsonProperty("persistent")
    public void setPersistent(Boolean persistent) {
        this.persistent = persistent;
    }

    public VariableAttribute withPersistent(Boolean persistent) {
        this.persistent = persistent;
        return this;
    }

    /**
     * If true, value that will never be changed by the Charging Station at runtime. Default when omitted is false.
     * 
     * 
     */
    @JsonProperty("constant")
    public Boolean getConstant() {
        return constant;
    }

    /**
     * If true, value that will never be changed by the Charging Station at runtime. Default when omitted is false.
     * 
     * 
     */
    @JsonProperty("constant")
    public void setConstant(Boolean constant) {
        this.constant = constant;
    }

    public VariableAttribute withConstant(Boolean constant) {
        this.constant = constant;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(VariableAttribute.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("type");
        sb.append('=');
        sb.append(((this.type == null)?"<null>":this.type));
        sb.append(',');
        sb.append("value");
        sb.append('=');
        sb.append(((this.value == null)?"<null>":this.value));
        sb.append(',');
        sb.append("mutability");
        sb.append('=');
        sb.append(((this.mutability == null)?"<null>":this.mutability));
        sb.append(',');
        sb.append("persistent");
        sb.append('=');
        sb.append(((this.persistent == null)?"<null>":this.persistent));
        sb.append(',');
        sb.append("constant");
        sb.append('=');
        sb.append(((this.constant == null)?"<null>":this.constant));
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
        result = ((result* 31)+((this.constant == null)? 0 :this.constant.hashCode()));
        result = ((result* 31)+((this.customData == null)? 0 :this.customData.hashCode()));
        result = ((result* 31)+((this.mutability == null)? 0 :this.mutability.hashCode()));
        result = ((result* 31)+((this.type == null)? 0 :this.type.hashCode()));
        result = ((result* 31)+((this.persistent == null)? 0 :this.persistent.hashCode()));
        result = ((result* 31)+((this.value == null)? 0 :this.value.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof VariableAttribute) == false) {
            return false;
        }
        VariableAttribute rhs = ((VariableAttribute) other);
        return (((((((this.constant == rhs.constant)||((this.constant!= null)&&this.constant.equals(rhs.constant)))&&((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData))))&&((this.mutability == rhs.mutability)||((this.mutability!= null)&&this.mutability.equals(rhs.mutability))))&&((this.type == rhs.type)||((this.type!= null)&&this.type.equals(rhs.type))))&&((this.persistent == rhs.persistent)||((this.persistent!= null)&&this.persistent.equals(rhs.persistent))))&&((this.value == rhs.value)||((this.value!= null)&&this.value.equals(rhs.value))));
    }

}
