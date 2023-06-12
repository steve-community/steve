
package ocpp.cp._2012._06;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import de.rwth.idsg.ocpp.jaxb.ResponseType;
import lombok.ToString;


/**
 * Defines the CancelReservation.conf PDU
 * 
 * <p>Java class for CancelReservationResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CancelReservationResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="status" type="{urn://Ocpp/Cp/2012/06/}CancelReservationStatus"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CancelReservationResponse", propOrder = {
    "status"
})
@ToString
public class CancelReservationResponse
    implements ResponseType
{

    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected CancelReservationStatus status;

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link CancelReservationStatus }
     *     
     */
    public CancelReservationStatus getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link CancelReservationStatus }
     *     
     */
    public void setStatus(CancelReservationStatus value) {
        this.status = value;
    }

    public boolean isSetStatus() {
        return (this.status!= null);
    }

    public CancelReservationResponse withStatus(CancelReservationStatus value) {
        setStatus(value);
        return this;
    }

}
