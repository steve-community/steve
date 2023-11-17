
package ocpp.cp._2012._06;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import lombok.ToString;


/**
 * <p>Java class for AuthorisationData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AuthorisationData"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="idTag" type="{urn://Ocpp/Cp/2012/06/}IdToken"/&gt;
 *         &lt;element name="idTagInfo" type="{urn://Ocpp/Cp/2012/06/}IdTagInfo" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthorisationData", propOrder = {
    "idTag",
    "idTagInfo"
})
@ToString
public class AuthorisationData {

    @XmlElement(required = true)
    protected String idTag;
    protected IdTagInfo idTagInfo;

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

    public AuthorisationData withIdTag(String value) {
        setIdTag(value);
        return this;
    }

    public AuthorisationData withIdTagInfo(IdTagInfo value) {
        setIdTagInfo(value);
        return this;
    }

}
