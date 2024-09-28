
package ocpp.cp._2015._10;

import de.rwth.idsg.ocpp.jaxb.RequestType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.ToString;


/**
 * Defines the RemoteStartTransaction.req PDU
 * 
 * <p>Java class for RemoteStartTransactionRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RemoteStartTransactionRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="connectorId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="idTag" type="{urn://Ocpp/Cp/2015/10/}IdToken"/&gt;
 *         &lt;element name="chargingProfile" type="{urn://Ocpp/Cp/2015/10/}ChargingProfile" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RemoteStartTransactionRequest", propOrder = {
    "connectorId",
    "idTag",
    "chargingProfile"
})
@ToString
public class RemoteStartTransactionRequest
    implements RequestType
{

    protected Integer connectorId;
    @XmlElement(required = true)
    protected String idTag;
    protected ChargingProfile chargingProfile;

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

    /**
     * Gets the value of the idTag property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdTag() {
        return idTag;
    }

    /**
     * Sets the value of the idTag property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdTag(String value) {
        this.idTag = value;
    }

    public boolean isSetIdTag() {
        return (this.idTag!= null);
    }

    /**
     * Gets the value of the chargingProfile property.
     * 
     * @return
     *     possible object is
     *     {@link ChargingProfile }
     *     
     */
    public ChargingProfile getChargingProfile() {
        return chargingProfile;
    }

    /**
     * Sets the value of the chargingProfile property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChargingProfile }
     *     
     */
    public void setChargingProfile(ChargingProfile value) {
        this.chargingProfile = value;
    }

    public boolean isSetChargingProfile() {
        return (this.chargingProfile!= null);
    }

    /**
     * Sets the value of the connectorId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     * @return
     *     The class instance
     */
    public RemoteStartTransactionRequest withConnectorId(Integer value) {
        setConnectorId(value);
        return this;
    }

    /**
     * Sets the value of the idTag property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     * @return
     *     The class instance
     */
    public RemoteStartTransactionRequest withIdTag(String value) {
        setIdTag(value);
        return this;
    }

    /**
     * Sets the value of the chargingProfile property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChargingProfile }
     * @return
     *     The class instance
     */
    public RemoteStartTransactionRequest withChargingProfile(ChargingProfile value) {
        setChargingProfile(value);
        return this;
    }

}
