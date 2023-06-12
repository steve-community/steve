
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
 * Communication_ Function
 * urn:x-oca:ocpp:uid:2:233304
 * The NetworkConnectionProfile defines the functional and technical parameters of a communication link.
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "apn",
    "ocppVersion",
    "ocppTransport",
    "ocppCsmsUrl",
    "messageTimeout",
    "securityProfile",
    "ocppInterface",
    "vpn"
})
@Generated("jsonschema2pojo")
public class NetworkConnectionProfile {

    /**
     * This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.
     * 
     */
    @JsonProperty("customData")
    @JsonPropertyDescription("This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.")
    @Valid
    private CustomData customData;
    /**
     * APN
     * urn:x-oca:ocpp:uid:2:233134
     * Collection of configuration data needed to make a data-connection over a cellular network.
     * 
     * NOTE: When asking a GSM modem to dial in, it is possible to specify which mobile operator should be used. This can be done with the mobile country code (MCC) in combination with a mobile network code (MNC). Example: If your preferred network is Vodafone Netherlands, the MCC=204 and the MNC=04 which means the key PreferredNetwork = 20404 Some modems allows to specify a preferred network, which means, if this network is not available, a different network is used. If you specify UseOnlyPreferredNetwork and this network is not available, the modem will not dial in.
     * 
     * 
     */
    @JsonProperty("apn")
    @JsonPropertyDescription("APN\r\nurn:x-oca:ocpp:uid:2:233134\r\nCollection of configuration data needed to make a data-connection over a cellular network.\r\n\r\nNOTE: When asking a GSM modem to dial in, it is possible to specify which mobile operator should be used. This can be done with the mobile country code (MCC) in combination with a mobile network code (MNC). Example: If your preferred network is Vodafone Netherlands, the MCC=204 and the MNC=04 which means the key PreferredNetwork = 20404 Some modems allows to specify a preferred network, which means, if this network is not available, a different network is used. If you specify UseOnlyPreferredNetwork and this network is not available, the modem will not dial in.\r\n")
    @Valid
    private APN apn;
    /**
     * Communication_ Function. OCPP_ Version. OCPP_ Version_ Code
     * urn:x-oca:ocpp:uid:1:569355
     * Defines the OCPP version used for this communication function.
     * 
     * (Required)
     * 
     */
    @JsonProperty("ocppVersion")
    @JsonPropertyDescription("Communication_ Function. OCPP_ Version. OCPP_ Version_ Code\r\nurn:x-oca:ocpp:uid:1:569355\r\nDefines the OCPP version used for this communication function.\r\n")
    @NotNull
    private OCPPVersionEnum ocppVersion;
    /**
     * Communication_ Function. OCPP_ Transport. OCPP_ Transport_ Code
     * urn:x-oca:ocpp:uid:1:569356
     * Defines the transport protocol (e.g. SOAP or JSON). Note: SOAP is not supported in OCPP 2.0, but is supported by other versions of OCPP.
     * 
     * (Required)
     * 
     */
    @JsonProperty("ocppTransport")
    @JsonPropertyDescription("Communication_ Function. OCPP_ Transport. OCPP_ Transport_ Code\r\nurn:x-oca:ocpp:uid:1:569356\r\nDefines the transport protocol (e.g. SOAP or JSON). Note: SOAP is not supported in OCPP 2.0, but is supported by other versions of OCPP.\r\n")
    @NotNull
    private OCPPTransportEnum ocppTransport;
    /**
     * Communication_ Function. OCPP_ Central_ System_ URL. URI
     * urn:x-oca:ocpp:uid:1:569357
     * URL of the CSMS(s) that this Charging Station  communicates with.
     * 
     * (Required)
     * 
     */
    @JsonProperty("ocppCsmsUrl")
    @JsonPropertyDescription("Communication_ Function. OCPP_ Central_ System_ URL. URI\r\nurn:x-oca:ocpp:uid:1:569357\r\nURL of the CSMS(s) that this Charging Station  communicates with.\r\n")
    @Size(max = 512)
    @NotNull
    private String ocppCsmsUrl;
    /**
     * Duration in seconds before a message send by the Charging Station via this network connection times-out.
     * The best setting depends on the underlying network and response times of the CSMS.
     * If you are looking for a some guideline: use 30 seconds as a starting point.
     * 
     * (Required)
     * 
     */
    @JsonProperty("messageTimeout")
    @JsonPropertyDescription("Duration in seconds before a message send by the Charging Station via this network connection times-out.\r\nThe best setting depends on the underlying network and response times of the CSMS.\r\nIf you are looking for a some guideline: use 30 seconds as a starting point.\r\n")
    @NotNull
    private Integer messageTimeout;
    /**
     * This field specifies the security profile used when connecting to the CSMS with this NetworkConnectionProfile.
     * 
     * (Required)
     * 
     */
    @JsonProperty("securityProfile")
    @JsonPropertyDescription("This field specifies the security profile used when connecting to the CSMS with this NetworkConnectionProfile.\r\n")
    @NotNull
    private Integer securityProfile;
    /**
     * Applicable Network Interface.
     * 
     * (Required)
     * 
     */
    @JsonProperty("ocppInterface")
    @JsonPropertyDescription("Applicable Network Interface.\r\n")
    @NotNull
    private OCPPInterfaceEnum ocppInterface;
    /**
     * VPN
     * urn:x-oca:ocpp:uid:2:233268
     * VPN Configuration settings
     * 
     * 
     */
    @JsonProperty("vpn")
    @JsonPropertyDescription("VPN\r\nurn:x-oca:ocpp:uid:2:233268\r\nVPN Configuration settings\r\n")
    @Valid
    private VPN vpn;

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

