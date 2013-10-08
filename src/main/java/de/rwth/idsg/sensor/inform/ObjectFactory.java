
package de.rwth.idsg.sensor.inform;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the de.rwth.idsg.sensor.inform package. 
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

    private final static QName _ChargeBoxIdentity_QNAME = new QName("urn://de/rwth/idsg/sensor/inform/", "ChargeBoxIdentity");
    private final static QName _InformParkingResponse_QNAME = new QName("urn://de/rwth/idsg/sensor/inform/", "InformParkingResponse");
    private final static QName _InformParkingRequest_QNAME = new QName("urn://de/rwth/idsg/sensor/inform/", "InformParkingRequest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: de.rwth.idsg.sensor.inform
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link InformParkingRequest }
     * 
     */
    public InformParkingRequest createInformParkingRequest() {
        return new InformParkingRequest();
    }

    /**
     * Create an instance of {@link InformParkingResponse }
     * 
     */
    public InformParkingResponse createInformParkingResponse() {
        return new InformParkingResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://de/rwth/idsg/sensor/inform/", name = "ChargeBoxIdentity")
    public JAXBElement<String> createChargeBoxIdentity(String value) {
        return new JAXBElement<String>(_ChargeBoxIdentity_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InformParkingResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://de/rwth/idsg/sensor/inform/", name = "InformParkingResponse")
    public JAXBElement<InformParkingResponse> createInformParkingResponse(InformParkingResponse value) {
        return new JAXBElement<InformParkingResponse>(_InformParkingResponse_QNAME, InformParkingResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InformParkingRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://de/rwth/idsg/sensor/inform/", name = "InformParkingRequest")
    public JAXBElement<InformParkingRequest> createInformParkingRequest(InformParkingRequest value) {
        return new JAXBElement<InformParkingRequest>(_InformParkingRequest_QNAME, InformParkingRequest.class, null, value);
    }

}
