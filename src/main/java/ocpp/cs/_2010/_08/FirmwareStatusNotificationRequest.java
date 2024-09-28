
package ocpp.cs._2010._08;

import de.rwth.idsg.ocpp.jaxb.RequestType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import lombok.ToString;


/**
 * Defines the FirmwareStatusNotification.req PDU
 * 
 * <p>Java class for FirmwareStatusNotificationRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FirmwareStatusNotificationRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="status" type="{urn://Ocpp/Cs/2010/08/}FirmwareStatus"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FirmwareStatusNotificationRequest", propOrder = {
    "status"
})
@ToString
public class FirmwareStatusNotificationRequest
    implements RequestType
{

    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected FirmwareStatus status;

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link FirmwareStatus }
     *     
     */
    public FirmwareStatus getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link FirmwareStatus }
     *     
     */
    public void setStatus(FirmwareStatus value) {
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
     *     {@link FirmwareStatus }
     * @return
     *     The class instance
     */
    public FirmwareStatusNotificationRequest withStatus(FirmwareStatus value) {
        setStatus(value);
        return this;
    }

}
