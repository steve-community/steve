
package ocpp.cp._2012._06;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import de.rwth.idsg.ocpp.jaxb.RequestType;
import lombok.ToString;


/**
 * Defines the DataTransfer.req PDU
 * 
 * <p>Java class for DataTransferRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DataTransferRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="vendorId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="messageId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="data" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataTransferRequest", propOrder = {
    "vendorId",
    "messageId",
    "data"
})
@ToString
public class DataTransferRequest
    implements RequestType
{

    @XmlElement(required = true)
    protected String vendorId;
    protected String messageId;
    protected String data;

    /**
     * Gets the value of the vendorId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVendorId() {
        return vendorId;
    }

    /**
     * Sets the value of the vendorId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVendorId(String value) {
        this.vendorId = value;
    }

    public boolean isSetVendorId() {
        return (this.vendorId!= null);
    }

    /**
     * Gets the value of the messageId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Sets the value of the messageId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageId(String value) {
        this.messageId = value;
    }

    public boolean isSetMessageId() {
        return (this.messageId!= null);
    }

    /**
     * Gets the value of the data property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getData() {
        return data;
    }

    /**
     * Sets the value of the data property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setData(String value) {
        this.data = value;
    }

    public boolean isSetData() {
        return (this.data!= null);
    }

    public DataTransferRequest withVendorId(String value) {
        setVendorId(value);
        return this;
    }

    public DataTransferRequest withMessageId(String value) {
        setMessageId(value);
        return this;
    }

    public DataTransferRequest withData(String value) {
        setData(value);
        return this;
    }

}
