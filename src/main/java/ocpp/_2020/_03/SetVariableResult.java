
package ocpp._2020._03;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "attributeType",
    "attributeStatus",
    "attributeStatusInfo",
    "component",
    "variable"
})
public class SetVariableResult {

    /**
     * This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.
     * 
     */
    @JsonProperty("customData")
    @JsonPropertyDescription("This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.")
    @Valid
    private CustomData customData;
    /**
     * Type of attribute: Actual, Target, MinSet, MaxSet. Default is Actual when omitted.
     * 
     * 
     */
    @JsonProperty("attributeType")
    @JsonPropertyDescription("Type of attribute: Actual, Target, MinSet, MaxSet. Default is Actual when omitted.\r\n")
    private AttributeEnum attributeType = AttributeEnum.fromValue("Actual");
    /**
     * Result status of setting the variable.
     * 
     * (Required)
     * 
     */
    @JsonProperty("attributeStatus")
    @JsonPropertyDescription("Result status of setting the variable.\r\n")
    @NotNull
    private SetVariableStatusEnum attributeStatus;
    /**
     * Element providing more information about the status.
     * 
     * 
     */
    @JsonProperty("attributeStatusInfo")
    @JsonPropertyDescription("Element providing more information about the status.\r\n")
    @Valid
    private StatusInfo attributeStatusInfo;
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

    public SetVariableResult withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * Type of attribute: Actual, Target, MinSet, MaxSet. Default is Actual when omitted.
     * 
     * 
     */
    @JsonProperty("attributeType")
    public AttributeEnum getAttributeType() {
        return attributeType;
    }

    /**
     * Type of attribute: Actual, Target, MinSet, MaxSet. Default is Actual when omitted.
     * 
     * 
     */
    @JsonProperty("attributeType")
    public void setAttributeType(AttributeEnum attributeType) {
        this.attributeType = attributeType;
    }

    public SetVariableResult withAttributeType(AttributeEnum attributeType) {
        this.attributeType = attributeType;
        return this;
    }

    /**
     * Result status of setting the variable.
     * 
     * (Required)
     * 
     */
    @JsonProperty("attributeStatus")
    public SetVariableStatusEnum getAttributeStatus() {
        return attributeStatus;
    }

    /**
     * Result status of setting the variable.
     * 
     * (Required)
     * 
     */
    @JsonProperty("attributeStatus")
    public void setAttributeStatus(SetVariableStatusEnum attributeStatus) {
        this.attributeStatus = attributeStatus;
    }

    public SetVariableResult withAttributeStatus(SetVariableStatusEnum attributeStatus) {
        this.attributeStatus = attributeStatus;
        return this;
    }

    /**
     * Element providing more information about the status.
     * 
     * 
     */
    @JsonProperty("attributeStatusInfo")
    public StatusInfo getAttributeStatusInfo() {
        return attributeStatusInfo;
    }

    /**
     * Element providing more information about the status.
     * 
     * 
     */
    @JsonProperty("attributeStatusInfo")
    public void setAttributeStatusInfo(StatusInfo attributeStatusInfo) {
        this.attributeStatusInfo = attributeStatusInfo;
    }

    public SetVariableResult withAttributeStatusInfo(StatusInfo attributeStatusInfo) {
        this.attributeStatusInfo = attributeStatusInfo;
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

    public SetVariableResult withComponent(Component component) {
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

    public SetVariableResult withVariable(Variable variable) {
        this.variable = variable;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(SetVariableResult.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("attributeType");
        sb.append('=');
        sb.append(((this.attributeType == null)?"<null>":this.attributeType));
        sb.append(',');
        sb.append("attributeStatus");
        sb.append('=');
        sb.append(((this.attributeStatus == null)?"<null>":this.attributeStatus));
        sb.append(',');
        sb.append("attributeStatusInfo");
        sb.append('=');
        sb.append(((this.attributeStatusInfo == null)?"<null>":this.attributeStatusInfo));
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
        result = ((result* 31)+((this.attributeStatus == null)? 0 :this.attributeStatus.hashCode()));
        result = ((result* 31)+((this.attributeStatusInfo == null)? 0 :this.attributeStatusInfo.hashCode()));
        result = ((result* 31)+((this.component == null)? 0 :this.component.hashCode()));
        result = ((result* 31)+((this.attributeType == null)? 0 :this.attributeType.hashCode()));
        result = ((result* 31)+((this.variable == null)? 0 :this.variable.hashCode()));
        result = ((result* 31)+((this.customData == null)? 0 :this.customData.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof SetVariableResult) == false) {
            return false;
        }
        SetVariableResult rhs = ((SetVariableResult) other);
        return (((((((this.attributeStatus == rhs.attributeStatus)||((this.attributeStatus!= null)&&this.attributeStatus.equals(rhs.attributeStatus)))&&((this.attributeStatusInfo == rhs.attributeStatusInfo)||((this.attributeStatusInfo!= null)&&this.attributeStatusInfo.equals(rhs.attributeStatusInfo))))&&((this.component == rhs.component)||((this.component!= null)&&this.component.equals(rhs.component))))&&((this.attributeType == rhs.attributeType)||((this.attributeType!= null)&&this.attributeType.equals(rhs.attributeType))))&&((this.variable == rhs.variable)||((this.variable!= null)&&this.variable.equals(rhs.variable))))&&((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData))));
    }

}
