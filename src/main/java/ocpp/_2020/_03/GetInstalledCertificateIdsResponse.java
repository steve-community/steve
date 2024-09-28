
package ocpp._2020._03;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.rwth.idsg.ocpp.jaxb.ResponseType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "status",
    "statusInfo",
    "certificateHashDataChain"
})
public class GetInstalledCertificateIdsResponse implements ResponseType
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
     * Charging Station indicates if it can process the request.
     * 
     * (Required)
     * 
     */
    @JsonProperty("status")
    @JsonPropertyDescription("Charging Station indicates if it can process the request.\r\n")
    @NotNull
    private GetInstalledCertificateStatusEnum status;
    /**
     * Element providing more information about the status.
     * 
     * 
     */
    @JsonProperty("statusInfo")
    @JsonPropertyDescription("Element providing more information about the status.\r\n")
    @Valid
    private StatusInfo statusInfo;
    @JsonProperty("certificateHashDataChain")
    @Size(min = 1)
    @Valid
    private List<CertificateHashDataChain> certificateHashDataChain = new ArrayList<CertificateHashDataChain>();

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

    public GetInstalledCertificateIdsResponse withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * Charging Station indicates if it can process the request.
     * 
     * (Required)
     * 
     */
    @JsonProperty("status")
    public GetInstalledCertificateStatusEnum getStatus() {
        return status;
    }

    /**
     * Charging Station indicates if it can process the request.
     * 
     * (Required)
     * 
     */
    @JsonProperty("status")
    public void setStatus(GetInstalledCertificateStatusEnum status) {
        this.status = status;
    }

    public GetInstalledCertificateIdsResponse withStatus(GetInstalledCertificateStatusEnum status) {
        this.status = status;
        return this;
    }

    /**
     * Element providing more information about the status.
     * 
     * 
     */
    @JsonProperty("statusInfo")
    public StatusInfo getStatusInfo() {
        return statusInfo;
    }

    /**
     * Element providing more information about the status.
     * 
     * 
     */
    @JsonProperty("statusInfo")
    public void setStatusInfo(StatusInfo statusInfo) {
        this.statusInfo = statusInfo;
    }

    public GetInstalledCertificateIdsResponse withStatusInfo(StatusInfo statusInfo) {
        this.statusInfo = statusInfo;
        return this;
    }

    @JsonProperty("certificateHashDataChain")
    public List<CertificateHashDataChain> getCertificateHashDataChain() {
        return certificateHashDataChain;
    }

    @JsonProperty("certificateHashDataChain")
    public void setCertificateHashDataChain(List<CertificateHashDataChain> certificateHashDataChain) {
        this.certificateHashDataChain = certificateHashDataChain;
    }

    public GetInstalledCertificateIdsResponse withCertificateHashDataChain(List<CertificateHashDataChain> certificateHashDataChain) {
        this.certificateHashDataChain = certificateHashDataChain;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(GetInstalledCertificateIdsResponse.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("status");
        sb.append('=');
        sb.append(((this.status == null)?"<null>":this.status));
        sb.append(',');
        sb.append("statusInfo");
        sb.append('=');
        sb.append(((this.statusInfo == null)?"<null>":this.statusInfo));
        sb.append(',');
        sb.append("certificateHashDataChain");
        sb.append('=');
        sb.append(((this.certificateHashDataChain == null)?"<null>":this.certificateHashDataChain));
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
        result = ((result* 31)+((this.statusInfo == null)? 0 :this.statusInfo.hashCode()));
        result = ((result* 31)+((this.certificateHashDataChain == null)? 0 :this.certificateHashDataChain.hashCode()));
        result = ((result* 31)+((this.status == null)? 0 :this.status.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof GetInstalledCertificateIdsResponse) == false) {
            return false;
        }
        GetInstalledCertificateIdsResponse rhs = ((GetInstalledCertificateIdsResponse) other);
        return (((((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData)))&&((this.statusInfo == rhs.statusInfo)||((this.statusInfo!= null)&&this.statusInfo.equals(rhs.statusInfo))))&&((this.certificateHashDataChain == rhs.certificateHashDataChain)||((this.certificateHashDataChain!= null)&&this.certificateHashDataChain.equals(rhs.certificateHashDataChain))))&&((this.status == rhs.status)||((this.status!= null)&&this.status.equals(rhs.status))));
    }

}
