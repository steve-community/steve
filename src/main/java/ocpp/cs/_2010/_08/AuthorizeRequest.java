
package ocpp.cs._2010._08;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import de.rwth.idsg.ocpp.jaxb.RequestType;
import lombok.ToString;


/**
 * Defines the Authorize.req PDU
 * 
 * <p>Java class for AuthorizeRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AuthorizeRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="idTag" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthorizeRequest", propOrder = {
    "idTag"
})
@ToString
public class AuthorizeRequest
    implements RequestType
{

    @XmlElement(required = true)
    protected String idTag;

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

    public AuthorizeRequest withIdTag(String value) {
        setIdTag(value);
        return this;
    }

}
