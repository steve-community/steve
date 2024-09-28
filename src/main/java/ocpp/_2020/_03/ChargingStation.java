
package ocpp._2020._03;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


/**
 * Charge_ Point
 * urn:x-oca:ocpp:uid:2:233122
 * The physical system where an Electrical Vehicle (EV) can be charged.
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "serialNumber",
    "model",
    "modem",
    "vendorName",
    "firmwareVersion"
})
public class ChargingStation {

    /**
     * This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.
     * 
     */
    @JsonProperty("customData")
    @JsonPropertyDescription("This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.")
    @Valid
    private CustomData customData;
    /**
     * Device. Serial_ Number. Serial_ Number
     * urn:x-oca:ocpp:uid:1:569324
     * Vendor-specific device identifier.
     * 
     * 
     */
    @JsonProperty("serialNumber")
    @JsonPropertyDescription("Device. Serial_ Number. Serial_ Number\r\nurn:x-oca:ocpp:uid:1:569324\r\nVendor-specific device identifier.\r\n")
    @Size(max = 25)
    private String serialNumber;
    /**
     * Device. Model. CI20_ Text
     * urn:x-oca:ocpp:uid:1:569325
     * Defines the model of the device.
     * 
     * (Required)
     * 
     */
    @JsonProperty("model")
    @JsonPropertyDescription("Device. Model. CI20_ Text\r\nurn:x-oca:ocpp:uid:1:569325\r\nDefines the model of the device.\r\n")
    @Size(max = 20)
    @NotNull
    private String model;
    /**
     * Wireless_ Communication_ Module
     * urn:x-oca:ocpp:uid:2:233306
     * Defines parameters required for initiating and maintaining wireless communication with other devices.
     * 
     * 
     */
    @JsonProperty("modem")
    @JsonPropertyDescription("Wireless_ Communication_ Module\r\nurn:x-oca:ocpp:uid:2:233306\r\nDefines parameters required for initiating and maintaining wireless communication with other devices.\r\n")
    @Valid
    private Modem modem;
    /**
     * Identifies the vendor (not necessarily in a unique manner).
     * 
     * (Required)
     * 
     */
    @JsonProperty("vendorName")
    @JsonPropertyDescription("Identifies the vendor (not necessarily in a unique manner).\r\n")
    @Size(max = 50)
    @NotNull
    private String vendorName;
    /**
     * This contains the firmware version of the Charging Station.
     * 
     * 
     * 
     */
    @JsonProperty("firmwareVersion")
    @JsonPropertyDescription("This contains the firmware version of the Charging Station.\r\n\r\n")
    @Size(max = 50)
    private String firmwareVersion;

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

    public ChargingStation withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * Device. Serial_ Number. Serial_ Number
     * urn:x-oca:ocpp:uid:1:569324
     * Vendor-specific device identifier.
     * 
     * 
     */
    @JsonProperty("serialNumber")
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Device. Serial_ Number. Serial_ Number
     * urn:x-oca:ocpp:uid:1:569324
     * Vendor-specific device identifier.
     * 
     * 
     */
    @JsonProperty("serialNumber")
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public ChargingStation withSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
        return this;
    }

    /**
     * Device. Model. CI20_ Text
     * urn:x-oca:ocpp:uid:1:569325
     * Defines the model of the device.
     * 
     * (Required)
     * 
     */
    @JsonProperty("model")
    public String getModel() {
        return model;
    }

    /**
     * Device. Model. CI20_ Text
     * urn:x-oca:ocpp:uid:1:569325
     * Defines the model of the device.
     * 
     * (Required)
     * 
     */
    @JsonProperty("model")
    public void setModel(String model) {
        this.model = model;
    }

    public ChargingStation withModel(String model) {
        this.model = model;
        return this;
    }

    /**
     * Wireless_ Communication_ Module
     * urn:x-oca:ocpp:uid:2:233306
     * Defines parameters required for initiating and maintaining wireless communication with other devices.
     * 
     * 
     */
    @JsonProperty("modem")
    public Modem getModem() {
        return modem;
    }

    /**
     * Wireless_ Communication_ Module
     * urn:x-oca:ocpp:uid:2:233306
     * Defines parameters required for initiating and maintaining wireless communication with other devices.
     * 
     * 
     */
    @JsonProperty("modem")
    public void setModem(Modem modem) {
        this.modem = modem;
    }

    public ChargingStation withModem(Modem modem) {
        this.modem = modem;
        return this;
    }

    /**
     * Identifies the vendor (not necessarily in a unique manner).
     * 
     * (Required)
     * 
     */
    @JsonProperty("vendorName")
    public String getVendorName() {
        return vendorName;
    }

    /**
     * Identifies the vendor (not necessarily in a unique manner).
     * 
     * (Required)
     * 
     */
    @JsonProperty("vendorName")
    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public ChargingStation withVendorName(String vendorName) {
        this.vendorName = vendorName;
        return this;
    }

    /**
     * This contains the firmware version of the Charging Station.
     * 
     * 
     * 
     */
    @JsonProperty("firmwareVersion")
    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    /**
     * This contains the firmware version of the Charging Station.
     * 
     * 
     * 
     */
    @JsonProperty("firmwareVersion")
    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public ChargingStation withFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ChargingStation.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("serialNumber");
        sb.append('=');
        sb.append(((this.serialNumber == null)?"<null>":this.serialNumber));
        sb.append(',');
        sb.append("model");
        sb.append('=');
        sb.append(((this.model == null)?"<null>":this.model));
        sb.append(',');
        sb.append("modem");
        sb.append('=');
        sb.append(((this.modem == null)?"<null>":this.modem));
        sb.append(',');
        sb.append("vendorName");
        sb.append('=');
        sb.append(((this.vendorName == null)?"<null>":this.vendorName));
        sb.append(',');
        sb.append("firmwareVersion");
        sb.append('=');
        sb.append(((this.firmwareVersion == null)?"<null>":this.firmwareVersion));
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
        result = ((result* 31)+((this.serialNumber == null)? 0 :this.serialNumber.hashCode()));
        result = ((result* 31)+((this.modem == null)? 0 :this.modem.hashCode()));
        result = ((result* 31)+((this.customData == null)? 0 :this.customData.hashCode()));
        result = ((result* 31)+((this.model == null)? 0 :this.model.hashCode()));
        result = ((result* 31)+((this.vendorName == null)? 0 :this.vendorName.hashCode()));
        result = ((result* 31)+((this.firmwareVersion == null)? 0 :this.firmwareVersion.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ChargingStation) == false) {
            return false;
        }
        ChargingStation rhs = ((ChargingStation) other);
        return (((((((this.serialNumber == rhs.serialNumber)||((this.serialNumber!= null)&&this.serialNumber.equals(rhs.serialNumber)))&&((this.modem == rhs.modem)||((this.modem!= null)&&this.modem.equals(rhs.modem))))&&((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData))))&&((this.model == rhs.model)||((this.model!= null)&&this.model.equals(rhs.model))))&&((this.vendorName == rhs.vendorName)||((this.vendorName!= null)&&this.vendorName.equals(rhs.vendorName))))&&((this.firmwareVersion == rhs.firmwareVersion)||((this.firmwareVersion!= null)&&this.firmwareVersion.equals(rhs.firmwareVersion))));
    }

}
