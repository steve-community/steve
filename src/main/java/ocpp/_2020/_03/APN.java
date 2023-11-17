
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
 * APN
 * urn:x-oca:ocpp:uid:2:233134
 * Collection of configuration data needed to make a data-connection over a cellular network.
 * 
 * NOTE: When asking a GSM modem to dial in, it is possible to specify which mobile operator should be used. This can be done with the mobile country code (MCC) in combination with a mobile network code (MNC). Example: If your preferred network is Vodafone Netherlands, the MCC=204 and the MNC=04 which means the key PreferredNetwork = 20404 Some modems allows to specify a preferred network, which means, if this network is not available, a different network is used. If you specify UseOnlyPreferredNetwork and this network is not available, the modem will not dial in.
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "apn",
    "apnUserName",
    "apnPassword",
    "simPin",
    "preferredNetwork",
    "useOnlyPreferredNetwork",
    "apnAuthentication"
})
@Generated("jsonschema2pojo")
public class APN {

    /**
     * This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.
     * 
     */
    @JsonProperty("customData")
    @JsonPropertyDescription("This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.")
    @Valid
    private CustomData customData;
    /**
     * APN. APN. URI
     * urn:x-oca:ocpp:uid:1:568814
     * The Access Point Name as an URL.
     * 
     * (Required)
     * 
     */
    @JsonProperty("apn")
    @JsonPropertyDescription("APN. APN. URI\r\nurn:x-oca:ocpp:uid:1:568814\r\nThe Access Point Name as an URL.\r\n")
    @Size(max = 512)
    @NotNull
    private String apn;
    /**
     * APN. APN. User_ Name
     * urn:x-oca:ocpp:uid:1:568818
     * APN username.
     * 
     * 
     */
    @JsonProperty("apnUserName")
    @JsonPropertyDescription("APN. APN. User_ Name\r\nurn:x-oca:ocpp:uid:1:568818\r\nAPN username.\r\n")
    @Size(max = 20)
    private String apnUserName;
    /**
     * APN. APN. Password
     * urn:x-oca:ocpp:uid:1:568819
     * APN Password.
     * 
     * 
     */
    @JsonProperty("apnPassword")
    @JsonPropertyDescription("APN. APN. Password\r\nurn:x-oca:ocpp:uid:1:568819\r\nAPN Password.\r\n")
    @Size(max = 20)
    private String apnPassword;
    /**
     * APN. SIMPIN. PIN_ Code
     * urn:x-oca:ocpp:uid:1:568821
     * SIM card pin code.
     * 
     * 
     */
    @JsonProperty("simPin")
    @JsonPropertyDescription("APN. SIMPIN. PIN_ Code\r\nurn:x-oca:ocpp:uid:1:568821\r\nSIM card pin code.\r\n")
    private Integer simPin;
    /**
     * APN. Preferred_ Network. Mobile_ Network_ ID
     * urn:x-oca:ocpp:uid:1:568822
     * Preferred network, written as MCC and MNC concatenated. See note.
     * 
     * 
     */
    @JsonProperty("preferredNetwork")
    @JsonPropertyDescription("APN. Preferred_ Network. Mobile_ Network_ ID\r\nurn:x-oca:ocpp:uid:1:568822\r\nPreferred network, written as MCC and MNC concatenated. See note.\r\n")
    @Size(max = 6)
    private String preferredNetwork;
    /**
     * APN. Use_ Only_ Preferred_ Network. Indicator
     * urn:x-oca:ocpp:uid:1:568824
     * Default: false. Use only the preferred Network, do
     * not dial in when not available. See Note.
     * 
     * 
     */
    @JsonProperty("useOnlyPreferredNetwork")
    @JsonPropertyDescription("APN. Use_ Only_ Preferred_ Network. Indicator\r\nurn:x-oca:ocpp:uid:1:568824\r\nDefault: false. Use only the preferred Network, do\r\nnot dial in when not available. See Note.\r\n")
    private Boolean useOnlyPreferredNetwork = false;
    /**
     * APN. APN_ Authentication. APN_ Authentication_ Code
     * urn:x-oca:ocpp:uid:1:568828
     * Authentication method.
     * 
     * (Required)
     * 
     */
    @JsonProperty("apnAuthentication")
    @JsonPropertyDescription("APN. APN_ Authentication. APN_ Authentication_ Code\r\nurn:x-oca:ocpp:uid:1:568828\r\nAuthentication method.\r\n")
    @NotNull
    private APNAuthenticationEnum apnAuthentication;

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

