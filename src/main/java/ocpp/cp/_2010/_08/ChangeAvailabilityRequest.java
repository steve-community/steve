
package ocpp.cp._2010._08;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import de.rwth.idsg.ocpp.jaxb.RequestType;
import lombok.ToString;


/**
 * Defines the ChangeAvailability.req PDU
 * 
 * <p>Java class for ChangeAvailabilityRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ChangeAvailabilityRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="connectorId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="type" type="{urn://Ocpp/Cp/2010/08/}AvailabilityType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChangeAvailabilityRequest", propOrder = {
    "connectorId",
    "type"
})
@ToString
public class ChangeAvailabilityRequest
    implements RequestType
{

    protected int connectorId;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected AvailabilityType type;

    /**
     * Gets the value of the connectorId property.
     * 
     */
    public int getConnectorId() {
        return connectorId;
    }

    /**
     * Sets the value of the connectorId property.
     * 
     */
    public void setConnectorId(int value) {
        this.connectorId = value;
    }

    public boolean isSetConnectorId() {
        return true;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link AvailabilityType }
     *     
     */
    public AvailabilityType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link AvailabilityType }
     *     
     */
    public void setType(AvailabilityType value) {
        this.type = value;
    }

    public boolean isSetType() {
        return (this.type!= null);
    }

    public ChangeAvailabilityRequest withConnectorId(int value) {
        setConnectorId(value);
        return this;
    }

    public ChangeAvailabilityRequest withType(AvailabilityType value) {
        setType(value);
        return this;
    }

}
