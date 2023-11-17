
package ocpp.cp._2015._10;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import de.rwth.idsg.ocpp.jaxb.RequestType;
import lombok.ToString;


/**
 * Defines the TriggerMessage.req PDU
 * 
 * <p>Java class for TriggerMessageRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TriggerMessageRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="requestedMessage" type="{urn://Ocpp/Cp/2015/10/}MessageTrigger"/&gt;
 *         &lt;element name="connectorId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TriggerMessageRequest", propOrder = {
    "requestedMessage",
    "connectorId"
})
@ToString
public class TriggerMessageRequest
    implements RequestType
{

    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected MessageTrigger requestedMessage;
    protected Integer connectorId;

    /**
     * Gets the value of the requestedMessage property.
     * 
     * @return
     *     possible object is
     *     {@link MessageTrigger }
     *     
     */
    public MessageTrigger getRequestedMessage() {
        return requestedMessage;
    }

    /**
     * Sets the value of the requestedMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageTrigger }
     *     
     */
    public void setRequestedMessage(MessageTrigger value) {
        this.requestedMessage = value;
    }

    public boolean isSetRequestedMessage() {
        return (this.requestedMessage!= null);
    }

    /**
     * Gets the value of the connectorId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getConnectorId() {
        return connectorId;
    }

    /**
     * Sets the value of the connectorId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setConnectorId(Integer value) {
        this.connectorId = value;
    }

    public boolean isSetConnectorId() {
        return (this.connectorId!= null);
    }

    public TriggerMessageRequest withRequestedMessage(MessageTrigger value) {
        setRequestedMessage(value);
        return this;
    }

    public TriggerMessageRequest withConnectorId(Integer value) {
        setConnectorId(value);
        return this;
    }

}
