
package ocpp._2020._03;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Class to hold parameters for GetVariables request.
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "attributeType",
    "component",
    "variable"
})
@Generated("jsonschema2pojo")
public class GetVariableData {

    /**
     * This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.
     * 
     */
    @JsonProperty("customData")
    @JsonPropertyDescription("This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.")
    @Valid
    private CustomData customData;
    /**
     * Attribute type for which value is requested. When absent, default Actual is assumed.
     * 
     * 
     */
    @JsonProperty("attributeType")
    @JsonPropertyDescription("Attribute type for which value is requested. When absent, default Actual is assumed.\r\n")
    private AttributeEnum attributeType = AttributeEnum.fromValue("Actual");
    /**
     * A physical or logical component
     * 
     * (Required)
     * 
     */
    @JsonProperty("component")
    @JsonPropertyDescription("A physical or logical component\r\n")
    @Valid
    @NotNull
    private Component component;
    /**
     * Reference key to a component-variable.
     * 
     * (Required)
     * 
     */
    @JsonProperty("variable")
    @JsonPropertyDescription("Reference key to a component-variable.\r\n")
    @Valid
    @NotNull
    private Variable variable;

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

    public GetVariableData withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * Attribute type for which value is requested. When absent, default Actual is assumed.
     * 
     * 
     */
    @JsonProperty("attributeType")
    public AttributeEnum getAttributeType() {
        return attributeType;
    }

    /**
     * Attribute type for which value is requested. When absent, default Actual is assumed.
     * 
     * 
     */
    @JsonProperty("attributeType")
    public void setAttributeType(AttributeEnum attributeType) {
        this.attributeType = attributeType;
    }

    public GetVariableData withAttributeType(AttributeEnum attributeType) {
        this.attributeType = attributeType;
        return this;
    }

    /**
     * A physical or logical component
     * 
     * (Required)
     * 
     */
    @JsonProperty("component")
    public Component getComponent() {
        return component;
    }

    /**
     * A physical or logical component
     * 
     * (Required)
     * 
     */
    @JsonProperty("component")
    public void setComponent(Component component) {
        this.component = component;
    }

    public GetVariableData withComponent(Component component) {
        this.component = component;
        return this;
    }

    /**
     * Reference key to a component-variable.
     * 
     * (Required)
     * 
     */
    @JsonProperty("variable")
    public Variable getVariable() {
        return variable;
    }

    /**
     * Reference key to a component-variable.
     * 
     * (Required)
     * 
     */
    @JsonProperty("variable")
    public void setVariable(Variable variable) {
        this.variable = variable;
    }

    public GetVariableData withVariable(Variable variable) {
        this.variable = variable;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(GetVariableData.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("attributeType");
        sb.append('=');
        sb.append(((this.attributeType == null)?"<null>":this.attributeType));
        sb.append(',');
        sb.append("component");
        sb.append('=');
        sb.append(((this.component == null)?"<null>":this.component));
        sb.append(',');
        sb.append("variable");
        sb.append('=');
        sb.append(((this.variable == null)?"<null>":this.variable));
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
        result = ((result* 31)+((this.variable == null)? 0 :this.variable.hashCode()));
        result = ((result* 31)+((this.customData == null)? 0 :this.customData.hashCode()));
        result = ((result* 31)+((this.component == null)? 0 :this.component.hashCode()));
        result = ((result* 31)+((this.attributeType == null)? 0 :this.attributeType.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof GetVariableData) == false) {
            return false;
        }
        GetVariableData rhs = ((GetVariableData) other);
        return (((((this.variable == rhs.variable)||((this.variable!= null)&&this.variable.equals(rhs.variable)))&&((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData))))&&((this.component == rhs.component)||((this.component!= null)&&this.component.equals(rhs.component))))&&((this.attributeType == rhs.attributeType)||((this.attributeType!= null)&&this.attributeType.equals(rhs.attributeType))));
    }

}
