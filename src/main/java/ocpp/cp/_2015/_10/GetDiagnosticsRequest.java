
package ocpp.cp._2015._10;

import de.rwth.idsg.ocpp.jaxb.JodaDateTimeConverter;
import de.rwth.idsg.ocpp.jaxb.RequestType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.ToString;
import org.joda.time.DateTime;


/**
 * Defines the GetDiagnostics.req PDU
 * 
 * <p>Java class for GetDiagnosticsRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetDiagnosticsRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="location" type="{http://www.w3.org/2001/XMLSchema}anyURI"/&gt;
 *         &lt;element name="startTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="stopTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="retries" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="retryInterval" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetDiagnosticsRequest", propOrder = {
    "location",
    "startTime",
    "stopTime",
    "retries",
    "retryInterval"
})
@ToString
public class GetDiagnosticsRequest
    implements RequestType
{

    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String location;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(JodaDateTimeConverter.class)
    @XmlSchemaType(name = "dateTime")
    protected DateTime startTime;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(JodaDateTimeConverter.class)
    @XmlSchemaType(name = "dateTime")
    protected DateTime stopTime;
    protected Integer retries;
    protected Integer retryInterval;

    /**
     * Gets the value of the location property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the value of the location property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocation(String value) {
        this.location = value;
    }

    public boolean isSetLocation() {
        return (this.location!= null);
    }

    /**
     * Gets the value of the startTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public DateTime getStartTime() {
        return startTime;
    }

    /**
     * Sets the value of the startTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStartTime(DateTime value) {
        this.startTime = value;
    }

    public boolean isSetStartTime() {
        return (this.startTime!= null);
    }

    /**
     * Gets the value of the stopTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public DateTime getStopTime() {
        return stopTime;
    }

    /**
     * Sets the value of the stopTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStopTime(DateTime value) {
        this.stopTime = value;
    }

    public boolean isSetStopTime() {
        return (this.stopTime!= null);
    }

    /**
     * Gets the value of the retries property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRetries() {
        return retries;
    }

    /**
     * Sets the value of the retries property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRetries(Integer value) {
        this.retries = value;
    }

    public boolean isSetRetries() {
        return (this.retries!= null);
    }

    /**
     * Gets the value of the retryInterval property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRetryInterval() {
        return retryInterval;
    }

    /**
     * Sets the value of the retryInterval property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRetryInterval(Integer value) {
        this.retryInterval = value;
    }

    public boolean isSetRetryInterval() {
        return (this.retryInterval!= null);
    }

    /**
     * Sets the value of the location property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     * @return
     *     The class instance
     */
    public GetDiagnosticsRequest withLocation(String value) {
        setLocation(value);
        return this;
    }

    /**
     * Sets the value of the startTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateTime }
     * @return
     *     The class instance
     */
    public GetDiagnosticsRequest withStartTime(DateTime value) {
        setStartTime(value);
        return this;
    }

    /**
     * Sets the value of the stopTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateTime }
     * @return
     *     The class instance
     */
    public GetDiagnosticsRequest withStopTime(DateTime value) {
        setStopTime(value);
        return this;
    }

    /**
     * Sets the value of the retries property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     * @return
     *     The class instance
     */
    public GetDiagnosticsRequest withRetries(Integer value) {
        setRetries(value);
        return this;
    }

    /**
     * Sets the value of the retryInterval property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     * @return
     *     The class instance
     */
    public GetDiagnosticsRequest withRetryInterval(Integer value) {
        setRetryInterval(value);
        return this;
    }

}
