
package ocpp._2020._03;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Class to hold results of GetVariables request.
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "attributeStatusInfo",
    "attributeStatus",
    "attributeType",
    "attributeValue",
    "component",
    "variable"
})
@Generated("jsonschema2pojo")
public class GetVariableResult {

    /**
     * This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.
     * 
     */
    @JsonProperty("customData")
    @JsonPropertyDescription("This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.")
    @Valid
    private CustomData customData;
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
     * Result status of getting the variable.
     * 
     * 
     * (Required)
     * 
     */
    @JsonProperty("attributeStatus")
    @JsonPropertyDescription("Result status of getting the variable.\r\n\r\n")
    @NotNull
    private GetVariableStatusEnum attributeStatus;
    /**
     * Attribute type for which value is requested. When absent, default Actual is assumed.
     * 
     * 
     */
    @JsonProperty("attributeType")
    @JsonPropertyDescription("Attribute type for which value is requested. When absent, default Actual is assumed.\r\n")
    private AttributeEnum attributeType = AttributeEnum.fromValue("Actual");
    /**
     * Value of requested attribute type of component-variable. This field can only be empty when the given status is NOT accepted.
     * 
     * The Configuration Variable &lt;&lt;configkey-reporting-value-size,ReportingValueSize&gt;&gt; can be used to limit GetVariableResult.attributeValue, VariableAttribute.value and EventData.actualValue. The max size of these values will always remain equal. 
     * 
     * 
     * 
     */
    @JsonProperty("attributeValue")
    @JsonPropertyDescription("Value of requested attribute type of component-variable. This field can only be empty when the given status is NOT accepted.\r\n\r\nThe Configuration Variable &lt;&lt;configkey-reporting-value-size,ReportingValueSize&gt;&gt; can be used to limit GetVariableResult.attributeValue, VariableAttribute.value and EventData.actualValue. The max size of these values will always remain equal. \r\n\r\n")
    @Size(max = 2500)
    private String attributeValue;
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

    public GetVariableResult withCustomData(CustomData customData) {
        this.customData = customData;
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

    public GetVariableResult withAttributeStatusInfo(StatusInfo attributeStatusInfo) {
        this.attributeStatusInfo = attributeStatusInfo;
        return this;
    }

    /**
     * Result status of getting the variable.
     * 
     * 
     * (Required)
     * 
     */
    @JsonProperty("attributeStatus")
    public GetVariableStatusEnum getAttributeStatus() {
        return attributeStatus;
    }

    /**
     * Result status of getting the variable.
     * 
     * 
     * (Required)
     * 
     */
    @JsonProperty("attributeStatus")
    public void setAttributeStatus(GetVariableStatusEnum attributeStatus) {
        this.attributeStatus = attributeStatus;
    }

    public GetVariableResult withAttributeStatus(GetVariableStatusEnum attributeStatus) {
        this.attributeStatus = attributeStatus;
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

    public GetVariableResult withAttributeType(AttributeEnum attributeType) {
        this.attributeType = attributeType;
        return this;
    }

    /**
     * Value of requested attribute type of component-variable. This field can only be empty when the given status is NOT accepted.
     * 
     * The Configuration Variable &lt;&lt;configkey-reporting-value-size,ReportingValueSize&gt;&gt; can be used to limit GetVariableResult.attributeValue, VariableAttribute.value and EventData.actualValue. The max size of these values will always remain equal. 
     * 
     * 
     * 
     */
    @JsonProperty("attributeValue")
    public String getAttributeValue() {
        return attributeValue;
    }

    /**
     * Value of requested attribute type of component-variable. This field can only be empty when the given status is NOT accepted.
     * 
     * The Configuration Variable &lt;&lt;configkey-reporting-value-size,ReportingValueSize&gt;&gt; can be used to limit GetVariableResult.attributeValue, VariableAttribute.value and EventData.actualValue. The max size of these values will always remain equal. 
     * 
     * 
     * 
     */
    @JsonProperty("attributeValue")
    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public GetVariableResult withAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
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

    public GetVariableResult withComponent(Component component) {
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

    public GetVariableResult withVariable(Variable variable) {
        this.variable = variable;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(GetVariableResult.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("attributeStatusInfo");
        sb.append('=');
        sb.append(((this.attributeStatusInfo == null)?"<null>":this.attributeStatusInfo));
        sb.append(',');
        sb.append("attributeStatus");
        sb.append('=');
        sb.append(((this.attributeStatus == null)?"<null>":this.attributeStatus));
        sb.append(',');
        sb.append("attributeType");
        sb.append('=');
        sb.append(((this.attributeType == null)?"<null>":this.attributeType));
        sb.append(',');
        sb.append("attributeValue");
        sb.append('=');
        sb.append(((this.attributeValue == null)?"<null>":this.attributeValue));
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
        result = ((result* 31)+((this.attributeStatusInfo == null)? 0 :this.attributeStatusInfo.hashCode()));
        result = ((result* 31)+((this.attributeStatus == null)? 0 :this.attributeStatus.hashCode()));
        result = ((result* 31)+((this.component == null)? 0 :this.component.hashCode()));
        result = ((result* 31)+((this.attributeValue == null)? 0 :this.attributeValue.hashCode()));
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
        if ((other instanceof GetVariableResult) == false) {
            return false;
        }
        GetVariableResult rhs = ((GetVariableResult) other);
        return ((((((((this.attributeStatusInfo == rhs.attributeStatusInfo)||((this.attributeStatusInfo!= null)&&this.attributeStatusInfo.equals(rhs.attributeStatusInfo)))&&((this.attributeStatus == rhs.attributeStatus)||((this.attributeStatus!= null)&&this.attributeStatus.equals(rhs.attributeStatus))))&&((this.component == rhs.component)||((this.component!= null)&&this.component.equals(rhs.component))))&&((this.attributeValue == rhs.attributeValue)||((this.attributeValue!= null)&&this.attributeValue.equals(rhs.attributeValue))))&&((this.attributeType == rhs.attributeType)||((this.attributeType!= null)&&this.attributeType.equals(rhs.attributeType))))&&((this.variable == rhs.variable)||((this.variable!= null)&&this.variable.equals(rhs.variable))))&&((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData))));
    }

}
