
package ocpp.cs._2010._08;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import de.rwth.idsg.ocpp.jaxb.JodaDateTimeConverter;
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
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
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
    "value"
})
@ToString
public class MeterValue {

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(JodaDateTimeConverter.class)
    @XmlSchemaType(name = "dateTime")
    protected DateTime timestamp;
    protected int value;

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
     * Gets the value of the value property.
     * 
     */
    public int getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     */
    public void setValue(int value) {
        this.value = value;
    }

    public boolean isSetValue() {
        return true;
    }

    public MeterValue withTimestamp(DateTime value) {
        setTimestamp(value);
        return this;
    }

    public MeterValue withValue(int value) {
        setValue(value);
        return this;
    }

}
