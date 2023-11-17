
package ocpp.cp._2015._10;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import de.rwth.idsg.ocpp.jaxb.ResponseType;
import lombok.ToString;


/**
 * Defines the SetChargingProfile.conf PDU
 * 
 * <p>Java class for SetChargingProfileResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SetChargingProfileResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="status" type="{urn://Ocpp/Cp/2015/10/}ChargingProfileStatus"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SetChargingProfileResponse", propOrder = {
    "status"
})
@ToString
public class SetChargingProfileResponse
    implements ResponseType
{

    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected ChargingProfileStatus status;

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link ChargingProfileStatus }
     *     
     */
    public ChargingProfileStatus getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChargingProfileStatus }
     *     
     */
    public void setStatus(ChargingProfileStatus value) {
        this.status = value;
    }

    public boolean isSetStatus() {
        return (this.status!= null);
    }

    public SetChargingProfileResponse withStatus(ChargingProfileStatus value) {
        setStatus(value);
        return this;
    }

}
