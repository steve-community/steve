
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
 * Represent a signed version of the meter value.
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "signedMeterData",
    "signingMethod",
    "encodingMethod",
    "publicKey"
})
@Generated("jsonschema2pojo")
public class SignedMeterValue {

    /**
     * This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.
     * 
     */
    @JsonProperty("customData")
    @JsonPropertyDescription("This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.")
    @Valid
    private CustomData customData;
    /**
     * Base64 encoded, contains the signed data which might contain more then just the meter value. It can contain information like timestamps, reference to a customer etc.
     * 
     * (Required)
     * 
     */
    @JsonProperty("signedMeterData")
    @JsonPropertyDescription("Base64 encoded, contains the signed data which might contain more then just the meter value. It can contain information like timestamps, reference to a customer etc.\r\n")
    @Size(max = 2500)
    @NotNull
    private String signedMeterData;
    /**
     * Method used to create the digital signature.
     * 
     * (Required)
     * 
     */
    @JsonProperty("signingMethod")
    @JsonPropertyDescription("Method used to create the digital signature.\r\n")
    @Size(max = 50)
    @NotNull
    private String signingMethod;
    /**
     * Method used to encode the meter values before applying the digital signature algorithm.
     * 
     * (Required)
     * 
     */
    @JsonProperty("encodingMethod")
    @JsonPropertyDescription("Method used to encode the meter values before applying the digital signature algorithm.\r\n")
    @Size(max = 50)
    @NotNull
    private String encodingMethod;
    /**
     * Base64 encoded, sending depends on configuration variable _PublicKeyWithSignedMeterValue_.
     * 
     * (Required)
     * 
     */
    @JsonProperty("publicKey")
    @JsonPropertyDescription("Base64 encoded, sending depends on configuration variable _PublicKeyWithSignedMeterValue_.\r\n")
    @Size(max = 2500)
    @NotNull
    private String publicKey;

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

    public SignedMeterValue withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * Base64 encoded, contains the signed data which might contain more then just the meter value. It can contain information like timestamps, reference to a customer etc.
     * 
     * (Required)
     * 
     */
    @JsonProperty("signedMeterData")
    public String getSignedMeterData() {
        return signedMeterData;
    }

    /**
     * Base64 encoded, contains the signed data which might contain more then just the meter value. It can contain information like timestamps, reference to a customer etc.
     * 
     * (Required)
     * 
     */
    @JsonProperty("signedMeterData")
    public void setSignedMeterData(String signedMeterData) {
        this.signedMeterData = signedMeterData;
    }

    public SignedMeterValue withSignedMeterData(String signedMeterData) {
        this.signedMeterData = signedMeterData;
        return this;
    }

    /**
     * Method used to create the digital signature.
     * 
     * (Required)
     * 
     */
    @JsonProperty("signingMethod")
    public String getSigningMethod() {
        return signingMethod;
    }

    /**
     * Method used to create the digital signature.
     * 
     * (Required)
     * 
     */
    @JsonProperty("signingMethod")
    public void setSigningMethod(String signingMethod) {
        this.signingMethod = signingMethod;
    }

    public SignedMeterValue withSigningMethod(String signingMethod) {
        this.signingMethod = signingMethod;
        return this;
    }

    /**
     * Method used to encode the meter values before applying the digital signature algorithm.
     * 
     * (Required)
     * 
     */
    @JsonProperty("encodingMethod")
    public String getEncodingMethod() {
        return encodingMethod;
    }

    /**
     * Method used to encode the meter values before applying the digital signature algorithm.
     * 
     * (Required)
     * 
     */
    @JsonProperty("encodingMethod")
    public void setEncodingMethod(String encodingMethod) {
        this.encodingMethod = encodingMethod;
    }

    public SignedMeterValue withEncodingMethod(String encodingMethod) {
        this.encodingMethod = encodingMethod;
        return this;
    }

    /**
     * Base64 encoded, sending depends on configuration variable _PublicKeyWithSignedMeterValue_.
     * 
     * (Required)
     * 
     */
    @JsonProperty("publicKey")
    public String getPublicKey() {
        return publicKey;
    }

    /**
     * Base64 encoded, sending depends on configuration variable _PublicKeyWithSignedMeterValue_.
     * 
     * (Required)
     * 
     */
    @JsonProperty("publicKey")
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public SignedMeterValue withPublicKey(String publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(SignedMeterValue.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("signedMeterData");
        sb.append('=');
        sb.append(((this.signedMeterData == null)?"<null>":this.signedMeterData));
        sb.append(',');
        sb.append("signingMethod");
        sb.append('=');
        sb.append(((this.signingMethod == null)?"<null>":this.signingMethod));
        sb.append(',');
        sb.append("encodingMethod");
        sb.append('=');
        sb.append(((this.encodingMethod == null)?"<null>":this.encodingMethod));
        sb.append(',');
        sb.append("publicKey");
        sb.append('=');
        sb.append(((this.publicKey == null)?"<null>":this.publicKey));
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
        result = ((result* 31)+((this.signedMeterData == null)? 0 :this.signedMeterData.hashCode()));
        result = ((result* 31)+((this.customData == null)? 0 :this.customData.hashCode()));
        result = ((result* 31)+((this.publicKey == null)? 0 :this.publicKey.hashCode()));
        result = ((result* 31)+((this.signingMethod == null)? 0 :this.signingMethod.hashCode()));
        result = ((result* 31)+((this.encodingMethod == null)? 0 :this.encodingMethod.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof SignedMeterValue) == false) {
            return false;
        }
        SignedMeterValue rhs = ((SignedMeterValue) other);
        return ((((((this.signedMeterData == rhs.signedMeterData)||((this.signedMeterData!= null)&&this.signedMeterData.equals(rhs.signedMeterData)))&&((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData))))&&((this.publicKey == rhs.publicKey)||((this.publicKey!= null)&&this.publicKey.equals(rhs.publicKey))))&&((this.signingMethod == rhs.signingMethod)||((this.signingMethod!= null)&&this.signingMethod.equals(rhs.signingMethod))))&&((this.encodingMethod == rhs.encodingMethod)||((this.encodingMethod!= null)&&this.encodingMethod.equals(rhs.encodingMethod))));
    }

}
