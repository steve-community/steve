
package ocpp.cs._2012._06;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Defines single value of the meter-value-value
 * 
 * <p>Java class for MeterValue complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MeterValue">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="value" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                 &lt;attribute name="context" type="{urn://Ocpp/Cs/2012/06/}ReadingContext" />
 *                 &lt;attribute name="format" type="{urn://Ocpp/Cs/2012/06/}ValueFormat" />
 *                 &lt;attribute name="measurand" type="{urn://Ocpp/Cs/2012/06/}Measurand" />
 *                 &lt;attribute name="location" type="{urn://Ocpp/Cs/2012/06/}Location" />
 *                 &lt;attribute name="unit" type="{urn://Ocpp/Cs/2012/06/}UnitOfMeasure" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MeterValue", propOrder = {
    "timestamp",
    "value"
})
public class MeterValue {

    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar timestamp;
    @XmlElement(required = true)
    protected List<MeterValue.Value> value;

    /**
     * Gets the value of the timestamp property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTimestamp(XMLGregorianCalendar value) {
        this.timestamp = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the value property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getValue().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MeterValue.Value }
     * 
     * 
     */
    public List<MeterValue.Value> getValue() {
        if (value == null) {
            value = new ArrayList<MeterValue.Value>();
        }
        return this.value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *       &lt;attribute name="context" type="{urn://Ocpp/Cs/2012/06/}ReadingContext" />
     *       &lt;attribute name="format" type="{urn://Ocpp/Cs/2012/06/}ValueFormat" />
     *       &lt;attribute name="measurand" type="{urn://Ocpp/Cs/2012/06/}Measurand" />
     *       &lt;attribute name="location" type="{urn://Ocpp/Cs/2012/06/}Location" />
     *       &lt;attribute name="unit" type="{urn://Ocpp/Cs/2012/06/}UnitOfMeasure" />
     *     &lt;/extension>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class Value {

        @XmlValue
        protected String value;
        @XmlAttribute(name = "context")
        protected ReadingContext context;
        @XmlAttribute(name = "format")
        protected ValueFormat format;
        @XmlAttribute(name = "measurand")
        protected Measurand measurand;
        @XmlAttribute(name = "location")
        protected Location location;
        @XmlAttribute(name = "unit")
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

    }

}
