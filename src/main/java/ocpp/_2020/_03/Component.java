
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
 * A physical or logical component
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "evse",
    "name",
    "instance"
})
@Generated("jsonschema2pojo")
public class Component {

    /**
     * This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.
     * 
     */
    @JsonProperty("customData")
    @JsonPropertyDescription("This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.")
    @Valid
    private CustomData customData;
    /**
     * EVSE
     * urn:x-oca:ocpp:uid:2:233123
     * Electric Vehicle Supply Equipment
     * 
     * 
     */
    @JsonProperty("evse")
    @JsonPropertyDescription("EVSE\r\nurn:x-oca:ocpp:uid:2:233123\r\nElectric Vehicle Supply Equipment\r\n")
    @Valid
    private EVSE evse;
    /**
     * Name of the component. Name should be taken from the list of standardized component names whenever possible. Case Insensitive. strongly advised to use Camel Case.
     * 
     * (Required)
     * 
     */
    @JsonProperty("name")
    @JsonPropertyDescription("Name of the component. Name should be taken from the list of standardized component names whenever possible. Case Insensitive. strongly advised to use Camel Case.\r\n")
    @Size(max = 50)
    @NotNull
    private String name;
    /**
     * Name of instance in case the component exists as multiple instances. Case Insensitive. strongly advised to use Camel Case.
     * 
     * 
     */
    @JsonProperty("instance")
    @JsonPropertyDescription("Name of instance in case the component exists as multiple instances. Case Insensitive. strongly advised to use Camel Case.\r\n")
    @Size(max = 50)
    private String instance;

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

    public Component withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * EVSE
     * urn:x-oca:ocpp:uid:2:233123
     * Electric Vehicle Supply Equipment
     * 
     * 
     */
    @JsonProperty("evse")
    public EVSE getEvse() {
        return evse;
    }

    /**
     * EVSE
     * urn:x-oca:ocpp:uid:2:233123
     * Electric Vehicle Supply Equipment
     * 
     * 
     */
    @JsonProperty("evse")
    public void setEvse(EVSE evse) {
        this.evse = evse;
    }

    public Component withEvse(EVSE evse) {
        this.evse = evse;
        return this;
    }

    /**
     * Name of the component. Name should be taken from the list of standardized component names whenever possible. Case Insensitive. strongly advised to use Camel Case.
     * 
     * (Required)
     * 
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * Name of the component. Name should be taken from the list of standardized component names whenever possible. Case Insensitive. strongly advised to use Camel Case.
     * 
     * (Required)
     * 
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    public Component withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Name of instance in case the component exists as multiple instances. Case Insensitive. strongly advised to use Camel Case.
     * 
     * 
     */
    @JsonProperty("instance")
    public String getInstance() {
        return instance;
    }

    /**
     * Name of instance in case the component exists as multiple instances. Case Insensitive. strongly advised to use Camel Case.
     * 
     * 
     */
    @JsonProperty("instance")
    public void setInstance(String instance) {
        this.instance = instance;
    }

    public Component withInstance(String instance) {
        this.instance = instance;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Component.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("evse");
        sb.append('=');
        sb.append(((this.evse == null)?"<null>":this.evse));
        sb.append(',');
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null)?"<null>":this.name));
        sb.append(',');
        sb.append("instance");
        sb.append('=');
        sb.append(((this.instance == null)?"<null>":this.instance));
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
        result = ((result* 31)+((this.name == null)? 0 :this.name.hashCode()));
        result = ((result* 31)+((this.customData == null)? 0 :this.customData.hashCode()));
        result = ((result* 31)+((this.instance == null)? 0 :this.instance.hashCode()));
        result = ((result* 31)+((this.evse == null)? 0 :this.evse.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Component) == false) {
            return false;
        }
        Component rhs = ((Component) other);
        return (((((this.name == rhs.name)||((this.name!= null)&&this.name.equals(rhs.name)))&&((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData))))&&((this.instance == rhs.instance)||((this.instance!= null)&&this.instance.equals(rhs.instance))))&&((this.evse == rhs.evse)||((this.evse!= null)&&this.evse.equals(rhs.evse))));
    }

}
