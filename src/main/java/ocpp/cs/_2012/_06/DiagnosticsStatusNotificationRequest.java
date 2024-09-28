
package ocpp.cs._2012._06;

import de.rwth.idsg.ocpp.jaxb.RequestType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import lombok.ToString;


/**
 * Defines the DiagnosticsStatusNotification.req PDU
 * 
 * <p>Java class for DiagnosticsStatusNotificationRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DiagnosticsStatusNotificationRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="status" type="{urn://Ocpp/Cs/2012/06/}DiagnosticsStatus"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DiagnosticsStatusNotificationRequest", propOrder = {
    "status"
})
@ToString
public class DiagnosticsStatusNotificationRequest
    implements RequestType
{

    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected DiagnosticsStatus status;

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link DiagnosticsStatus }
     *     
     */
    public DiagnosticsStatus getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link DiagnosticsStatus }
     *     
     */
    public void setStatus(DiagnosticsStatus value) {
        this.status = value;
    }

    public boolean isSetStatus() {
        return (this.status!= null);
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link DiagnosticsStatus }
     * @return
     *     The class instance
     */
    public DiagnosticsStatusNotificationRequest withStatus(DiagnosticsStatus value) {
        setStatus(value);
        return this;
    }

}
