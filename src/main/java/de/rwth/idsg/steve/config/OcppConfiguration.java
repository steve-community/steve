package de.rwth.idsg.steve.config;

import de.rwth.idsg.steve.SteveConfiguration;
import de.rwth.idsg.steve.ocpp.soap.MediatorInInterceptor;
import de.rwth.idsg.steve.ocpp.soap.MessageIdInterceptor;
import de.rwth.idsg.steve.ocpp.ws.custom.AlwaysLastStrategy;
import de.rwth.idsg.steve.ocpp.ws.custom.RoundRobinStrategy;
import de.rwth.idsg.steve.ocpp.ws.custom.WsSessionSelectStrategy;
import org.apache.cxf.binding.soap.SoapMessage;
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
import java.util.List;

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
    private PhaseInterceptor<SoapMessage> fromAddressInterceptor;

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
        return f;
    }

    @Bean
    @Qualifier("ocpp15")
    public JaxWsProxyFactoryBean ocpp15ClientFactory() {
        JaxWsProxyFactoryBean f = new JaxWsProxyFactoryBean();
        f.setBindingId(SOAPBinding.SOAP12HTTP_BINDING);
        f.getFeatures().add(new WSAddressingFeature());
        f.setServiceClass(ocpp.cp._2012._06.ChargePointService.class);
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
        f.create();
    }

    private void createOcpp15Service(List<Interceptor<? extends Message>> interceptors) {
        JaxWsServerFactoryBean f = new JaxWsServerFactoryBean();
        f.setServiceBean(ocpp15Server);
        f.setAddress("/CentralSystemServiceOCPP15");
        f.getInInterceptors().addAll(interceptors);
        f.create();
    }
}
