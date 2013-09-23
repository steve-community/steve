
package de.rwth.idsg.sensor.change;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the de.rwth.idsg.sensor.change package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ChangeStatusRequest_QNAME = new QName("urn://de/rwth/idsg/sensor/change", "ChangeStatusRequest");
    private final static QName _ChangeStatusResponse_QNAME = new QName("urn://de/rwth/idsg/sensor/change", "ChangeStatusResponse");
    private final static QName _ChangeConfigurationRequest_QNAME = new QName("urn://de/rwth/idsg/sensor/change", "ChangeConfigurationRequest");
    private final static QName _ChargeBoxIdentity_QNAME = new QName("urn://de/rwth/idsg/sensor/change", "ChargeBoxIdentity");
    private final static QName _ChangeConfigurationResponse_QNAME = new QName("urn://de/rwth/idsg/sensor/change", "ChangeConfigurationResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: de.rwth.idsg.sensor.change
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ChangeConfigurationResponse }
     * 
     */
    public ChangeConfigurationResponse createChangeConfigurationResponse() {
        return new ChangeConfigurationResponse();
    }

    /**
     * Create an instance of {@link ChangeStatusRequest }
     * 
     */
    public ChangeStatusRequest createChangeStatusRequest() {
        return new ChangeStatusRequest();
    }

    /**
     * Create an instance of {@link ChangeConfigurationRequest }
     * 
     */
    public ChangeConfigurationRequest createChangeConfigurationRequest() {
        return new ChangeConfigurationRequest();
    }

    /**
     * Create an instance of {@link ChangeStatusResponse }
     * 
     */
    public ChangeStatusResponse createChangeStatusResponse() {
        return new ChangeStatusResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ChangeStatusRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://de/rwth/idsg/sensor/change", name = "ChangeStatusRequest")
    public JAXBElement<ChangeStatusRequest> createChangeStatusRequest(ChangeStatusRequest value) {
        return new JAXBElement<ChangeStatusRequest>(_ChangeStatusRequest_QNAME, ChangeStatusRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ChangeStatusResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://de/rwth/idsg/sensor/change", name = "ChangeStatusResponse")
    public JAXBElement<ChangeStatusResponse> createChangeStatusResponse(ChangeStatusResponse value) {
        return new JAXBElement<ChangeStatusResponse>(_ChangeStatusResponse_QNAME, ChangeStatusResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ChangeConfigurationRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://de/rwth/idsg/sensor/change", name = "ChangeConfigurationRequest")
    public JAXBElement<ChangeConfigurationRequest> createChangeConfigurationRequest(ChangeConfigurationRequest value) {
        return new JAXBElement<ChangeConfigurationRequest>(_ChangeConfigurationRequest_QNAME, ChangeConfigurationRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://de/rwth/idsg/sensor/change", name = "ChargeBoxIdentity")
    public JAXBElement<String> createChargeBoxIdentity(String value) {
        return new JAXBElement<String>(_ChargeBoxIdentity_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ChangeConfigurationResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://de/rwth/idsg/sensor/change", name = "ChangeConfigurationResponse")
    public JAXBElement<ChangeConfigurationResponse> createChangeConfigurationResponse(ChangeConfigurationResponse value) {
        return new JAXBElement<ChangeConfigurationResponse>(_ChangeConfigurationResponse_QNAME, ChangeConfigurationResponse.class, null, value);
    }

}
