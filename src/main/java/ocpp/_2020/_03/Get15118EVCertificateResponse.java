
package ocpp._2020._03;

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
    "exiResponse"
})
public class Get15118EVCertificateResponse implements ResponseType
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
     * Indicates whether the message was processed properly.
     * 
     * (Required)
     * 
     */
    @JsonProperty("status")
    @JsonPropertyDescription("Indicates whether the message was processed properly.\r\n")
    @NotNull
    private Iso15118EVCertificateStatusEnum status;
    /**
     * Element providing more information about the status.
     * 
     * 
     */
    @JsonProperty("statusInfo")
    @JsonPropertyDescription("Element providing more information about the status.\r\n")
    @Valid
    private StatusInfo statusInfo;
    /**
     * Raw CertificateInstallationRes response for the EV, Base64 encoded.
     * 
     * (Required)
     * 
     */
    @JsonProperty("exiResponse")
    @JsonPropertyDescription("Raw CertificateInstallationRes response for the EV, Base64 encoded.\r\n")
    @Size(max = 5600)
    @NotNull
    private String exiResponse;

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

    public Get15118EVCertificateResponse withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * Indicates whether the message was processed properly.
     * 
     * (Required)
     * 
     */
    @JsonProperty("status")
    public Iso15118EVCertificateStatusEnum getStatus() {
        return status;
    }

    /**
     * Indicates whether the message was processed properly.
     * 
     * (Required)
     * 
     */
    @JsonProperty("status")
    public void setStatus(Iso15118EVCertificateStatusEnum status) {
        this.status = status;
    }

    public Get15118EVCertificateResponse withStatus(Iso15118EVCertificateStatusEnum status) {
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

    public Get15118EVCertificateResponse withStatusInfo(StatusInfo statusInfo) {
        this.statusInfo = statusInfo;
        return this;
    }

    /**
     * Raw CertificateInstallationRes response for the EV, Base64 encoded.
     * 
     * (Required)
     * 
     */
    @JsonProperty("exiResponse")
    public String getExiResponse() {
        return exiResponse;
    }

    /**
     * Raw CertificateInstallationRes response for the EV, Base64 encoded.
     * 
     * (Required)
     * 
     */
    @JsonProperty("exiResponse")
    public void setExiResponse(String exiResponse) {
        this.exiResponse = exiResponse;
    }

    public Get15118EVCertificateResponse withExiResponse(String exiResponse) {
        this.exiResponse = exiResponse;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Get15118EVCertificateResponse.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
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
        sb.append("exiResponse");
        sb.append('=');
        sb.append(((this.exiResponse == null)?"<null>":this.exiResponse));
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
        result = ((result* 31)+((this.exiResponse == null)? 0 :this.exiResponse.hashCode()));
        result = ((result* 31)+((this.status == null)? 0 :this.status.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Get15118EVCertificateResponse) == false) {
            return false;
        }
        Get15118EVCertificateResponse rhs = ((Get15118EVCertificateResponse) other);
        return (((((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData)))&&((this.statusInfo == rhs.statusInfo)||((this.statusInfo!= null)&&this.statusInfo.equals(rhs.statusInfo))))&&((this.exiResponse == rhs.exiResponse)||((this.exiResponse!= null)&&this.exiResponse.equals(rhs.exiResponse))))&&((this.status == rhs.status)||((this.status!= null)&&this.status.equals(rhs.status))));
    }

}
