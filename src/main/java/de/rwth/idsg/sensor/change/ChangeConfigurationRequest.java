
package de.rwth.idsg.sensor.change;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * Defines the amount of time (in seconds) the sensor will wait for the authorization of the user after parking, 
 *          			or for the vehicle to leave the parking spot before it times out.
 * 
 * <p>Java class for ChangeConfigurationRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ChangeConfigurationRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="timeoutValue" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChangeConfigurationRequest", propOrder = {
    "timeoutValue"
})
public class ChangeConfigurationRequest {

    protected int timeoutValue;

    /**
     * Gets the value of the timeoutValue property.
     * 
     */
    public int getTimeoutValue() {
        return timeoutValue;
    }

    /**
     * Sets the value of the timeoutValue property.
     * 
     */
    public void setTimeoutValue(int value) {
        this.timeoutValue = value;
    }

}
