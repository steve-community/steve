
package ocpp.cp._2015._10;

import de.rwth.idsg.ocpp.jaxb.JodaDateTimeConverter;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.ToString;
import org.joda.time.DateTime;


/**
 * <p>Java class for ChargingProfile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ChargingProfile"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="chargingProfileId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="transactionId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="stackLevel" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="chargingProfilePurpose" type="{urn://Ocpp/Cp/2015/10/}ChargingProfilePurposeType"/&gt;
 *         &lt;element name="chargingProfileKind" type="{urn://Ocpp/Cp/2015/10/}ChargingProfileKindType"/&gt;
 *         &lt;element name="recurrencyKind" type="{urn://Ocpp/Cp/2015/10/}RecurrencyKindType" minOccurs="0"/&gt;
 *         &lt;element name="validFrom" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="validTo" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="chargingSchedule" type="{urn://Ocpp/Cp/2015/10/}ChargingSchedule"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChargingProfile", propOrder = {
    "chargingProfileId",
    "transactionId",
    "stackLevel",
    "chargingProfilePurpose",
    "chargingProfileKind",
    "recurrencyKind",
    "validFrom",
    "validTo",
    "chargingSchedule"
})
@ToString
public class ChargingProfile {

    protected int chargingProfileId;
    protected Integer transactionId;
    protected int stackLevel;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected ChargingProfilePurposeType chargingProfilePurpose;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected ChargingProfileKindType chargingProfileKind;
    @XmlSchemaType(name = "string")
    protected RecurrencyKindType recurrencyKind;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(JodaDateTimeConverter.class)
    @XmlSchemaType(name = "dateTime")
    protected DateTime validFrom;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(JodaDateTimeConverter.class)
    @XmlSchemaType(name = "dateTime")
    protected DateTime validTo;
    @XmlElement(required = true)
    protected ChargingSchedule chargingSchedule;

    /**
     * Gets the value of the chargingProfileId property.
     * 
     */
    public int getChargingProfileId() {
        return chargingProfileId;
    }

    /**
     * Sets the value of the chargingProfileId property.
     * 
     */
    public void setChargingProfileId(int value) {
        this.chargingProfileId = value;
    }

    public boolean isSetChargingProfileId() {
        return true;
    }

    /**
     * Gets the value of the transactionId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTransactionId() {
        return transactionId;
    }

    /**
     * Sets the value of the transactionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTransactionId(Integer value) {
        this.transactionId = value;
    }

    public boolean isSetTransactionId() {
        return (this.transactionId!= null);
    }

    /**
     * Gets the value of the stackLevel property.
     * 
     */
    public int getStackLevel() {
        return stackLevel;
    }

    /**
     * Sets the value of the stackLevel property.
     * 
     */
    public void setStackLevel(int value) {
        this.stackLevel = value;
    }

    public boolean isSetStackLevel() {
        return true;
    }

    /**
     * Gets the value of the chargingProfilePurpose property.
     * 
     * @return
     *     possible object is
     *     {@link ChargingProfilePurposeType }
     *     
     */
    public ChargingProfilePurposeType getChargingProfilePurpose() {
        return chargingProfilePurpose;
    }

    /**
     * Sets the value of the chargingProfilePurpose property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChargingProfilePurposeType }
     *     
     */
    public void setChargingProfilePurpose(ChargingProfilePurposeType value) {
        this.chargingProfilePurpose = value;
    }

    public boolean isSetChargingProfilePurpose() {
        return (this.chargingProfilePurpose!= null);
    }

    /**
     * Gets the value of the chargingProfileKind property.
     * 
     * @return
     *     possible object is
     *     {@link ChargingProfileKindType }
     *     
     */
    public ChargingProfileKindType getChargingProfileKind() {
        return chargingProfileKind;
    }

    /**
     * Sets the value of the chargingProfileKind property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChargingProfileKindType }
     *     
     */
    public void setChargingProfileKind(ChargingProfileKindType value) {
        this.chargingProfileKind = value;
    }

    public boolean isSetChargingProfileKind() {
        return (this.chargingProfileKind!= null);
    }

    /**
     * Gets the value of the recurrencyKind property.
     * 
     * @return
     *     possible object is
     *     {@link RecurrencyKindType }
     *     
     */
    public RecurrencyKindType getRecurrencyKind() {
        return recurrencyKind;
    }

    /**
     * Sets the value of the recurrencyKind property.
     * 
     * @param value
     *     allowed object is
     *     {@link RecurrencyKindType }
     *     
     */
    public void setRecurrencyKind(RecurrencyKindType value) {
        this.recurrencyKind = value;
    }

    public boolean isSetRecurrencyKind() {
        return (this.recurrencyKind!= null);
    }

    /**
     * Gets the value of the validFrom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public DateTime getValidFrom() {
        return validFrom;
    }

    /**
     * Sets the value of the validFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValidFrom(DateTime value) {
        this.validFrom = value;
    }

    public boolean isSetValidFrom() {
        return (this.validFrom!= null);
    }

    /**
     * Gets the value of the validTo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public DateTime getValidTo() {
        return validTo;
    }

    /**
     * Sets the value of the validTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValidTo(DateTime value) {
        this.validTo = value;
    }

    public boolean isSetValidTo() {
        return (this.validTo!= null);
    }

    /**
     * Gets the value of the chargingSchedule property.
     * 
     * @return
     *     possible object is
     *     {@link ChargingSchedule }
     *     
     */
    public ChargingSchedule getChargingSchedule() {
        return chargingSchedule;
    }

    /**
     * Sets the value of the chargingSchedule property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChargingSchedule }
     *     
     */
    public void setChargingSchedule(ChargingSchedule value) {
        this.chargingSchedule = value;
    }

    public boolean isSetChargingSchedule() {
        return (this.chargingSchedule!= null);
    }

    /**
     * Sets the value of the chargingProfileId property.
     * 
     * @param value
     *     allowed object is
     *     int
     * @return
     *     The class instance
     */
    public ChargingProfile withChargingProfileId(int value) {
        setChargingProfileId(value);
        return this;
    }

    /**
     * Sets the value of the transactionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     * @return
     *     The class instance
     */
    public ChargingProfile withTransactionId(Integer value) {
        setTransactionId(value);
        return this;
    }

    /**
     * Sets the value of the stackLevel property.
     * 
     * @param value
     *     allowed object is
     *     int
     * @return
     *     The class instance
     */
    public ChargingProfile withStackLevel(int value) {
        setStackLevel(value);
        return this;
    }

    /**
     * Sets the value of the chargingProfilePurpose property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChargingProfilePurposeType }
     * @return
     *     The class instance
     */
    public ChargingProfile withChargingProfilePurpose(ChargingProfilePurposeType value) {
        setChargingProfilePurpose(value);
        return this;
    }

    /**
     * Sets the value of the chargingProfileKind property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChargingProfileKindType }
     * @return
     *     The class instance
     */
    public ChargingProfile withChargingProfileKind(ChargingProfileKindType value) {
        setChargingProfileKind(value);
        return this;
    }

    /**
     * Sets the value of the recurrencyKind property.
     * 
     * @param value
     *     allowed object is
     *     {@link RecurrencyKindType }
     * @return
     *     The class instance
     */
    public ChargingProfile withRecurrencyKind(RecurrencyKindType value) {
        setRecurrencyKind(value);
        return this;
    }

    /**
     * Sets the value of the validFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateTime }
     * @return
     *     The class instance
     */
    public ChargingProfile withValidFrom(DateTime value) {
        setValidFrom(value);
        return this;
    }

    /**
     * Sets the value of the validTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateTime }
     * @return
     *     The class instance
     */
    public ChargingProfile withValidTo(DateTime value) {
        setValidTo(value);
        return this;
    }

    /**
     * Sets the value of the chargingSchedule property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChargingSchedule }
     * @return
     *     The class instance
     */
    public ChargingProfile withChargingSchedule(ChargingSchedule value) {
        setChargingSchedule(value);
        return this;
    }

}
