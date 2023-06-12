
package ocpp.cs._2015._10;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import de.rwth.idsg.ocpp.jaxb.RequestType;
import lombok.ToString;


/**
 * Defines the BootNotification.req PDU
 * 
 * <p>Java class for BootNotificationRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BootNotificationRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="chargePointVendor" type="{urn://Ocpp/Cs/2015/10/}CiString20Type"/&gt;
 *         &lt;element name="chargePointModel" type="{urn://Ocpp/Cs/2015/10/}CiString20Type"/&gt;
 *         &lt;element name="chargePointSerialNumber" type="{urn://Ocpp/Cs/2015/10/}CiString25Type" minOccurs="0"/&gt;
 *         &lt;element name="chargeBoxSerialNumber" type="{urn://Ocpp/Cs/2015/10/}CiString25Type" minOccurs="0"/&gt;
 *         &lt;element name="firmwareVersion" type="{urn://Ocpp/Cs/2015/10/}CiString50Type" minOccurs="0"/&gt;
 *         &lt;element name="iccid" type="{urn://Ocpp/Cs/2015/10/}CiString20Type" minOccurs="0"/&gt;
 *         &lt;element name="imsi" type="{urn://Ocpp/Cs/2015/10/}CiString20Type" minOccurs="0"/&gt;
 *         &lt;element name="meterType" type="{urn://Ocpp/Cs/2015/10/}CiString25Type" minOccurs="0"/&gt;
 *         &lt;element name="meterSerialNumber" type="{urn://Ocpp/Cs/2015/10/}CiString25Type" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BootNotificationRequest", propOrder = {
    "chargePointVendor",
    "chargePointModel",
    "chargePointSerialNumber",
    "chargeBoxSerialNumber",
    "firmwareVersion",
    "iccid",
    "imsi",
    "meterType",
    "meterSerialNumber"
})
@ToString
public class BootNotificationRequest
    implements RequestType
{

    @XmlElement(required = true)
    protected String chargePointVendor;
    @XmlElement(required = true)
    protected String chargePointModel;
    protected String chargePointSerialNumber;
    protected String chargeBoxSerialNumber;
    protected String firmwareVersion;
    protected String iccid;
    protected String imsi;
    protected String meterType;
    protected String meterSerialNumber;

    /**
     * Gets the value of the chargePointVendor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChargePointVendor() {
        return chargePointVendor;
    }

    /**
     * Sets the value of the chargePointVendor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChargePointVendor(String value) {
        this.chargePointVendor = value;
    }

    public boolean isSetChargePointVendor() {
        return (this.chargePointVendor!= null);
    }

    /**
     * Gets the value of the chargePointModel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChargePointModel() {
        return chargePointModel;
    }

    /**
     * Sets the value of the chargePointModel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChargePointModel(String value) {
        this.chargePointModel = value;
    }

    public boolean isSetChargePointModel() {
        return (this.chargePointModel!= null);
    }

    /**
     * Gets the value of the chargePointSerialNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChargePointSerialNumber() {
        return chargePointSerialNumber;
    }

    /**
     * Sets the value of the chargePointSerialNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChargePointSerialNumber(String value) {
        this.chargePointSerialNumber = value;
    }

    public boolean isSetChargePointSerialNumber() {
        return (this.chargePointSerialNumber!= null);
    }

    /**
     * Gets the value of the chargeBoxSerialNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChargeBoxSerialNumber() {
        return chargeBoxSerialNumber;
    }

    /**
     * Sets the value of the chargeBoxSerialNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChargeBoxSerialNumber(String value) {
        this.chargeBoxSerialNumber = value;
    }

    public boolean isSetChargeBoxSerialNumber() {
        return (this.chargeBoxSerialNumber!= null);
    }

    /**
     * Gets the value of the firmwareVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    /**
     * Sets the value of the firmwareVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirmwareVersion(String value) {
        this.firmwareVersion = value;
    }

    public boolean isSetFirmwareVersion() {
        return (this.firmwareVersion!= null);
    }

    /**
     * Gets the value of the iccid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIccid() {
        return iccid;
    }

    /**
     * Sets the value of the iccid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIccid(String value) {
        this.iccid = value;
    }

    public boolean isSetIccid() {
        return (this.iccid!= null);
    }

    /**
     * Gets the value of the imsi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImsi() {
        return imsi;
    }

    /**
     * Sets the value of the imsi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImsi(String value) {
        this.imsi = value;
    }

    public boolean isSetImsi() {
        return (this.imsi!= null);
    }

    /**
     * Gets the value of the meterType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMeterType() {
        return meterType;
    }

    /**
     * Sets the value of the meterType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMeterType(String value) {
        this.meterType = value;
    }

    public boolean isSetMeterType() {
        return (this.meterType!= null);
    }

    /**
     * Gets the value of the meterSerialNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMeterSerialNumber() {
        return meterSerialNumber;
    }

    /**
     * Sets the value of the meterSerialNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMeterSerialNumber(String value) {
        this.meterSerialNumber = value;
    }

    public boolean isSetMeterSerialNumber() {
        return (this.meterSerialNumber!= null);
    }

    public BootNotificationRequest withChargePointVendor(String value) {
        setChargePointVendor(value);
        return this;
    }

    public BootNotificationRequest withChargePointModel(String value) {
        setChargePointModel(value);
        return this;
    }

    public BootNotificationRequest withChargePointSerialNumber(String value) {
        setChargePointSerialNumber(value);
        return this;
    }

    public BootNotificationRequest withChargeBoxSerialNumber(String value) {
        setChargeBoxSerialNumber(value);
        return this;
    }

    public BootNotificationRequest withFirmwareVersion(String value) {
        setFirmwareVersion(value);
        return this;
    }

    public BootNotificationRequest withIccid(String value) {
        setIccid(value);
        return this;
    }

    public BootNotificationRequest withImsi(String value) {
        setImsi(value);
        return this;
    }

    public BootNotificationRequest withMeterType(String value) {
        setMeterType(value);
        return this;
    }

    public BootNotificationRequest withMeterSerialNumber(String value) {
        setMeterSerialNumber(value);
        return this;
    }

}