    public NetworkConnectionProfile withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * APN
     * urn:x-oca:ocpp:uid:2:233134
     * Collection of configuration data needed to make a data-connection over a cellular network.
     * 
     * NOTE: When asking a GSM modem to dial in, it is possible to specify which mobile operator should be used. This can be done with the mobile country code (MCC) in combination with a mobile network code (MNC). Example: If your preferred network is Vodafone Netherlands, the MCC=204 and the MNC=04 which means the key PreferredNetwork = 20404 Some modems allows to specify a preferred network, which means, if this network is not available, a different network is used. If you specify UseOnlyPreferredNetwork and this network is not available, the modem will not dial in.
     * 
     * 
     */
    @JsonProperty("apn")
    public APN getApn() {
        return apn;
    }

    /**
     * APN
     * urn:x-oca:ocpp:uid:2:233134
     * Collection of configuration data needed to make a data-connection over a cellular network.
     * 
     * NOTE: When asking a GSM modem to dial in, it is possible to specify which mobile operator should be used. This can be done with the mobile country code (MCC) in combination with a mobile network code (MNC). Example: If your preferred network is Vodafone Netherlands, the MCC=204 and the MNC=04 which means the key PreferredNetwork = 20404 Some modems allows to specify a preferred network, which means, if this network is not available, a different network is used. If you specify UseOnlyPreferredNetwork and this network is not available, the modem will not dial in.
     * 
     * 
     */
    @JsonProperty("apn")
    public void setApn(APN apn) {
        this.apn = apn;
    }

    public NetworkConnectionProfile withApn(APN apn) {
        this.apn = apn;
        return this;
    }

    /**
     * Communication_ Function. OCPP_ Version. OCPP_ Version_ Code
     * urn:x-oca:ocpp:uid:1:569355
     * Defines the OCPP version used for this communication function.
     * 
     * (Required)
     * 
     */
    @JsonProperty("ocppVersion")
    public OCPPVersionEnum getOcppVersion() {
        return ocppVersion;
    }

    /**
     * Communication_ Function. OCPP_ Version. OCPP_ Version_ Code
     * urn:x-oca:ocpp:uid:1:569355
     * Defines the OCPP version used for this communication function.
     * 
     * (Required)
     * 
     */
    @JsonProperty("ocppVersion")
    public void setOcppVersion(OCPPVersionEnum ocppVersion) {
        this.ocppVersion = ocppVersion;
    }

    public NetworkConnectionProfile withOcppVersion(OCPPVersionEnum ocppVersion) {
        this.ocppVersion = ocppVersion;
        return this;
    }

