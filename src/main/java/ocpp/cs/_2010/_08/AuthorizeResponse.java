
package ocpp.cs._2010._08;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import de.rwth.idsg.ocpp.jaxb.ResponseType;
import lombok.ToString;


/**
 * Defines the Authorize.conf PDU
 * 
 * <p>Java class for AuthorizeResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AuthorizeResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="idTagInfo" type="{urn://Ocpp/Cs/2010/08/}IdTagInfo"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthorizeResponse", propOrder = {
    "idTagInfo"
})
@ToString
public class AuthorizeResponse
    implements ResponseType
{

    @XmlElement(required = true)
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

    public AuthorizeResponse withIdTagInfo(IdTagInfo value) {
        setIdTagInfo(value);
        return this;
    }

}
