
package de.rwth.idsg.sensor.change;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * Informs whether the request has been processed successfully or not
 * 
 * <p>Java class for ChangeConfigurationResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ChangeConfigurationResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="configChanged" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChangeConfigurationResponse", propOrder = {
    "configChanged"
})
public class ChangeConfigurationResponse {

    protected boolean configChanged;

    /**
     * Gets the value of the configChanged property.
     * 
     */
    public boolean isConfigChanged() {
        return configChanged;
    }

    /**
     * Sets the value of the configChanged property.
     * 
     */
    public void setConfigChanged(boolean value) {
        this.configChanged = value;
    }

}
