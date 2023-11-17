
package ocpp._2020._03;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.rwth.idsg.ocpp.jaxb.RequestType;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "certificateType"
})
@Generated("jsonschema2pojo")
public class GetInstalledCertificateIdsRequest implements RequestType
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
     * Indicates the type of certificates requested. When omitted, all certificate types are requested.
     * 
     * 
     */
    @JsonProperty("certificateType")
    @JsonPropertyDescription("Indicates the type of certificates requested. When omitted, all certificate types are requested.\r\n")
    @Size(min = 1)
    @Valid
    private List<GetCertificateIdUseEnum> certificateType = new ArrayList<GetCertificateIdUseEnum>();

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

    public GetInstalledCertificateIdsRequest withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * Indicates the type of certificates requested. When omitted, all certificate types are requested.
     * 
     * 
     */
    @JsonProperty("certificateType")
    public List<GetCertificateIdUseEnum> getCertificateType() {
        return certificateType;
    }

    /**
     * Indicates the type of certificates requested. When omitted, all certificate types are requested.
     * 
     * 
     */
    @JsonProperty("certificateType")
    public void setCertificateType(List<GetCertificateIdUseEnum> certificateType) {
        this.certificateType = certificateType;
    }

    public GetInstalledCertificateIdsRequest withCertificateType(List<GetCertificateIdUseEnum> certificateType) {
        this.certificateType = certificateType;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(GetInstalledCertificateIdsRequest.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("certificateType");
        sb.append('=');
        sb.append(((this.certificateType == null)?"<null>":this.certificateType));
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
        result = ((result* 31)+((this.certificateType == null)? 0 :this.certificateType.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof GetInstalledCertificateIdsRequest) == false) {
            return false;
        }
        GetInstalledCertificateIdsRequest rhs = ((GetInstalledCertificateIdsRequest) other);
        return (((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData)))&&((this.certificateType == rhs.certificateType)||((this.certificateType!= null)&&this.certificateType.equals(rhs.certificateType))));
    }

}
