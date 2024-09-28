
package ocpp.cp._2012._06;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import de.rwth.idsg.ocpp.jaxb.RequestType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import lombok.ToString;


/**
 * Defines the GetConfiguration.req PDU
 * 
 * <p>Java class for GetConfigurationRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetConfigurationRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="key" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetConfigurationRequest", propOrder = {
    "key"
})
@ToString
public class GetConfigurationRequest
    implements RequestType
{

    protected List<String> key;

    /**
     * Gets the value of the key property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the key property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getKey().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getKey() {
        if (key == null) {
            key = new ArrayList<String>();
        }
        return this.key;
    }

    public boolean isSetKey() {
        return ((this.key!= null)&&(!this.key.isEmpty()));
    }

    public void unsetKey() {
        this.key = null;
    }

    /**
     * Adds objects to the list of Key using add method
     * 
     * @param values
     *     objects to add to the list Key
     * @return
     *     The class instance
     */
    public GetConfigurationRequest withKey(String... values) {
        if (values!= null) {
            for (String value: values) {
                getKey().add(value);
            }
        }
        return this;
    }

    /**
     * Adds objects to the list of Key using addAll method
     * 
     * @param values
     *     objects to add to the list Key
     * @return
     *     The class instance
     */
    public GetConfigurationRequest withKey(Collection<String> values) {
        if (values!= null) {
            getKey().addAll(values);
        }
        return this;
    }

}
