
package ocpp.cp._2012._06;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * Defines the GetLocalListVersion.conf PDU
 * 
 * <p>Java class for GetLocalListVersionResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetLocalListVersionResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="listVersion" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetLocalListVersionResponse", propOrder = {
    "listVersion"
})
public class GetLocalListVersionResponse {

    protected int listVersion;

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

}
