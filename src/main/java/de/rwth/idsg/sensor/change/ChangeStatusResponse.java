
package de.rwth.idsg.sensor.change;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * Informs whether the request has been processed successfully or not
 * 
 * <p>Java class for ChangeStatusResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ChangeStatusResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="statusChanged" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChangeStatusResponse", propOrder = {
    "statusChanged"
})
public class ChangeStatusResponse {

    protected boolean statusChanged;

    /**
     * Gets the value of the statusChanged property.
     * 
     */
    public boolean isStatusChanged() {
        return statusChanged;
    }

    /**
     * Sets the value of the statusChanged property.
     * 
     */
    public void setStatusChanged(boolean value) {
        this.statusChanged = value;
    }

}
