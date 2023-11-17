
package ocpp.cp._2015._10;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import de.rwth.idsg.ocpp.jaxb.JodaDateTimeConverter;
import de.rwth.idsg.ocpp.jaxb.RequestType;
import lombok.ToString;
import org.joda.time.DateTime;


/**
 * Defines the ReserveNow.req PDU
 * 
 * <p>Java class for ReserveNowRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReserveNowRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="connectorId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="expiryDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="idTag" type="{urn://Ocpp/Cp/2015/10/}IdToken"/&gt;
 *         &lt;element name="parentIdTag" type="{urn://Ocpp/Cp/2015/10/}IdToken" minOccurs="0"/&gt;
 *         &lt;element name="reservationId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReserveNowRequest", propOrder = {
    "connectorId",
    "expiryDate",
    "idTag",
    "parentIdTag",
    "reservationId"
})
@ToString
public class ReserveNowRequest
    implements RequestType
{

    protected int connectorId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(JodaDateTimeConverter.class)
    @XmlSchemaType(name = "dateTime")
    protected DateTime expiryDate;
    @XmlElement(required = true)
    protected String idTag;
    protected String parentIdTag;
    protected int reservationId;

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
     * Gets the value of the expiryDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public DateTime getExpiryDate() {
        return expiryDate;
    }

    /**
     * Sets the value of the expiryDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpiryDate(DateTime value) {
        this.expiryDate = value;
    }

    public boolean isSetExpiryDate() {
        return (this.expiryDate!= null);
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
     * Gets the value of the parentIdTag property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParentIdTag() {
        return parentIdTag;
    }

    /**
     * Sets the value of the parentIdTag property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentIdTag(String value) {
        this.parentIdTag = value;
    }

    public boolean isSetParentIdTag() {
        return (this.parentIdTag!= null);
    }

    /**
     * Gets the value of the reservationId property.
     * 
     */
    public int getReservationId() {
        return reservationId;
    }

    /**
     * Sets the value of the reservationId property.
     * 
     */
    public void setReservationId(int value) {
        this.reservationId = value;
    }

    public boolean isSetReservationId() {
        return true;
    }

    public ReserveNowRequest withConnectorId(int value) {
        setConnectorId(value);
        return this;
    }

    public ReserveNowRequest withExpiryDate(DateTime value) {
        setExpiryDate(value);
        return this;
    }

    public ReserveNowRequest withIdTag(String value) {
        setIdTag(value);
        return this;
    }

    public ReserveNowRequest withParentIdTag(String value) {
        setParentIdTag(value);
        return this;
    }

    public ReserveNowRequest withReservationId(int value) {
        setReservationId(value);
        return this;
    }

}