    public APN withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * APN. APN. URI
     * urn:x-oca:ocpp:uid:1:568814
     * The Access Point Name as an URL.
     * 
     * (Required)
     * 
     */
    @JsonProperty("apn")
    public String getApn() {
        return apn;
    }

    /**
     * APN. APN. URI
     * urn:x-oca:ocpp:uid:1:568814
     * The Access Point Name as an URL.
     * 
     * (Required)
     * 
     */
    @JsonProperty("apn")
    public void setApn(String apn) {
        this.apn = apn;
    }

    public APN withApn(String apn) {
        this.apn = apn;
        return this;
    }

    /**
     * APN. APN. User_ Name
     * urn:x-oca:ocpp:uid:1:568818
     * APN username.
     * 
     * 
     */
    @JsonProperty("apnUserName")
    public String getApnUserName() {
        return apnUserName;
    }

    /**
     * APN. APN. User_ Name
     * urn:x-oca:ocpp:uid:1:568818
     * APN username.
     * 
     * 
     */
    @JsonProperty("apnUserName")
    public void setApnUserName(String apnUserName) {
        this.apnUserName = apnUserName;
    }

    public APN withApnUserName(String apnUserName) {
        this.apnUserName = apnUserName;
        return this;
    }

    /**
     * APN. APN. Password
     * urn:x-oca:ocpp:uid:1:568819
     * APN Password.
     * 
     * 
     */
    @JsonProperty("apnPassword")
    public String getApnPassword() {
        return apnPassword;
    }

    /**
     * APN. APN. Password
     * urn:x-oca:ocpp:uid:1:568819
     * APN Password.
     * 
     * 
     */
    @JsonProperty("apnPassword")
    public void setApnPassword(String apnPassword) {
        this.apnPassword = apnPassword;
    }

    public APN withApnPassword(String apnPassword) {
        this.apnPassword = apnPassword;
        return this;
    }

    /**
     * APN. SIMPIN. PIN_ Code
     * urn:x-oca:ocpp:uid:1:568821
     * SIM card pin code.
     * 
     * 
     */
    @JsonProperty("simPin")
    public Integer getSimPin() {
        return simPin;
    }

    /**
     * APN. SIMPIN. PIN_ Code
     * urn:x-oca:ocpp:uid:1:568821
     * SIM card pin code.
     * 
     * 
     */
    @JsonProperty("simPin")
    public void setSimPin(Integer simPin) {
        this.simPin = simPin;
    }

    public APN withSimPin(Integer simPin) {
        this.simPin = simPin;
        return this;
    }

    /**
     * APN. Preferred_ Network. Mobile_ Network_ ID
     * urn:x-oca:ocpp:uid:1:568822
     * Preferred network, written as MCC and MNC concatenated. See note.
     * 
     * 
     */
    @JsonProperty("preferredNetwork")
    public String getPreferredNetwork() {
        return preferredNetwork;
    }

    /**
     * APN. Preferred_ Network. Mobile_ Network_ ID
     * urn:x-oca:ocpp:uid:1:568822
     * Preferred network, written as MCC and MNC concatenated. See note.
     * 
     * 
     */
    @JsonProperty("preferredNetwork")
    public void setPreferredNetwork(String preferredNetwork) {
        this.preferredNetwork = preferredNetwork;
    }

    public APN withPreferredNetwork(String preferredNetwork) {
        this.preferredNetwork = preferredNetwork;
        return this;
    }

    /**
     * APN. Use_ Only_ Preferred_ Network. Indicator
     * urn:x-oca:ocpp:uid:1:568824
     * Default: false. Use only the preferred Network, do
     * not dial in when not available. See Note.
     * 
     * 
     */
    @JsonProperty("useOnlyPreferredNetwork")
    public Boolean getUseOnlyPreferredNetwork() {
        return useOnlyPreferredNetwork;
    }

    /**
     * APN. Use_ Only_ Preferred_ Network. Indicator
     * urn:x-oca:ocpp:uid:1:568824
     * Default: false. Use only the preferred Network, do
     * not dial in when not available. See Note.
     * 
     * 
     */
    @JsonProperty("useOnlyPreferredNetwork")
    public void setUseOnlyPreferredNetwork(Boolean useOnlyPreferredNetwork) {
        this.useOnlyPreferredNetwork = useOnlyPreferredNetwork;
    }

