
package ocpp._2020._03;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.rwth.idsg.ocpp.jaxb.RequestType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "customerCertificate",
    "idToken",
    "requestId",
    "report",
    "clear",
    "customerIdentifier"
})
public class CustomerInformationRequest implements RequestType
{

    /**
     * This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.
     * 
     */
    @JsonProperty("customData")
    @JsonPropertyDescription("This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.")
    @Valid
    private CustomData customData;
    @JsonProperty("customerCertificate")
    @Valid
    private CertificateHashData customerCertificate;
    /**
     * Contains a case insensitive identifier to use for the authorization and the type of authorization to support multiple forms of identifiers.
     * 
     * 
     */
    @JsonProperty("idToken")
    @JsonPropertyDescription("Contains a case insensitive identifier to use for the authorization and the type of authorization to support multiple forms of identifiers.\r\n")
    @Valid
    private IdToken idToken;
    /**
     * The Id of the request.
     * 
     * 
     * (Required)
     * 
     */
    @JsonProperty("requestId")
    @JsonPropertyDescription("The Id of the request.\r\n\r\n")
    @NotNull
    private Integer requestId;
    /**
     * Flag indicating whether the Charging Station should return NotifyCustomerInformationRequest messages containing information about the customer referred to.
     * 
     * (Required)
     * 
     */
    @JsonProperty("report")
    @JsonPropertyDescription("Flag indicating whether the Charging Station should return NotifyCustomerInformationRequest messages containing information about the customer referred to.\r\n")
    @NotNull
    private Boolean report;
    /**
     * Flag indicating whether the Charging Station should clear all information about the customer referred to.
     * 
     * (Required)
     * 
     */
    @JsonProperty("clear")
    @JsonPropertyDescription("Flag indicating whether the Charging Station should clear all information about the customer referred to.\r\n")
    @NotNull
    private Boolean clear;
    /**
     * A (e.g. vendor specific) identifier of the customer this request refers to. This field contains a custom identifier other than IdToken and Certificate.
     * One of the possible identifiers (customerIdentifier, customerIdToken or customerCertificate) should be in the request message.
     * 
     * 
     */
    @JsonProperty("customerIdentifier")
    @JsonPropertyDescription("A (e.g. vendor specific) identifier of the customer this request refers to. This field contains a custom identifier other than IdToken and Certificate.\r\nOne of the possible identifiers (customerIdentifier, customerIdToken or customerCertificate) should be in the request message.\r\n")
    @Size(max = 64)
    private String customerIdentifier;

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

    public CustomerInformationRequest withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    @JsonProperty("customerCertificate")
    public CertificateHashData getCustomerCertificate() {
        return customerCertificate;
    }

    @JsonProperty("customerCertificate")
    public void setCustomerCertificate(CertificateHashData customerCertificate) {
        this.customerCertificate = customerCertificate;
    }

    public CustomerInformationRequest withCustomerCertificate(CertificateHashData customerCertificate) {
        this.customerCertificate = customerCertificate;
        return this;
    }

    /**
     * Contains a case insensitive identifier to use for the authorization and the type of authorization to support multiple forms of identifiers.
     * 
     * 
     */
    @JsonProperty("idToken")
    public IdToken getIdToken() {
        return idToken;
    }

    /**
     * Contains a case insensitive identifier to use for the authorization and the type of authorization to support multiple forms of identifiers.
     * 
     * 
     */
    @JsonProperty("idToken")
    public void setIdToken(IdToken idToken) {
        this.idToken = idToken;
    }

    public CustomerInformationRequest withIdToken(IdToken idToken) {
        this.idToken = idToken;
        return this;
    }

    /**
     * The Id of the request.
     * 
     * 
     * (Required)
     * 
     */
    @JsonProperty("requestId")
    public Integer getRequestId() {
        return requestId;
    }

    /**
     * The Id of the request.
     * 
     * 
     * (Required)
     * 
     */
    @JsonProperty("requestId")
    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public CustomerInformationRequest withRequestId(Integer requestId) {
        this.requestId = requestId;
        return this;
    }

    /**
     * Flag indicating whether the Charging Station should return NotifyCustomerInformationRequest messages containing information about the customer referred to.
     * 
     * (Required)
     * 
     */
    @JsonProperty("report")
    public Boolean getReport() {
        return report;
    }

