
package ocpp._2020._03;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.rwth.idsg.ocpp.jaxb.RequestType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "configurationSlot",
    "connectionData"
})
public class SetNetworkProfileRequest implements RequestType
{

    /**
     * This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.
     * 
     */
    @JsonProperty("customData")
    @JsonPropertyDescription("This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.")
    @Valid
    private CustomData customData;
    /**
     * Slot in which the configuration should be stored.
     * 
     * (Required)
     * 
     */
    @JsonProperty("configurationSlot")
    @JsonPropertyDescription("Slot in which the configuration should be stored.\r\n")
    @NotNull
    private Integer configurationSlot;
    /**
     * Communication_ Function
     * urn:x-oca:ocpp:uid:2:233304
     * The NetworkConnectionProfile defines the functional and technical parameters of a communication link.
     * 
     * (Required)
     * 
     */
    @JsonProperty("connectionData")
    @JsonPropertyDescription("Communication_ Function\r\nurn:x-oca:ocpp:uid:2:233304\r\nThe NetworkConnectionProfile defines the functional and technical parameters of a communication link.\r\n")
    @Valid
    @NotNull
    private NetworkConnectionProfile connectionData;

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

    public SetNetworkProfileRequest withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * Slot in which the configuration should be stored.
     * 
     * (Required)
     * 
     */
    @JsonProperty("configurationSlot")
    public Integer getConfigurationSlot() {
        return configurationSlot;
    }

    /**
     * Slot in which the configuration should be stored.
     * 
     * (Required)
     * 
     */
    @JsonProperty("configurationSlot")
    public void setConfigurationSlot(Integer configurationSlot) {
        this.configurationSlot = configurationSlot;
    }

    public SetNetworkProfileRequest withConfigurationSlot(Integer configurationSlot) {
        this.configurationSlot = configurationSlot;
        return this;
    }

    /**
     * Communication_ Function
     * urn:x-oca:ocpp:uid:2:233304
     * The NetworkConnectionProfile defines the functional and technical parameters of a communication link.
     * 
     * (Required)
     * 
     */
    @JsonProperty("connectionData")
    public NetworkConnectionProfile getConnectionData() {
        return connectionData;
    }

    /**
     * Communication_ Function
     * urn:x-oca:ocpp:uid:2:233304
     * The NetworkConnectionProfile defines the functional and technical parameters of a communication link.
     * 
     * (Required)
     * 
     */
    @JsonProperty("connectionData")
    public void setConnectionData(NetworkConnectionProfile connectionData) {
        this.connectionData = connectionData;
    }

    public SetNetworkProfileRequest withConnectionData(NetworkConnectionProfile connectionData) {
        this.connectionData = connectionData;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(SetNetworkProfileRequest.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("configurationSlot");
        sb.append('=');
        sb.append(((this.configurationSlot == null)?"<null>":this.configurationSlot));
        sb.append(',');
        sb.append("connectionData");
        sb.append('=');
        sb.append(((this.connectionData == null)?"<null>":this.connectionData));
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
        result = ((result* 31)+((this.connectionData == null)? 0 :this.connectionData.hashCode()));
        result = ((result* 31)+((this.configurationSlot == null)? 0 :this.configurationSlot.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof SetNetworkProfileRequest) == false) {
            return false;
        }
        SetNetworkProfileRequest rhs = ((SetNetworkProfileRequest) other);
        return ((((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData)))&&((this.connectionData == rhs.connectionData)||((this.connectionData!= null)&&this.connectionData.equals(rhs.connectionData))))&&((this.configurationSlot == rhs.configurationSlot)||((this.configurationSlot!= null)&&this.configurationSlot.equals(rhs.configurationSlot))));
    }

}
