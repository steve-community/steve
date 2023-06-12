
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
    "certificateHashData"
})
@Generated("jsonschema2pojo")
public class DeleteCertificateRequest implements RequestType
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
     * 
     * (Required)
     * 
     */
    @JsonProperty("certificateHashData")
    @Valid
    @NotNull
    private CertificateHashData certificateHashData;

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

    public DeleteCertificateRequest withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("certificateHashData")
    public CertificateHashData getCertificateHashData() {
        return certificateHashData;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("certificateHashData")
    public void setCertificateHashData(CertificateHashData certificateHashData) {
        this.certificateHashData = certificateHashData;
    }

    public DeleteCertificateRequest withCertificateHashData(CertificateHashData certificateHashData) {
        this.certificateHashData = certificateHashData;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(DeleteCertificateRequest.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("certificateHashData");
        sb.append('=');
        sb.append(((this.certificateHashData == null)?"<null>":this.certificateHashData));
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
        result = ((result* 31)+((this.certificateHashData == null)? 0 :this.certificateHashData.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof DeleteCertificateRequest) == false) {
            return false;
        }
        DeleteCertificateRequest rhs = ((DeleteCertificateRequest) other);
        return (((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData)))&&((this.certificateHashData == rhs.certificateHashData)||((this.certificateHashData!= null)&&this.certificateHashData.equals(rhs.certificateHashData))));
    }

}