    /**
     * Flag indicating whether the Charging Station should return NotifyCustomerInformationRequest messages containing information about the customer referred to.
     * 
     * (Required)
     * 
     */
    @JsonProperty("report")
    public void setReport(Boolean report) {
        this.report = report;
    }

    public CustomerInformationRequest withReport(Boolean report) {
        this.report = report;
        return this;
    }

    /**
     * Flag indicating whether the Charging Station should clear all information about the customer referred to.
     * 
     * (Required)
     * 
     */
    @JsonProperty("clear")
    public Boolean getClear() {
        return clear;
    }

    /**
     * Flag indicating whether the Charging Station should clear all information about the customer referred to.
     * 
     * (Required)
     * 
     */
    @JsonProperty("clear")
    public void setClear(Boolean clear) {
        this.clear = clear;
    }

    public CustomerInformationRequest withClear(Boolean clear) {
        this.clear = clear;
        return this;
    }

    /**
     * A (e.g. vendor specific) identifier of the customer this request refers to. This field contains a custom identifier other than IdToken and Certificate.
     * One of the possible identifiers (customerIdentifier, customerIdToken or customerCertificate) should be in the request message.
     * 
     * 
     */
    @JsonProperty("customerIdentifier")
    public String getCustomerIdentifier() {
        return customerIdentifier;
    }

    /**
     * A (e.g. vendor specific) identifier of the customer this request refers to. This field contains a custom identifier other than IdToken and Certificate.
     * One of the possible identifiers (customerIdentifier, customerIdToken or customerCertificate) should be in the request message.
     * 
     * 
     */
    @JsonProperty("customerIdentifier")
    public void setCustomerIdentifier(String customerIdentifier) {
        this.customerIdentifier = customerIdentifier;
    }

    public CustomerInformationRequest withCustomerIdentifier(String customerIdentifier) {
        this.customerIdentifier = customerIdentifier;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(CustomerInformationRequest.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("customerCertificate");
        sb.append('=');
        sb.append(((this.customerCertificate == null)?"<null>":this.customerCertificate));
        sb.append(',');
        sb.append("idToken");
        sb.append('=');
        sb.append(((this.idToken == null)?"<null>":this.idToken));
        sb.append(',');
        sb.append("requestId");
        sb.append('=');
        sb.append(((this.requestId == null)?"<null>":this.requestId));
        sb.append(',');
        sb.append("report");
        sb.append('=');
        sb.append(((this.report == null)?"<null>":this.report));
        sb.append(',');
        sb.append("clear");
        sb.append('=');
        sb.append(((this.clear == null)?"<null>":this.clear));
        sb.append(',');
        sb.append("customerIdentifier");
        sb.append('=');
        sb.append(((this.customerIdentifier == null)?"<null>":this.customerIdentifier));
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
        result = ((result* 31)+((this.customerCertificate == null)? 0 :this.customerCertificate.hashCode()));
        result = ((result* 31)+((this.requestId == null)? 0 :this.requestId.hashCode()));
        result = ((result* 31)+((this.idToken == null)? 0 :this.idToken.hashCode()));
        result = ((result* 31)+((this.report == null)? 0 :this.report.hashCode()));
        result = ((result* 31)+((this.clear == null)? 0 :this.clear.hashCode()));
        result = ((result* 31)+((this.customData == null)? 0 :this.customData.hashCode()));
        result = ((result* 31)+((this.customerIdentifier == null)? 0 :this.customerIdentifier.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof CustomerInformationRequest) == false) {
            return false;
        }
        CustomerInformationRequest rhs = ((CustomerInformationRequest) other);
        return ((((((((this.customerCertificate == rhs.customerCertificate)||((this.customerCertificate!= null)&&this.customerCertificate.equals(rhs.customerCertificate)))&&((this.requestId == rhs.requestId)||((this.requestId!= null)&&this.requestId.equals(rhs.requestId))))&&((this.idToken == rhs.idToken)||((this.idToken!= null)&&this.idToken.equals(rhs.idToken))))&&((this.report == rhs.report)||((this.report!= null)&&this.report.equals(rhs.report))))&&((this.clear == rhs.clear)||((this.clear!= null)&&this.clear.equals(rhs.clear))))&&((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData))))&&((this.customerIdentifier == rhs.customerIdentifier)||((this.customerIdentifier!= null)&&this.customerIdentifier.equals(rhs.customerIdentifier))));
    }

}
