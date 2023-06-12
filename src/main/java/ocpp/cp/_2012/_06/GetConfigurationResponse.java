
package ocpp.cp._2012._06;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import de.rwth.idsg.ocpp.jaxb.ResponseType;
import lombok.ToString;


/**
 * Defines the GetConfiguration.req PDU
 * 
 * <p>Java class for GetConfigurationResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetConfigurationResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="configurationKey" type="{urn://Ocpp/Cp/2012/06/}KeyValue" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="unknownKey" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetConfigurationResponse", propOrder = {
    "configurationKey",
    "unknownKey"
})
@ToString
public class GetConfigurationResponse
    implements ResponseType
{

    protected List<KeyValue> configurationKey;
    protected List<String> unknownKey;

    /**
     * Gets the value of the configurationKey property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the configurationKey property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConfigurationKey().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link KeyValue }
     * 
     * 
     */
    public List<KeyValue> getConfigurationKey() {
        if (configurationKey == null) {
            configurationKey = new ArrayList<KeyValue>();
        }
        return this.configurationKey;
    }

    public boolean isSetConfigurationKey() {
        return ((this.configurationKey!= null)&&(!this.configurationKey.isEmpty()));
    }

    public void unsetConfigurationKey() {
        this.configurationKey = null;
    }

    /**
     * Gets the value of the unknownKey property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the unknownKey property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUnknownKey().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getUnknownKey() {
        if (unknownKey == null) {
            unknownKey = new ArrayList<String>();
        }
        return this.unknownKey;
    }

    public boolean isSetUnknownKey() {
        return ((this.unknownKey!= null)&&(!this.unknownKey.isEmpty()));
    }

    public void unsetUnknownKey() {
        this.unknownKey = null;
    }

    public GetConfigurationResponse withConfigurationKey(KeyValue... values) {
        if (values!= null) {
            for (KeyValue value: values) {
                getConfigurationKey().add(value);
            }
        }
        return this;
    }

    public GetConfigurationResponse withConfigurationKey(Collection<KeyValue> values) {
        if (values!= null) {
            getConfigurationKey().addAll(values);
        }
        return this;
    }

    public GetConfigurationResponse withUnknownKey(String... values) {
        if (values!= null) {
            for (String value: values) {
                getUnknownKey().add(value);
            }
        }
        return this;
    }

    public GetConfigurationResponse withUnknownKey(Collection<String> values) {
        if (values!= null) {
            getUnknownKey().addAll(values);
        }
        return this;
    }

}