    public APN withUseOnlyPreferredNetwork(Boolean useOnlyPreferredNetwork) {
        this.useOnlyPreferredNetwork = useOnlyPreferredNetwork;
        return this;
    }

    /**
     * APN. APN_ Authentication. APN_ Authentication_ Code
     * urn:x-oca:ocpp:uid:1:568828
     * Authentication method.
     * 
     * (Required)
     * 
     */
    @JsonProperty("apnAuthentication")
    public APNAuthenticationEnum getApnAuthentication() {
        return apnAuthentication;
    }

    /**
     * APN. APN_ Authentication. APN_ Authentication_ Code
     * urn:x-oca:ocpp:uid:1:568828
     * Authentication method.
     * 
     * (Required)
     * 
     */
    @JsonProperty("apnAuthentication")
    public void setApnAuthentication(APNAuthenticationEnum apnAuthentication) {
        this.apnAuthentication = apnAuthentication;
    }

    public APN withApnAuthentication(APNAuthenticationEnum apnAuthentication) {
        this.apnAuthentication = apnAuthentication;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(APN.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("apn");
        sb.append('=');
        sb.append(((this.apn == null)?"<null>":this.apn));
        sb.append(',');
        sb.append("apnUserName");
        sb.append('=');
        sb.append(((this.apnUserName == null)?"<null>":this.apnUserName));
        sb.append(',');
        sb.append("apnPassword");
        sb.append('=');
        sb.append(((this.apnPassword == null)?"<null>":this.apnPassword));
        sb.append(',');
        sb.append("simPin");
        sb.append('=');
        sb.append(((this.simPin == null)?"<null>":this.simPin));
        sb.append(',');
        sb.append("preferredNetwork");
        sb.append('=');
        sb.append(((this.preferredNetwork == null)?"<null>":this.preferredNetwork));
        sb.append(',');
        sb.append("useOnlyPreferredNetwork");
        sb.append('=');
        sb.append(((this.useOnlyPreferredNetwork == null)?"<null>":this.useOnlyPreferredNetwork));
        sb.append(',');
        sb.append("apnAuthentication");
        sb.append('=');
        sb.append(((this.apnAuthentication == null)?"<null>":this.apnAuthentication));
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
        result = ((result* 31)+((this.preferredNetwork == null)? 0 :this.preferredNetwork.hashCode()));
        result = ((result* 31)+((this.apnUserName == null)? 0 :this.apnUserName.hashCode()));
        result = ((result* 31)+((this.apnAuthentication == null)? 0 :this.apnAuthentication.hashCode()));
        result = ((result* 31)+((this.useOnlyPreferredNetwork == null)? 0 :this.useOnlyPreferredNetwork.hashCode()));
        result = ((result* 31)+((this.simPin == null)? 0 :this.simPin.hashCode()));
        result = ((result* 31)+((this.customData == null)? 0 :this.customData.hashCode()));
        result = ((result* 31)+((this.apn == null)? 0 :this.apn.hashCode()));
        result = ((result* 31)+((this.apnPassword == null)? 0 :this.apnPassword.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof APN) == false) {
            return false;
        }
        APN rhs = ((APN) other);
        return (((((((((this.preferredNetwork == rhs.preferredNetwork)||((this.preferredNetwork!= null)&&this.preferredNetwork.equals(rhs.preferredNetwork)))&&((this.apnUserName == rhs.apnUserName)||((this.apnUserName!= null)&&this.apnUserName.equals(rhs.apnUserName))))&&((this.apnAuthentication == rhs.apnAuthentication)||((this.apnAuthentication!= null)&&this.apnAuthentication.equals(rhs.apnAuthentication))))&&((this.useOnlyPreferredNetwork == rhs.useOnlyPreferredNetwork)||((this.useOnlyPreferredNetwork!= null)&&this.useOnlyPreferredNetwork.equals(rhs.useOnlyPreferredNetwork))))&&((this.simPin == rhs.simPin)||((this.simPin!= null)&&this.simPin.equals(rhs.simPin))))&&((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData))))&&((this.apn == rhs.apn)||((this.apn!= null)&&this.apn.equals(rhs.apn))))&&((this.apnPassword == rhs.apnPassword)||((this.apnPassword!= null)&&this.apnPassword.equals(rhs.apnPassword))));
    }

}
