
package ocpp.cp._2012._06;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import de.rwth.idsg.ocpp.jaxb.RequestType;
import lombok.ToString;


/**
 * Defines the SendLocalList.req PDU
 * 
 * <p>Java class for SendLocalListRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SendLocalListRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="updateType" type="{urn://Ocpp/Cp/2012/06/}UpdateType"/&gt;
 *         &lt;element name="listVersion" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="localAuthorisationList" type="{urn://Ocpp/Cp/2012/06/}AuthorisationData" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="hash" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SendLocalListRequest", propOrder = {
    "updateType",
    "listVersion",
    "localAuthorisationList",
    "hash"
})
@ToString
public class SendLocalListRequest
    implements RequestType
{

    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected UpdateType updateType;
    protected int listVersion;
    protected List<AuthorisationData> localAuthorisationList;
    protected String hash;

    /**
     * Gets the value of the updateType property.
     * 
     * @return
     *     possible object is
     *     {@link UpdateType }
     *     
     */
    public UpdateType getUpdateType() {
        return updateType;
    }

    /**
     * Sets the value of the updateType property.
     * 
     * @param value
     *     allowed object is
     *     {@link UpdateType }
     *     
     */
    public void setUpdateType(UpdateType value) {
        this.updateType = value;
    }

    public boolean isSetUpdateType() {
        return (this.updateType!= null);
    }

    /**
     * Gets the value of the listVersion property.
     * 
     */
    public int getListVersion() {
        return listVersion;
    }

    /**
     * Sets the value of the listVersion property.
     * 
     */
    public void setListVersion(int value) {
        this.listVersion = value;
    }

    public boolean isSetListVersion() {
        return true;
    }

    /**
     * Gets the value of the localAuthorisationList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the localAuthorisationList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLocalAuthorisationList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AuthorisationData }
     * 
     * 
     */
    public List<AuthorisationData> getLocalAuthorisationList() {
        if (localAuthorisationList == null) {
            localAuthorisationList = new ArrayList<AuthorisationData>();
        }
        return this.localAuthorisationList;
    }

    public boolean isSetLocalAuthorisationList() {
        return ((this.localAuthorisationList!= null)&&(!this.localAuthorisationList.isEmpty()));
    }

    public void unsetLocalAuthorisationList() {
        this.localAuthorisationList = null;
    }

    /**
     * Gets the value of the hash property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHash() {
        return hash;
    }

    /**
     * Sets the value of the hash property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHash(String value) {
        this.hash = value;
    }

    public boolean isSetHash() {
        return (this.hash!= null);
    }

    public SendLocalListRequest withUpdateType(UpdateType value) {
        setUpdateType(value);
        return this;
    }

    public SendLocalListRequest withListVersion(int value) {
        setListVersion(value);
        return this;
    }

    public SendLocalListRequest withLocalAuthorisationList(AuthorisationData... values) {
        if (values!= null) {
            for (AuthorisationData value: values) {
                getLocalAuthorisationList().add(value);
            }
        }
        return this;
    }

    public SendLocalListRequest withLocalAuthorisationList(Collection<AuthorisationData> values) {
        if (values!= null) {
            getLocalAuthorisationList().addAll(values);
        }
        return this;
    }

    public SendLocalListRequest withHash(String value) {
        setHash(value);
        return this;
    }

}