    /**
     * Communication_ Function. OCPP_ Transport. OCPP_ Transport_ Code
     * urn:x-oca:ocpp:uid:1:569356
     * Defines the transport protocol (e.g. SOAP or JSON). Note: SOAP is not supported in OCPP 2.0, but is supported by other versions of OCPP.
     * 
     * (Required)
     * 
     */
    @JsonProperty("ocppTransport")
    public OCPPTransportEnum getOcppTransport() {
        return ocppTransport;
    }

    /**
     * Communication_ Function. OCPP_ Transport. OCPP_ Transport_ Code
     * urn:x-oca:ocpp:uid:1:569356
     * Defines the transport protocol (e.g. SOAP or JSON). Note: SOAP is not supported in OCPP 2.0, but is supported by other versions of OCPP.
     * 
     * (Required)
     * 
     */
    @JsonProperty("ocppTransport")
    public void setOcppTransport(OCPPTransportEnum ocppTransport) {
        this.ocppTransport = ocppTransport;
    }

    public NetworkConnectionProfile withOcppTransport(OCPPTransportEnum ocppTransport) {
        this.ocppTransport = ocppTransport;
        return this;
    }

    /**
     * Communication_ Function. OCPP_ Central_ System_ URL. URI
     * urn:x-oca:ocpp:uid:1:569357
     * URL of the CSMS(s) that this Charging Station  communicates with.
     * 
     * (Required)
     * 
     */
    @JsonProperty("ocppCsmsUrl")
    public String getOcppCsmsUrl() {
        return ocppCsmsUrl;
    }

    /**
     * Communication_ Function. OCPP_ Central_ System_ URL. URI
     * urn:x-oca:ocpp:uid:1:569357
     * URL of the CSMS(s) that this Charging Station  communicates with.
     * 
     * (Required)
     * 
     */
    @JsonProperty("ocppCsmsUrl")
    public void setOcppCsmsUrl(String ocppCsmsUrl) {
        this.ocppCsmsUrl = ocppCsmsUrl;
    }

    public NetworkConnectionProfile withOcppCsmsUrl(String ocppCsmsUrl) {
        this.ocppCsmsUrl = ocppCsmsUrl;
        return this;
    }

    /**
     * Duration in seconds before a message send by the Charging Station via this network connection times-out.
     * The best setting depends on the underlying network and response times of the CSMS.
     * If you are looking for a some guideline: use 30 seconds as a starting point.
     * 
     * (Required)
     * 
     */
    @JsonProperty("messageTimeout")
    public Integer getMessageTimeout() {
        return messageTimeout;
    }

    /**
     * Duration in seconds before a message send by the Charging Station via this network connection times-out.
     * The best setting depends on the underlying network and response times of the CSMS.
     * If you are looking for a some guideline: use 30 seconds as a starting point.
     * 
     * (Required)
     * 
     */
    @JsonProperty("messageTimeout")
    public void setMessageTimeout(Integer messageTimeout) {
        this.messageTimeout = messageTimeout;
    }

    public NetworkConnectionProfile withMessageTimeout(Integer messageTimeout) {
        this.messageTimeout = messageTimeout;
        return this;
    }

    /**
     * This field specifies the security profile used when connecting to the CSMS with this NetworkConnectionProfile.
     * 
     * (Required)
     * 
     */
    @JsonProperty("securityProfile")
    public Integer getSecurityProfile() {
        return securityProfile;
    }

    /**
     * This field specifies the security profile used when connecting to the CSMS with this NetworkConnectionProfile.
     * 
     * (Required)
     * 
     */
    @JsonProperty("securityProfile")
    public void setSecurityProfile(Integer securityProfile) {
        this.securityProfile = securityProfile;
    }

    public NetworkConnectionProfile withSecurityProfile(Integer securityProfile) {
        this.securityProfile = securityProfile;
        return this;
    }

    /**
     * Applicable Network Interface.
     * 
     * (Required)
     * 
     */
    @JsonProperty("ocppInterface")
    public OCPPInterfaceEnum getOcppInterface() {
        return ocppInterface;
    }

    /**
     * Applicable Network Interface.
     * 
     * (Required)
     * 
     */
    @JsonProperty("ocppInterface")
    public void setOcppInterface(OCPPInterfaceEnum ocppInterface) {
        this.ocppInterface = ocppInterface;
    }

    public NetworkConnectionProfile withOcppInterface(OCPPInterfaceEnum ocppInterface) {
        this.ocppInterface = ocppInterface;
        return this;
    }

