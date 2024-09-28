
package ocpp._2020._03;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


/**
 * Class to report components, variables and variable attributes and characteristics.
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "component",
    "variable",
    "variableAttribute",
    "variableCharacteristics"
})
public class ReportData {

    /**
     * This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.
     * 
     */
    @JsonProperty("customData")
    @JsonPropertyDescription("This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.")
    @Valid
    private CustomData customData;
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
     * 
     * (Required)
     * 
     */
    @JsonProperty("variableAttribute")
    @Size(min = 1, max = 4)
    @Valid
    @NotNull
    private List<VariableAttribute> variableAttribute = new ArrayList<VariableAttribute>();
    /**
     * Fixed read-only parameters of a variable.
     * 
     * 
     */
    @JsonProperty("variableCharacteristics")
    @JsonPropertyDescription("Fixed read-only parameters of a variable.\r\n")
    @Valid
    private VariableCharacteristics variableCharacteristics;

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

    public ReportData withCustomData(CustomData customData) {
        this.customData = customData;
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

    public ReportData withComponent(Component component) {
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

    public ReportData withVariable(Variable variable) {
        this.variable = variable;
        return this;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("variableAttribute")
    public List<VariableAttribute> getVariableAttribute() {
        return variableAttribute;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("variableAttribute")
    public void setVariableAttribute(List<VariableAttribute> variableAttribute) {
        this.variableAttribute = variableAttribute;
    }

    public ReportData withVariableAttribute(List<VariableAttribute> variableAttribute) {
        this.variableAttribute = variableAttribute;
        return this;
    }

    /**
     * Fixed read-only parameters of a variable.
     * 
     * 
     */
    @JsonProperty("variableCharacteristics")
    public VariableCharacteristics getVariableCharacteristics() {
        return variableCharacteristics;
    }

    /**
     * Fixed read-only parameters of a variable.
     * 
     * 
     */
    @JsonProperty("variableCharacteristics")
    public void setVariableCharacteristics(VariableCharacteristics variableCharacteristics) {
        this.variableCharacteristics = variableCharacteristics;
    }

    public ReportData withVariableCharacteristics(VariableCharacteristics variableCharacteristics) {
        this.variableCharacteristics = variableCharacteristics;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ReportData.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("component");
        sb.append('=');
        sb.append(((this.component == null)?"<null>":this.component));
        sb.append(',');
        sb.append("variable");
        sb.append('=');
        sb.append(((this.variable == null)?"<null>":this.variable));
        sb.append(',');
        sb.append("variableAttribute");
        sb.append('=');
        sb.append(((this.variableAttribute == null)?"<null>":this.variableAttribute));
        sb.append(',');
        sb.append("variableCharacteristics");
        sb.append('=');
        sb.append(((this.variableCharacteristics == null)?"<null>":this.variableCharacteristics));
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
        result = ((result* 31)+((this.variableAttribute == null)? 0 :this.variableAttribute.hashCode()));
        result = ((result* 31)+((this.variableCharacteristics == null)? 0 :this.variableCharacteristics.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ReportData) == false) {
            return false;
        }
        ReportData rhs = ((ReportData) other);
        return ((((((this.variable == rhs.variable)||((this.variable!= null)&&this.variable.equals(rhs.variable)))&&((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData))))&&((this.component == rhs.component)||((this.component!= null)&&this.component.equals(rhs.component))))&&((this.variableAttribute == rhs.variableAttribute)||((this.variableAttribute!= null)&&this.variableAttribute.equals(rhs.variableAttribute))))&&((this.variableCharacteristics == rhs.variableCharacteristics)||((this.variableCharacteristics!= null)&&this.variableCharacteristics.equals(rhs.variableCharacteristics))));
    }

}
