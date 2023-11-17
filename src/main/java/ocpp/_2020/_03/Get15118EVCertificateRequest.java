
package ocpp._2020._03;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.rwth.idsg.ocpp.jaxb.RequestType;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "iso15118SchemaVersion",
    "action",
    "exiRequest"
})
@Generated("jsonschema2pojo")
public class Get15118EVCertificateRequest implements RequestType
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
     * Schema version currently used for the 15118 session between EV and Charging Station. Needed for parsing of the EXI stream by the CSMS.
     * 
     * 
     * (Required)
     * 
     */
    @JsonProperty("iso15118SchemaVersion")
    @JsonPropertyDescription("Schema version currently used for the 15118 session between EV and Charging Station. Needed for parsing of the EXI stream by the CSMS.\r\n\r\n")
    @Size(max = 50)
    @NotNull
    private String iso15118SchemaVersion;
    /**
     * Defines whether certificate needs to be installed or updated.
     * 
     * (Required)
     * 
     */
    @JsonProperty("action")
    @JsonPropertyDescription("Defines whether certificate needs to be installed or updated.\r\n")
    @NotNull
    private CertificateActionEnum action;
    /**
     * Raw CertificateInstallationReq request from EV, Base64 encoded.
     * 
     * (Required)
     * 
     */
    @JsonProperty("exiRequest")
    @JsonPropertyDescription("Raw CertificateInstallationReq request from EV, Base64 encoded.\r\n")
    @Size(max = 5600)
    @NotNull
    private String exiRequest;

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

    public Get15118EVCertificateRequest withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * Schema version currently used for the 15118 session between EV and Charging Station. Needed for parsing of the EXI stream by the CSMS.
     * 
     * 
     * (Required)
     * 
     */
    @JsonProperty("iso15118SchemaVersion")
    public String getIso15118SchemaVersion() {
        return iso15118SchemaVersion;
    }

    /**
     * Schema version currently used for the 15118 session between EV and Charging Station. Needed for parsing of the EXI stream by the CSMS.
     * 
     * 
     * (Required)
     * 
     */
    @JsonProperty("iso15118SchemaVersion")
    public void setIso15118SchemaVersion(String iso15118SchemaVersion) {
        this.iso15118SchemaVersion = iso15118SchemaVersion;
    }

    public Get15118EVCertificateRequest withIso15118SchemaVersion(String iso15118SchemaVersion) {
        this.iso15118SchemaVersion = iso15118SchemaVersion;
        return this;
    }

    /**
     * Defines whether certificate needs to be installed or updated.
     * 
     * (Required)
     * 
     */
    @JsonProperty("action")
    public CertificateActionEnum getAction() {
        return action;
    }

    /**
     * Defines whether certificate needs to be installed or updated.
     * 
     * (Required)
     * 
     */
    @JsonProperty("action")
    public void setAction(CertificateActionEnum action) {
        this.action = action;
    }

    public Get15118EVCertificateRequest withAction(CertificateActionEnum action) {
        this.action = action;
        return this;
    }

    /**
     * Raw CertificateInstallationReq request from EV, Base64 encoded.
     * 
     * (Required)
     * 
     */
    @JsonProperty("exiRequest")
    public String getExiRequest() {
        return exiRequest;
    }

    /**
     * Raw CertificateInstallationReq request from EV, Base64 encoded.
     * 
     * (Required)
     * 
     */
    @JsonProperty("exiRequest")
    public void setExiRequest(String exiRequest) {
        this.exiRequest = exiRequest;
    }

    public Get15118EVCertificateRequest withExiRequest(String exiRequest) {
        this.exiRequest = exiRequest;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Get15118EVCertificateRequest.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("iso15118SchemaVersion");
        sb.append('=');
        sb.append(((this.iso15118SchemaVersion == null)?"<null>":this.iso15118SchemaVersion));
        sb.append(',');
        sb.append("action");
        sb.append('=');
        sb.append(((this.action == null)?"<null>":this.action));
        sb.append(',');
        sb.append("exiRequest");
        sb.append('=');
        sb.append(((this.exiRequest == null)?"<null>":this.exiRequest));
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
        result = ((result* 31)+((this.iso15118SchemaVersion == null)? 0 :this.iso15118SchemaVersion.hashCode()));
        result = ((result* 31)+((this.action == null)? 0 :this.action.hashCode()));
        result = ((result* 31)+((this.exiRequest == null)? 0 :this.exiRequest.hashCode()));
        result = ((result* 31)+((this.customData == null)? 0 :this.customData.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Get15118EVCertificateRequest) == false) {
            return false;
        }
        Get15118EVCertificateRequest rhs = ((Get15118EVCertificateRequest) other);
        return (((((this.iso15118SchemaVersion == rhs.iso15118SchemaVersion)||((this.iso15118SchemaVersion!= null)&&this.iso15118SchemaVersion.equals(rhs.iso15118SchemaVersion)))&&((this.action == rhs.action)||((this.action!= null)&&this.action.equals(rhs.action))))&&((this.exiRequest == rhs.exiRequest)||((this.exiRequest!= null)&&this.exiRequest.equals(rhs.exiRequest))))&&((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData))));
    }

}
