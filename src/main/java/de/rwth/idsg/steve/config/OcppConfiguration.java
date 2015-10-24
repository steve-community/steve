package de.rwth.idsg.steve.config;

import de.rwth.idsg.steve.SteveConfiguration;
import de.rwth.idsg.steve.ocpp.soap.MediatorInInterceptor;
import de.rwth.idsg.steve.ocpp.soap.MessageIdInterceptor;
import de.rwth.idsg.steve.ocpp.ws.custom.AlwaysLastStrategy;
import de.rwth.idsg.steve.ocpp.ws.custom.RoundRobinStrategy;
import de.rwth.idsg.steve.ocpp.ws.custom.WsSessionSelectStrategy;
import org.apache.cxf.Bus;
import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.common.logging.Slf4jLogger;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuration and beans related to OCPP.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 18.11.2014
 */
@Configuration
@ImportResource({"classpath:META-INF/cxf/cxf.xml"})
public class OcppConfiguration {

    static {
        LogUtils.setLoggerClass(Slf4jLogger.class);
    }

    @Autowired private Bus bus;
    @Autowired private ocpp.cs._2010._08.CentralSystemService ocpp12Server;
    @Autowired private ocpp.cs._2012._06.CentralSystemService ocpp15Server;

    @Autowired
    @Qualifier("FromAddressInterceptor")
    private PhaseInterceptor<Message> fromAddressInterceptor;

    @PostConstruct
    public void init() {
        configureBus();

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

    private void configureBus() {
        List<Feature> list = new ArrayList<>();
        list.add(new LoggingFeature()); // Log incoming/outgoing messages
        bus.setFeatures(list);
    }

    /**
     * Just a dummy service to route incoming messages to the appropriate service version.
     */
    private void createRouterService() {
        JaxWsServerFactoryBean f = new JaxWsServerFactoryBean();
        f.setBus(bus);
        f.setServiceBean(ocpp12Server);
        f.setAddress(SteveConfiguration.ROUTER_ENDPOINT_PATH);
        f.getInInterceptors().add(new MediatorInInterceptor());
        f.create();
    }

    private void createOcpp12Service(List<Interceptor<? extends Message>> interceptors) {
        JaxWsServerFactoryBean f = new JaxWsServerFactoryBean();
        f.setBus(bus);
        f.setServiceBean(ocpp12Server);
        f.setAddress("/CentralSystemServiceOCPP12");
        f.getInInterceptors().addAll(interceptors);
        f.create();
    }

    private void createOcpp15Service(List<Interceptor<? extends Message>> interceptors) {
        JaxWsServerFactoryBean f = new JaxWsServerFactoryBean();
        f.setBus(bus);
        f.setServiceBean(ocpp15Server);
        f.setAddress("/CentralSystemServiceOCPP15");
        f.getInInterceptors().addAll(interceptors);
        f.create();
    }
}
