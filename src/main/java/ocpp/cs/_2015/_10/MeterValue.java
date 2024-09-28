
package ocpp.cs._2015._10;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
 * Defines single value of the meter-value-value
 * 
 * <p>Java class for MeterValue complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MeterValue"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="sampledValue" type="{urn://Ocpp/Cs/2015/10/}SampledValue" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MeterValue", propOrder = {
    "timestamp",
    "sampledValue"
})
@ToString
public class MeterValue {

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(JodaDateTimeConverter.class)
    @XmlSchemaType(name = "dateTime")
    protected DateTime timestamp;
    @XmlElement(required = true)
    protected List<SampledValue> sampledValue;

    /**
     * Gets the value of the timestamp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public DateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimestamp(DateTime value) {
        this.timestamp = value;
    }

    public boolean isSetTimestamp() {
        return (this.timestamp!= null);
    }

    /**
     * Gets the value of the sampledValue property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the sampledValue property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSampledValue().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SampledValue }
     * 
     * 
     */
    public List<SampledValue> getSampledValue() {
        if (sampledValue == null) {
            sampledValue = new ArrayList<SampledValue>();
        }
        return this.sampledValue;
    }

    public boolean isSetSampledValue() {
        return ((this.sampledValue!= null)&&(!this.sampledValue.isEmpty()));
    }

    public void unsetSampledValue() {
        this.sampledValue = null;
    }

    /**
     * Sets the value of the timestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateTime }
     * @return
     *     The class instance
     */
    public MeterValue withTimestamp(DateTime value) {
        setTimestamp(value);
        return this;
    }

    /**
     * Adds objects to the list of SampledValue using add method
     * 
     * @param values
     *     objects to add to the list SampledValue
     * @return
     *     The class instance
     */
    public MeterValue withSampledValue(SampledValue... values) {
        if (values!= null) {
            for (SampledValue value: values) {
                getSampledValue().add(value);
            }
        }
        return this;
    }

    /**
     * Adds objects to the list of SampledValue using addAll method
     * 
     * @param values
     *     objects to add to the list SampledValue
     * @return
     *     The class instance
     */
    public MeterValue withSampledValue(Collection<SampledValue> values) {
        if (values!= null) {
            getSampledValue().addAll(values);
        }
        return this;
    }

}
