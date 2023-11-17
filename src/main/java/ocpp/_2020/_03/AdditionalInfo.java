
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
 * Contains a case insensitive identifier to use for the authorization and the type of authorization to support multiple forms of identifiers.
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "additionalIdToken",
    "type"
})
@Generated("jsonschema2pojo")
public class AdditionalInfo {

    /**
     * This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.
     * 
     */
    @JsonProperty("customData")
    @JsonPropertyDescription("This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.")
    @Valid
    private CustomData customData;
    /**
     * This field specifies the additional IdToken.
     * 
     * (Required)
     * 
     */
    @JsonProperty("additionalIdToken")
    @JsonPropertyDescription("This field specifies the additional IdToken.\r\n")
    @Size(max = 36)
    @NotNull
    private String additionalIdToken;
    /**
     * This defines the type of the additionalIdToken. This is a custom type, so the implementation needs to be agreed upon by all involved parties.
     * 
     * (Required)
     * 
     */
    @JsonProperty("type")
    @JsonPropertyDescription("This defines the type of the additionalIdToken. This is a custom type, so the implementation needs to be agreed upon by all involved parties.\r\n")
    @Size(max = 50)
    @NotNull
    private String type;

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

    public AdditionalInfo withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * This field specifies the additional IdToken.
     * 
     * (Required)
     * 
     */
    @JsonProperty("additionalIdToken")
    public String getAdditionalIdToken() {
        return additionalIdToken;
    }

    /**
     * This field specifies the additional IdToken.
     * 
     * (Required)
     * 
     */
    @JsonProperty("additionalIdToken")
    public void setAdditionalIdToken(String additionalIdToken) {
        this.additionalIdToken = additionalIdToken;
    }

    public AdditionalInfo withAdditionalIdToken(String additionalIdToken) {
        this.additionalIdToken = additionalIdToken;
        return this;
    }

    /**
     * This defines the type of the additionalIdToken. This is a custom type, so the implementation needs to be agreed upon by all involved parties.
     * 
     * (Required)
     * 
     */
    @JsonProperty("type")
    public String getType() {
        return type;
    }

    /**
     * This defines the type of the additionalIdToken. This is a custom type, so the implementation needs to be agreed upon by all involved parties.
     * 
     * (Required)
     * 
     */
    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    public AdditionalInfo withType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(AdditionalInfo.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("additionalIdToken");
        sb.append('=');
        sb.append(((this.additionalIdToken == null)?"<null>":this.additionalIdToken));
        sb.append(',');
        sb.append("type");
        sb.append('=');
        sb.append(((this.type == null)?"<null>":this.type));
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
        result = ((result* 31)+((this.additionalIdToken == null)? 0 :this.additionalIdToken.hashCode()));
        result = ((result* 31)+((this.type == null)? 0 :this.type.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof AdditionalInfo) == false) {
            return false;
        }
        AdditionalInfo rhs = ((AdditionalInfo) other);
        return ((((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData)))&&((this.additionalIdToken == rhs.additionalIdToken)||((this.additionalIdToken!= null)&&this.additionalIdToken.equals(rhs.additionalIdToken))))&&((this.type == rhs.type)||((this.type!= null)&&this.type.equals(rhs.type))));
    }

}
