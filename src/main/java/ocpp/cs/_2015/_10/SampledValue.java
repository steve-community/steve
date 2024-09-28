
package ocpp.cs._2015._10;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import lombok.ToString;


/**
 * Defines the SampledValue-value
 * 
 * <p>Java class for SampledValue complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SampledValue"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="context" type="{urn://Ocpp/Cs/2015/10/}ReadingContext" minOccurs="0"/&gt;
 *         &lt;element name="format" type="{urn://Ocpp/Cs/2015/10/}ValueFormat" minOccurs="0"/&gt;
 *         &lt;element name="measurand" type="{urn://Ocpp/Cs/2015/10/}Measurand" minOccurs="0"/&gt;
 *         &lt;element name="phase" type="{urn://Ocpp/Cs/2015/10/}Phase" minOccurs="0"/&gt;
 *         &lt;element name="location" type="{urn://Ocpp/Cs/2015/10/}Location" minOccurs="0"/&gt;
 *         &lt;element name="unit" type="{urn://Ocpp/Cs/2015/10/}UnitOfMeasure" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SampledValue", propOrder = {
    "value",
    "context",
    "format",
    "measurand",
    "phase",
    "location",
    "unit"
})
@ToString
public class SampledValue {

    @XmlElement(required = true)
    protected String value;
    @XmlSchemaType(name = "string")
    protected ReadingContext context;
    @XmlSchemaType(name = "string")
    protected ValueFormat format;
    @XmlSchemaType(name = "string")
    protected Measurand measurand;
    @XmlSchemaType(name = "string")
    protected Phase phase;
    @XmlSchemaType(name = "string")
    protected Location location;
    @XmlSchemaType(name = "string")
    protected UnitOfMeasure unit;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    public boolean isSetValue() {
        return (this.value!= null);
    }

    /**
     * Gets the value of the context property.
     * 
     * @return
     *     possible object is
     *     {@link ReadingContext }
     *     
     */
    public ReadingContext getContext() {
        return context;
    }

    /**
     * Sets the value of the context property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReadingContext }
     *     
     */
    public void setContext(ReadingContext value) {
        this.context = value;
    }

    public boolean isSetContext() {
        return (this.context!= null);
    }

    /**
     * Gets the value of the format property.
     * 
     * @return
     *     possible object is
     *     {@link ValueFormat }
     *     
     */
    public ValueFormat getFormat() {
        return format;
    }

    /**
     * Sets the value of the format property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValueFormat }
     *     
     */
    public void setFormat(ValueFormat value) {
        this.format = value;
    }

    public boolean isSetFormat() {
        return (this.format!= null);
    }

    /**
     * Gets the value of the measurand property.
     * 
     * @return
     *     possible object is
     *     {@link Measurand }
     *     
     */
    public Measurand getMeasurand() {
        return measurand;
    }

    /**
     * Sets the value of the measurand property.
     * 
     * @param value
     *     allowed object is
     *     {@link Measurand }
     *     
     */
    public void setMeasurand(Measurand value) {
        this.measurand = value;
    }

    public boolean isSetMeasurand() {
        return (this.measurand!= null);
    }

    /**
     * Gets the value of the phase property.
     * 
     * @return
     *     possible object is
     *     {@link Phase }
     *     
     */
    public Phase getPhase() {
        return phase;
    }

    /**
     * Sets the value of the phase property.
     * 
     * @param value
     *     allowed object is
     *     {@link Phase }
     *     
     */
    public void setPhase(Phase value) {
        this.phase = value;
    }

    public boolean isSetPhase() {
        return (this.phase!= null);
    }

    /**
     * Gets the value of the location property.
     * 
     * @return
     *     possible object is
     *     {@link Location }
     *     
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Sets the value of the location property.
     * 
     * @param value
     *     allowed object is
     *     {@link Location }
     *     
     */
    public void setLocation(Location value) {
        this.location = value;
    }

    public boolean isSetLocation() {
        return (this.location!= null);
    }

    /**
     * Gets the value of the unit property.
     * 
     * @return
     *     possible object is
     *     {@link UnitOfMeasure }
     *     
     */
    public UnitOfMeasure getUnit() {
        return unit;
    }

    /**
     * Sets the value of the unit property.
     * 
     * @param value
     *     allowed object is
     *     {@link UnitOfMeasure }
     *     
     */
    public void setUnit(UnitOfMeasure value) {
        this.unit = value;
    }

    public boolean isSetUnit() {
        return (this.unit!= null);
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     * @return
     *     The class instance
     */
    public SampledValue withValue(String value) {
        setValue(value);
        return this;
    }

    /**
     * Sets the value of the context property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReadingContext }
     * @return
     *     The class instance
     */
    public SampledValue withContext(ReadingContext value) {
        setContext(value);
        return this;
    }

    /**
     * Sets the value of the format property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValueFormat }
     * @return
     *     The class instance
     */
    public SampledValue withFormat(ValueFormat value) {
        setFormat(value);
        return this;
    }

    /**
     * Sets the value of the measurand property.
     * 
     * @param value
     *     allowed object is
     *     {@link Measurand }
     * @return
     *     The class instance
     */
    public SampledValue withMeasurand(Measurand value) {
        setMeasurand(value);
        return this;
    }

    /**
     * Sets the value of the phase property.
     * 
     * @param value
     *     allowed object is
     *     {@link Phase }
     * @return
     *     The class instance
     */
    public SampledValue withPhase(Phase value) {
        setPhase(value);
        return this;
    }

    /**
     * Sets the value of the location property.
     * 
     * @param value
     *     allowed object is
     *     {@link Location }
     * @return
     *     The class instance
     */
    public SampledValue withLocation(Location value) {
        setLocation(value);
        return this;
    }

    /**
     * Sets the value of the unit property.
     * 
     * @param value
     *     allowed object is
     *     {@link UnitOfMeasure }
     * @return
     *     The class instance
     */
    public SampledValue withUnit(UnitOfMeasure value) {
        setUnit(value);
        return this;
    }

}