    /**
     * VPN
     * urn:x-oca:ocpp:uid:2:233268
     * VPN Configuration settings
     * 
     * 
     */
    @JsonProperty("vpn")
    public VPN getVpn() {
        return vpn;
    }

    /**
     * VPN
     * urn:x-oca:ocpp:uid:2:233268
     * VPN Configuration settings
     * 
     * 
     */
    @JsonProperty("vpn")
    public void setVpn(VPN vpn) {
        this.vpn = vpn;
    }

    public NetworkConnectionProfile withVpn(VPN vpn) {
        this.vpn = vpn;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(NetworkConnectionProfile.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("apn");
        sb.append('=');
        sb.append(((this.apn == null)?"<null>":this.apn));
        sb.append(',');
        sb.append("ocppVersion");
        sb.append('=');
        sb.append(((this.ocppVersion == null)?"<null>":this.ocppVersion));
        sb.append(',');
        sb.append("ocppTransport");
        sb.append('=');
        sb.append(((this.ocppTransport == null)?"<null>":this.ocppTransport));
        sb.append(',');
        sb.append("ocppCsmsUrl");
        sb.append('=');
        sb.append(((this.ocppCsmsUrl == null)?"<null>":this.ocppCsmsUrl));
        sb.append(',');
        sb.append("messageTimeout");
        sb.append('=');
        sb.append(((this.messageTimeout == null)?"<null>":this.messageTimeout));
        sb.append(',');
        sb.append("securityProfile");
        sb.append('=');
        sb.append(((this.securityProfile == null)?"<null>":this.securityProfile));
        sb.append(',');
        sb.append("ocppInterface");
        sb.append('=');
        sb.append(((this.ocppInterface == null)?"<null>":this.ocppInterface));
        sb.append(',');
        sb.append("vpn");
        sb.append('=');
        sb.append(((this.vpn == null)?"<null>":this.vpn));
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
        result = ((result* 31)+((this.ocppTransport == null)? 0 :this.ocppTransport.hashCode()));
        result = ((result* 31)+((this.securityProfile == null)? 0 :this.securityProfile.hashCode()));
        result = ((result* 31)+((this.vpn == null)? 0 :this.vpn.hashCode()));
        result = ((result* 31)+((this.ocppVersion == null)? 0 :this.ocppVersion.hashCode()));
        result = ((result* 31)+((this.customData == null)? 0 :this.customData.hashCode()));
        result = ((result* 31)+((this.ocppCsmsUrl == null)? 0 :this.ocppCsmsUrl.hashCode()));
        result = ((result* 31)+((this.apn == null)? 0 :this.apn.hashCode()));
        result = ((result* 31)+((this.messageTimeout == null)? 0 :this.messageTimeout.hashCode()));
        result = ((result* 31)+((this.ocppInterface == null)? 0 :this.ocppInterface.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof NetworkConnectionProfile) == false) {
            return false;
        }
        NetworkConnectionProfile rhs = ((NetworkConnectionProfile) other);
        return ((((((((((this.ocppTransport == rhs.ocppTransport)||((this.ocppTransport!= null)&&this.ocppTransport.equals(rhs.ocppTransport)))&&((this.securityProfile == rhs.securityProfile)||((this.securityProfile!= null)&&this.securityProfile.equals(rhs.securityProfile))))&&((this.vpn == rhs.vpn)||((this.vpn!= null)&&this.vpn.equals(rhs.vpn))))&&((this.ocppVersion == rhs.ocppVersion)||((this.ocppVersion!= null)&&this.ocppVersion.equals(rhs.ocppVersion))))&&((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData))))&&((this.ocppCsmsUrl == rhs.ocppCsmsUrl)||((this.ocppCsmsUrl!= null)&&this.ocppCsmsUrl.equals(rhs.ocppCsmsUrl))))&&((this.apn == rhs.apn)||((this.apn!= null)&&this.apn.equals(rhs.apn))))&&((this.messageTimeout == rhs.messageTimeout)||((this.messageTimeout!= null)&&this.messageTimeout.equals(rhs.messageTimeout))))&&((this.ocppInterface == rhs.ocppInterface)||((this.ocppInterface!= null)&&this.ocppInterface.equals(rhs.ocppInterface))));
    }

}
