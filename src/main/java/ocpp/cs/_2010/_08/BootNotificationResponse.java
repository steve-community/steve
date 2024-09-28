
package ocpp.cs._2010._08;

import de.rwth.idsg.ocpp.jaxb.JodaDateTimeConverter;
import de.rwth.idsg.ocpp.jaxb.ResponseType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.ToString;
import org.joda.time.DateTime;


/**
 * Defines the BootNotification.conf PDU
 * 
 * <p>Java class for BootNotificationResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BootNotificationResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="status" type="{urn://Ocpp/Cs/2010/08/}RegistrationStatus"/&gt;
 *         &lt;element name="currentTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="heartbeatInterval" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BootNotificationResponse", propOrder = {
    "status",
    "currentTime",
    "heartbeatInterval"
})
@ToString
public class BootNotificationResponse
    implements ResponseType
{

    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected RegistrationStatus status;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(JodaDateTimeConverter.class)
    @XmlSchemaType(name = "dateTime")
    protected DateTime currentTime;
    protected Integer heartbeatInterval;

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link RegistrationStatus }
     *     
     */
    public RegistrationStatus getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link RegistrationStatus }
     *     
     */
    public void setStatus(RegistrationStatus value) {
        this.status = value;
    }

    public boolean isSetStatus() {
        return (this.status!= null);
    }

    /**
     * Gets the value of the currentTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public DateTime getCurrentTime() {
        return currentTime;
    }

    /**
     * Sets the value of the currentTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCurrentTime(DateTime value) {
        this.currentTime = value;
    }

    public boolean isSetCurrentTime() {
        return (this.currentTime!= null);
    }

    /**
     * Gets the value of the heartbeatInterval property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getHeartbeatInterval() {
        return heartbeatInterval;
    }

    /**
     * Sets the value of the heartbeatInterval property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setHeartbeatInterval(Integer value) {
        this.heartbeatInterval = value;
    }

    public boolean isSetHeartbeatInterval() {
        return (this.heartbeatInterval!= null);
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link RegistrationStatus }
     * @return
     *     The class instance
     */
    public BootNotificationResponse withStatus(RegistrationStatus value) {
        setStatus(value);
        return this;
    }

    /**
     * Sets the value of the currentTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateTime }
     * @return
     *     The class instance
     */
    public BootNotificationResponse withCurrentTime(DateTime value) {
        setCurrentTime(value);
        return this;
    }

    /**
     * Sets the value of the heartbeatInterval property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     * @return
     *     The class instance
     */
    public BootNotificationResponse withHeartbeatInterval(Integer value) {
        setHeartbeatInterval(value);
        return this;
    }

}
