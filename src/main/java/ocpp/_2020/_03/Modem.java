
package ocpp._2020._03;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;


/**
 * Wireless_ Communication_ Module
 * urn:x-oca:ocpp:uid:2:233306
 * Defines parameters required for initiating and maintaining wireless communication with other devices.
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "iccid",
    "imsi"
})
public class Modem {

    /**
     * This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.
     * 
     */
    @JsonProperty("customData")
    @JsonPropertyDescription("This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.")
    @Valid
    private CustomData customData;
    /**
     * Wireless_ Communication_ Module. ICCID. CI20_ Text
     * urn:x-oca:ocpp:uid:1:569327
     * This contains the ICCID of the modem’s SIM card.
     * 
     * 
     */
    @JsonProperty("iccid")
    @JsonPropertyDescription("Wireless_ Communication_ Module. ICCID. CI20_ Text\r\nurn:x-oca:ocpp:uid:1:569327\r\nThis contains the ICCID of the modem\u2019s SIM card.\r\n")
    @Size(max = 20)
    private String iccid;
    /**
     * Wireless_ Communication_ Module. IMSI. CI20_ Text
     * urn:x-oca:ocpp:uid:1:569328
     * This contains the IMSI of the modem’s SIM card.
     * 
     * 
     */
    @JsonProperty("imsi")
    @JsonPropertyDescription("Wireless_ Communication_ Module. IMSI. CI20_ Text\r\nurn:x-oca:ocpp:uid:1:569328\r\nThis contains the IMSI of the modem\u2019s SIM card.\r\n")
    @Size(max = 20)
    private String imsi;

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

    public Modem withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * Wireless_ Communication_ Module. ICCID. CI20_ Text
     * urn:x-oca:ocpp:uid:1:569327
     * This contains the ICCID of the modem’s SIM card.
     * 
     * 
     */
    @JsonProperty("iccid")
    public String getIccid() {
        return iccid;
    }

    /**
     * Wireless_ Communication_ Module. ICCID. CI20_ Text
     * urn:x-oca:ocpp:uid:1:569327
     * This contains the ICCID of the modem’s SIM card.
     * 
     * 
     */
    @JsonProperty("iccid")
    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    public Modem withIccid(String iccid) {
        this.iccid = iccid;
        return this;
    }

    /**
     * Wireless_ Communication_ Module. IMSI. CI20_ Text
     * urn:x-oca:ocpp:uid:1:569328
     * This contains the IMSI of the modem’s SIM card.
     * 
     * 
     */
    @JsonProperty("imsi")
    public String getImsi() {
        return imsi;
    }

    /**
     * Wireless_ Communication_ Module. IMSI. CI20_ Text
     * urn:x-oca:ocpp:uid:1:569328
     * This contains the IMSI of the modem’s SIM card.
     * 
     * 
     */
    @JsonProperty("imsi")
    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public Modem withImsi(String imsi) {
        this.imsi = imsi;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Modem.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("iccid");
        sb.append('=');
        sb.append(((this.iccid == null)?"<null>":this.iccid));
        sb.append(',');
        sb.append("imsi");
        sb.append('=');
        sb.append(((this.imsi == null)?"<null>":this.imsi));
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
        result = ((result* 31)+((this.iccid == null)? 0 :this.iccid.hashCode()));
        result = ((result* 31)+((this.imsi == null)? 0 :this.imsi.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Modem) == false) {
            return false;
        }
        Modem rhs = ((Modem) other);
        return ((((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData)))&&((this.iccid == rhs.iccid)||((this.iccid!= null)&&this.iccid.equals(rhs.iccid))))&&((this.imsi == rhs.imsi)||((this.imsi!= null)&&this.imsi.equals(rhs.imsi))));
    }

}
