package de.rwth.idsg.steve.config;

import de.rwth.idsg.steve.SteveConfiguration;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.soap.MediatorInInterceptor;
import de.rwth.idsg.steve.ocpp.soap.MessageIdInterceptor;
import de.rwth.idsg.steve.ocpp.ws.custom.AlwaysLastStrategy;
import de.rwth.idsg.steve.ocpp.ws.custom.RoundRobinStrategy;
import de.rwth.idsg.steve.ocpp.ws.custom.WsSessionSelectStrategy;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptor;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.xml.ws.soap.SOAPBinding;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration and beans related to OCPP clients/services.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 18.11.2014
 */
@Configuration
public class OcppConfiguration {

    @Autowired private ocpp.cs._2010._08.CentralSystemService ocpp12Server;
    @Autowired private ocpp.cs._2012._06.CentralSystemService ocpp15Server;

    @Autowired
    @Qualifier("FromAddressInterceptor")
    private PhaseInterceptor<Message> fromAddressInterceptor;

    @PostConstruct
    public void init() {
        List<Interceptor<? extends Message>> interceptors = new ArrayList<>();
        interceptors.add(new MessageIdInterceptor());
        interceptors.add(fromAddressInterceptor);

        createRouterService();
        createOcpp12Service(interceptors);
        createOcpp15Service(interceptors);
    }

    @Bean
    public WsSessionSelectStrategy sessionSelectStrategy() {
        switch (SteveConfiguration.Ocpp.WS_SESSION_SELECT_STRATEGY) {
            case ALWAYS_LAST:
                return new AlwaysLastStrategy();
            case ROUND_ROBIN:
                return new RoundRobinStrategy();
            default:
                throw new RuntimeException("Could not find a valid WsSessionSelectStrategy");
        }
    }

    @Bean
    @Qualifier("ocpp12")
    public JaxWsProxyFactoryBean ocpp12ClientFactory() {
        JaxWsProxyFactoryBean f = new JaxWsProxyFactoryBean();
        f.setBindingId(SOAPBinding.SOAP12HTTP_BINDING);
        f.getFeatures().add(new WSAddressingFeature());
        f.setServiceClass(ocpp.cp._2010._08.ChargePointService.class);
        f.setProperties(ocppMap(OcppVersion.V_12));
        return f;
    }

    @Bean
    @Qualifier("ocpp15")
    public JaxWsProxyFactoryBean ocpp15ClientFactory() {
        JaxWsProxyFactoryBean f = new JaxWsProxyFactoryBean();
        f.setBindingId(SOAPBinding.SOAP12HTTP_BINDING);
        f.getFeatures().add(new WSAddressingFeature());
        f.setServiceClass(ocpp.cp._2012._06.ChargePointService.class);
        f.setProperties(ocppMap(OcppVersion.V_15));
        return f;
    }

    /**
     * Just a dummy service to route incoming messages to the appropriate service version.
     */
    private void createRouterService() {
        JaxWsServerFactoryBean f = new JaxWsServerFactoryBean();
        f.setServiceBean(ocpp12Server);
        f.setAddress("/CentralSystemService");
        f.getInInterceptors().add(new MediatorInInterceptor());
        f.create();
    }

    private void createOcpp12Service(List<Interceptor<? extends Message>> interceptors) {
        JaxWsServerFactoryBean f = new JaxWsServerFactoryBean();
        f.setServiceBean(ocpp12Server);
        f.setAddress("/CentralSystemServiceOCPP12");
        f.getInInterceptors().addAll(interceptors);
        f.setProperties(ocppServiceMap(OcppVersion.V_12));
        f.create();
    }

    private void createOcpp15Service(List<Interceptor<? extends Message>> interceptors) {
        JaxWsServerFactoryBean f = new JaxWsServerFactoryBean();
        f.setServiceBean(ocpp15Server);
        f.setAddress("/CentralSystemServiceOCPP15");
        f.getInInterceptors().addAll(interceptors);
        f.setProperties(ocppServiceMap(OcppVersion.V_15));
        f.create();
    }

    /**
     * There might be charging stations who expect predefined namespace prefixes,
     * and don't want to play with the default namespace/prefix handling of our beloved CXF.
     *
     * Therefore, we customize namespace prefixes in client requests.
     * Same approach can also be used to modify service responses.
     */
    private Map<String, Object> ocppMap(OcppVersion ocppVersion) {
        Map<String, String> nameSpaceMap = new HashMap<>();
        nameSpaceMap.put("soapenv", "http://www.w3.org/2003/05/soap-envelope");
        nameSpaceMap.put("wsa", "http://www.w3.org/2005/08/addressing");

        switch (ocppVersion) {
            case V_12:
                nameSpaceMap.put("ns1", "urn://Ocpp/Cp/2010/08/");
                break;

            case V_15:
                nameSpaceMap.put("ns1", "urn://Ocpp/Cp/2012/06/");
                break;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("soap.env.ns.map", nameSpaceMap);
        map.put("disable.outputstream.optimization", true);
        return map;
    }

    private Map<String, Object> ocppServiceMap(OcppVersion ocppVersion) {
        Map<String, String> nameSpaceMap = new HashMap<>();
        nameSpaceMap.put("soapenv", "http://www.w3.org/2003/05/soap-envelope");
        nameSpaceMap.put("wsa", "http://www.w3.org/2005/08/addressing");

        switch (ocppVersion) {
            case V_12:
                nameSpaceMap.put("ns1", "urn://Ocpp/Cs/2010/08/");
                break;

            case V_15:
                nameSpaceMap.put("ns1", "urn://Ocpp/Cs/2012/06/");
                break;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("soap.env.ns.map", nameSpaceMap);
        map.put("disable.outputstream.optimization", true);
        return map;
    }
}
