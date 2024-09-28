
package ocpp._2020._03;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "hashAlgorithm",
    "issuerNameHash",
    "issuerKeyHash",
    "serialNumber",
    "responderURL"
})
public class OCSPRequestData {

    /**
     * This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.
     * 
     */
    @JsonProperty("customData")
    @JsonPropertyDescription("This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.")
    @Valid
    private CustomData customData;
    /**
     * Used algorithms for the hashes provided.
     * 
     * (Required)
     * 
     */
    @JsonProperty("hashAlgorithm")
    @JsonPropertyDescription("Used algorithms for the hashes provided.\r\n")
    @NotNull
    private HashAlgorithmEnum hashAlgorithm;
    /**
     * Hashed value of the Issuer DN (Distinguished Name).
     * 
     * 
     * (Required)
     * 
     */
    @JsonProperty("issuerNameHash")
    @JsonPropertyDescription("Hashed value of the Issuer DN (Distinguished Name).\r\n\r\n")
    @Size(max = 128)
    @NotNull
    private String issuerNameHash;
    /**
     * Hashed value of the issuers public key
     * 
     * (Required)
     * 
     */
    @JsonProperty("issuerKeyHash")
    @JsonPropertyDescription("Hashed value of the issuers public key\r\n")
    @Size(max = 128)
    @NotNull
    private String issuerKeyHash;
    /**
     * The serial number of the certificate.
     * 
     * (Required)
     * 
     */
    @JsonProperty("serialNumber")
    @JsonPropertyDescription("The serial number of the certificate.\r\n")
    @Size(max = 40)
    @NotNull
    private String serialNumber;
    /**
     * This contains the responder URL (Case insensitive). 
     * 
     * 
     * (Required)
     * 
     */
    @JsonProperty("responderURL")
    @JsonPropertyDescription("This contains the responder URL (Case insensitive). \r\n\r\n")
    @Size(max = 512)
    @NotNull
    private String responderURL;

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

    public OCSPRequestData withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * Used algorithms for the hashes provided.
     * 
     * (Required)
     * 
     */
    @JsonProperty("hashAlgorithm")
    public HashAlgorithmEnum getHashAlgorithm() {
        return hashAlgorithm;
    }

    /**
     * Used algorithms for the hashes provided.
     * 
     * (Required)
     * 
     */
    @JsonProperty("hashAlgorithm")
    public void setHashAlgorithm(HashAlgorithmEnum hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    public OCSPRequestData withHashAlgorithm(HashAlgorithmEnum hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
        return this;
    }

    /**
     * Hashed value of the Issuer DN (Distinguished Name).
     * 
     * 
     * (Required)
     * 
     */
    @JsonProperty("issuerNameHash")
    public String getIssuerNameHash() {
        return issuerNameHash;
    }

    /**
     * Hashed value of the Issuer DN (Distinguished Name).
     * 
     * 
     * (Required)
     * 
     */
    @JsonProperty("issuerNameHash")
    public void setIssuerNameHash(String issuerNameHash) {
        this.issuerNameHash = issuerNameHash;
    }

    public OCSPRequestData withIssuerNameHash(String issuerNameHash) {
        this.issuerNameHash = issuerNameHash;
        return this;
    }

    /**
     * Hashed value of the issuers public key
     * 
     * (Required)
     * 
     */
    @JsonProperty("issuerKeyHash")
    public String getIssuerKeyHash() {
        return issuerKeyHash;
    }

    /**
     * Hashed value of the issuers public key
     * 
     * (Required)
     * 
     */
    @JsonProperty("issuerKeyHash")
    public void setIssuerKeyHash(String issuerKeyHash) {
        this.issuerKeyHash = issuerKeyHash;
    }

    public OCSPRequestData withIssuerKeyHash(String issuerKeyHash) {
        this.issuerKeyHash = issuerKeyHash;
        return this;
    }

    /**
     * The serial number of the certificate.
     * 
     * (Required)
     * 
     */
    @JsonProperty("serialNumber")
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * The serial number of the certificate.
     * 
     * (Required)
     * 
     */
    @JsonProperty("serialNumber")
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public OCSPRequestData withSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
        return this;
    }

    /**
     * This contains the responder URL (Case insensitive). 
     * 
     * 
     * (Required)
     * 
     */
    @JsonProperty("responderURL")
    public String getResponderURL() {
        return responderURL;
    }

    /**
     * This contains the responder URL (Case insensitive). 
     * 
     * 
     * (Required)
     * 
     */
    @JsonProperty("responderURL")
    public void setResponderURL(String responderURL) {
        this.responderURL = responderURL;
    }

    public OCSPRequestData withResponderURL(String responderURL) {
        this.responderURL = responderURL;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(OCSPRequestData.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("hashAlgorithm");
        sb.append('=');
        sb.append(((this.hashAlgorithm == null)?"<null>":this.hashAlgorithm));
        sb.append(',');
        sb.append("issuerNameHash");
        sb.append('=');
        sb.append(((this.issuerNameHash == null)?"<null>":this.issuerNameHash));
        sb.append(',');
        sb.append("issuerKeyHash");
        sb.append('=');
        sb.append(((this.issuerKeyHash == null)?"<null>":this.issuerKeyHash));
        sb.append(',');
        sb.append("serialNumber");
        sb.append('=');
        sb.append(((this.serialNumber == null)?"<null>":this.serialNumber));
        sb.append(',');
        sb.append("responderURL");
        sb.append('=');
        sb.append(((this.responderURL == null)?"<null>":this.responderURL));
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
        result = ((result* 31)+((this.responderURL == null)? 0 :this.responderURL.hashCode()));
        result = ((result* 31)+((this.issuerNameHash == null)? 0 :this.issuerNameHash.hashCode()));
        result = ((result* 31)+((this.issuerKeyHash == null)? 0 :this.issuerKeyHash.hashCode()));
        result = ((result* 31)+((this.serialNumber == null)? 0 :this.serialNumber.hashCode()));
        result = ((result* 31)+((this.customData == null)? 0 :this.customData.hashCode()));
        result = ((result* 31)+((this.hashAlgorithm == null)? 0 :this.hashAlgorithm.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof OCSPRequestData) == false) {
            return false;
        }
        OCSPRequestData rhs = ((OCSPRequestData) other);
        return (((((((this.responderURL == rhs.responderURL)||((this.responderURL!= null)&&this.responderURL.equals(rhs.responderURL)))&&((this.issuerNameHash == rhs.issuerNameHash)||((this.issuerNameHash!= null)&&this.issuerNameHash.equals(rhs.issuerNameHash))))&&((this.issuerKeyHash == rhs.issuerKeyHash)||((this.issuerKeyHash!= null)&&this.issuerKeyHash.equals(rhs.issuerKeyHash))))&&((this.serialNumber == rhs.serialNumber)||((this.serialNumber!= null)&&this.serialNumber.equals(rhs.serialNumber))))&&((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData))))&&((this.hashAlgorithm == rhs.hashAlgorithm)||((this.hashAlgorithm!= null)&&this.hashAlgorithm.equals(rhs.hashAlgorithm))));
    }

}
