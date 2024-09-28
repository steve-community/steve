package ocpp.cs._2010._08;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import javax.xml.namespace.QName;
import jakarta.xml.ws.WebEndpoint;
import jakarta.xml.ws.WebServiceClient;
import jakarta.xml.ws.WebServiceFeature;
import jakarta.xml.ws.Service;

/**
 * The Central System Service for the Open Charge Point Protocol
 *
 * This class was generated by Apache CXF 4.0.5
 * 2024-09-28T11:14:44.774+02:00
 * Generated source version: 4.0.5
 *
 */
@WebServiceClient(name = "CentralSystemService",
                  wsdlLocation = "file:/C:/projects/parkl/ocpp-jaxb/src/main/resources/wsdl/centralsystemservice_0.wsdl",
                  targetNamespace = "urn://Ocpp/Cs/2010/08/")
public class CentralSystemService_Service extends Service {

    public static final URL WSDL_LOCATION;

    public static final QName SERVICE = new QName("urn://Ocpp/Cs/2010/08/", "CentralSystemService");
    public static final QName CentralSystemServiceSoap12 = new QName("urn://Ocpp/Cs/2010/08/", "CentralSystemServiceSoap12");
    static {
        URL url = null;
        try {
            url = URI.create("file:/C:/projects/parkl/ocpp-jaxb/src/main/resources/wsdl/centralsystemservice_0.wsdl").toURL();
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(CentralSystemService_Service.class.getName())
                .log(java.util.logging.Level.INFO,
                     "Can not initialize the default wsdl from {0}", "file:/C:/projects/parkl/ocpp-jaxb/src/main/resources/wsdl/centralsystemservice_0.wsdl");
        }
        WSDL_LOCATION = url;
    }

    public CentralSystemService_Service(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public CentralSystemService_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public CentralSystemService_Service() {
        super(WSDL_LOCATION, SERVICE);
    }

    public CentralSystemService_Service(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE, features);
    }

    public CentralSystemService_Service(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE, features);
    }

    public CentralSystemService_Service(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName, features);
    }




    /**
     *
     * @return
     *     returns CentralSystemService
     */
    @WebEndpoint(name = "CentralSystemServiceSoap12")
    public CentralSystemService getCentralSystemServiceSoap12() {
        return super.getPort(CentralSystemServiceSoap12, CentralSystemService.class);
    }

    /**
     *
     * @param features
     *     A list of {@link jakarta.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns CentralSystemService
     */
    @WebEndpoint(name = "CentralSystemServiceSoap12")
    public CentralSystemService getCentralSystemServiceSoap12(WebServiceFeature... features) {
        return super.getPort(CentralSystemServiceSoap12, CentralSystemService.class, features);
    }

}
