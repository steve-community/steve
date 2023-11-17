
package ocpp._2020._03;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.rwth.idsg.ocpp.jaxb.RequestType;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "type",
    "evseId"
})
@Generated("jsonschema2pojo")
public class ResetRequest implements RequestType
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
     * This contains the type of reset that the Charging Station or EVSE should perform.
     * 
     * (Required)
     * 
     */
    @JsonProperty("type")
    @JsonPropertyDescription("This contains the type of reset that the Charging Station or EVSE should perform.\r\n")
    @NotNull
    private ResetEnum type;
    /**
     * This contains the ID of a specific EVSE that needs to be reset, instead of the entire Charging Station.
     * 
     * 
     */
    @JsonProperty("evseId")
    @JsonPropertyDescription("This contains the ID of a specific EVSE that needs to be reset, instead of the entire Charging Station.\r\n")
    private Integer evseId;

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

    public ResetRequest withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * This contains the type of reset that the Charging Station or EVSE should perform.
     * 
     * (Required)
     * 
     */
    @JsonProperty("type")
    public ResetEnum getType() {
        return type;
    }

    /**
     * This contains the type of reset that the Charging Station or EVSE should perform.
     * 
     * (Required)
     * 
     */
    @JsonProperty("type")
    public void setType(ResetEnum type) {
        this.type = type;
    }

    public ResetRequest withType(ResetEnum type) {
        this.type = type;
        return this;
    }

    /**
     * This contains the ID of a specific EVSE that needs to be reset, instead of the entire Charging Station.
     * 
     * 
     */
    @JsonProperty("evseId")
    public Integer getEvseId() {
        return evseId;
    }

    /**
     * This contains the ID of a specific EVSE that needs to be reset, instead of the entire Charging Station.
     * 
     * 
     */
    @JsonProperty("evseId")
    public void setEvseId(Integer evseId) {
        this.evseId = evseId;
    }

    public ResetRequest withEvseId(Integer evseId) {
        this.evseId = evseId;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ResetRequest.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("type");
        sb.append('=');
        sb.append(((this.type == null)?"<null>":this.type));
        sb.append(',');
        sb.append("evseId");
        sb.append('=');
        sb.append(((this.evseId == null)?"<null>":this.evseId));
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
        result = ((result* 31)+((this.evseId == null)? 0 :this.evseId.hashCode()));
        result = ((result* 31)+((this.customData == null)? 0 :this.customData.hashCode()));
        result = ((result* 31)+((this.type == null)? 0 :this.type.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ResetRequest) == false) {
            return false;
        }
        ResetRequest rhs = ((ResetRequest) other);
        return ((((this.evseId == rhs.evseId)||((this.evseId!= null)&&this.evseId.equals(rhs.evseId)))&&((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData))))&&((this.type == rhs.type)||((this.type!= null)&&this.type.equals(rhs.type))));
    }

}
