package ocpp.cp._2015._10;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import javax.xml.namespace.QName;
import jakarta.xml.ws.WebEndpoint;
import jakarta.xml.ws.WebServiceClient;
import jakarta.xml.ws.WebServiceFeature;
import jakarta.xml.ws.Service;

/**
 * The ChargePoint Service for the Open Charge Point Protocol
 *
 * This class was generated by Apache CXF 4.0.5
 * 2024-09-28T11:14:45.651+02:00
 * Generated source version: 4.0.5
 *
 */
@WebServiceClient(name = "ChargePointService",
                  wsdlLocation = "file:/C:/projects/parkl/ocpp-jaxb/src/main/resources/wsdl/OCPP_ChargePointService_1.6.wsdl",
                  targetNamespace = "urn://Ocpp/Cp/2015/10/")
public class ChargePointService_Service extends Service {

    public static final URL WSDL_LOCATION;

    public static final QName SERVICE = new QName("urn://Ocpp/Cp/2015/10/", "ChargePointService");
    public static final QName ChargePointServiceSoap12 = new QName("urn://Ocpp/Cp/2015/10/", "ChargePointServiceSoap12");
    static {
        URL url = null;
        try {
            url = URI.create("file:/C:/projects/parkl/ocpp-jaxb/src/main/resources/wsdl/OCPP_ChargePointService_1.6.wsdl").toURL();
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(ChargePointService_Service.class.getName())
                .log(java.util.logging.Level.INFO,
                     "Can not initialize the default wsdl from {0}", "file:/C:/projects/parkl/ocpp-jaxb/src/main/resources/wsdl/OCPP_ChargePointService_1.6.wsdl");
        }
        WSDL_LOCATION = url;
    }

    public ChargePointService_Service(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public ChargePointService_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public ChargePointService_Service() {
        super(WSDL_LOCATION, SERVICE);
    }

    public ChargePointService_Service(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE, features);
    }

    public ChargePointService_Service(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE, features);
    }

    public ChargePointService_Service(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName, features);
    }




    /**
     *
     * @return
     *     returns ChargePointService
     */
    @WebEndpoint(name = "ChargePointServiceSoap12")
    public ChargePointService getChargePointServiceSoap12() {
        return super.getPort(ChargePointServiceSoap12, ChargePointService.class);
    }

    /**
     *
     * @param features
     *     A list of {@link jakarta.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ChargePointService
     */
    @WebEndpoint(name = "ChargePointServiceSoap12")
    public ChargePointService getChargePointServiceSoap12(WebServiceFeature... features) {
        return super.getPort(ChargePointServiceSoap12, ChargePointService.class, features);
    }

}
