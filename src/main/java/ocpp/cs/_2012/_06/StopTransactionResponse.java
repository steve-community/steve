
package ocpp.cs._2012._06;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import de.rwth.idsg.ocpp.jaxb.ResponseType;
import lombok.ToString;


/**
 * Defines the StopTransaction.conf PDU
 * 
 * <p>Java class for StopTransactionResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StopTransactionResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="idTagInfo" type="{urn://Ocpp/Cs/2012/06/}IdTagInfo" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StopTransactionResponse", propOrder = {
    "idTagInfo"
})
@ToString
public class StopTransactionResponse
    implements ResponseType
{

    protected IdTagInfo idTagInfo;

    /**
     * Gets the value of the idTagInfo property.
     * 
     * @return
     *     possible object is
     *     {@link IdTagInfo }
     *     
     */
    public IdTagInfo getIdTagInfo() {
        return idTagInfo;
    }

    /**
     * Sets the value of the idTagInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdTagInfo }
     *     
     */
    public void setIdTagInfo(IdTagInfo value) {
        this.idTagInfo = value;
    }

    public boolean isSetIdTagInfo() {
        return (this.idTagInfo!= null);
    }

    public StopTransactionResponse withIdTagInfo(IdTagInfo value) {
        setIdTagInfo(value);
        return this;
    }

}
