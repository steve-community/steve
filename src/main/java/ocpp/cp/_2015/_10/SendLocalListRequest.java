
package ocpp.cp._2015._10;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import de.rwth.idsg.ocpp.jaxb.RequestType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
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
 *         &lt;element name="listVersion" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="localAuthorizationList" type="{urn://Ocpp/Cp/2015/10/}AuthorizationData" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="updateType" type="{urn://Ocpp/Cp/2015/10/}UpdateType"/&gt;
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
    "listVersion",
    "localAuthorizationList",
    "updateType"
})
@ToString
public class SendLocalListRequest
    implements RequestType
{

    protected int listVersion;
    protected List<AuthorizationData> localAuthorizationList;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected UpdateType updateType;

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
     * Gets the value of the localAuthorizationList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the localAuthorizationList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLocalAuthorizationList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AuthorizationData }
     * 
     * 
     */
    public List<AuthorizationData> getLocalAuthorizationList() {
        if (localAuthorizationList == null) {
            localAuthorizationList = new ArrayList<AuthorizationData>();
        }
        return this.localAuthorizationList;
    }

    public boolean isSetLocalAuthorizationList() {
        return ((this.localAuthorizationList!= null)&&(!this.localAuthorizationList.isEmpty()));
    }

    public void unsetLocalAuthorizationList() {
        this.localAuthorizationList = null;
    }

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
     * Sets the value of the listVersion property.
     * 
     * @param value
     *     allowed object is
     *     int
     * @return
     *     The class instance
     */
    public SendLocalListRequest withListVersion(int value) {
        setListVersion(value);
        return this;
    }

    /**
     * Adds objects to the list of LocalAuthorizationList using add method
     * 
     * @param values
     *     objects to add to the list LocalAuthorizationList
     * @return
     *     The class instance
     */
    public SendLocalListRequest withLocalAuthorizationList(AuthorizationData... values) {
        if (values!= null) {
            for (AuthorizationData value: values) {
                getLocalAuthorizationList().add(value);
            }
        }
        return this;
    }

    /**
     * Adds objects to the list of LocalAuthorizationList using addAll method
     * 
     * @param values
     *     objects to add to the list LocalAuthorizationList
     * @return
     *     The class instance
     */
    public SendLocalListRequest withLocalAuthorizationList(Collection<AuthorizationData> values) {
        if (values!= null) {
            getLocalAuthorizationList().addAll(values);
        }
        return this;
    }

    /**
     * Sets the value of the updateType property.
     * 
     * @param value
     *     allowed object is
     *     {@link UpdateType }
     * @return
     *     The class instance
     */
    public SendLocalListRequest withUpdateType(UpdateType value) {
        setUpdateType(value);
        return this;
    }

}
