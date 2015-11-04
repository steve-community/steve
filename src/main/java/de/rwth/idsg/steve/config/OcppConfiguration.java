package de.rwth.idsg.steve.config;

import de.rwth.idsg.steve.ocpp.soap.MediatorInInterceptor;
import de.rwth.idsg.steve.ocpp.soap.MessageIdInterceptor;
import de.rwth.idsg.steve.ocpp.ws.custom.AlwaysLastStrategy;
import de.rwth.idsg.steve.ocpp.ws.custom.RoundRobinStrategy;
import de.rwth.idsg.steve.ocpp.ws.custom.WsSessionSelectStrategy;
import org.apache.cxf.Bus;
import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.common.logging.Slf4jLogger;
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
import java.util.List;

import static de.rwth.idsg.steve.SteveConfiguration.Ocpp.WS_SESSION_SELECT_STRATEGY;
import static de.rwth.idsg.steve.SteveConfiguration.ROUTER_ENDPOINT_PATH;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

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
        // Log incoming/outgoing messages
        bus.getFeatures().add(new LoggingFeature());

        List<Interceptor<? extends Message>> route = singletonList(new MediatorInInterceptor());
        List<Interceptor<? extends Message>> interceptors = asList(new MessageIdInterceptor(), fromAddressInterceptor);

        // Just a dummy service to route incoming messages to the appropriate service version
        createOcppService(ocpp12Server, ROUTER_ENDPOINT_PATH, route);

        createOcppService(ocpp12Server, "/CentralSystemServiceOCPP12", interceptors);
        createOcppService(ocpp15Server, "/CentralSystemServiceOCPP15", interceptors);
    }

    @Bean
    public WsSessionSelectStrategy sessionSelectStrategy() {
        switch (WS_SESSION_SELECT_STRATEGY) {
            case ALWAYS_LAST:
                return new AlwaysLastStrategy();
            case ROUND_ROBIN:
                return new RoundRobinStrategy();
            default:
                throw new RuntimeException("Could not find a valid WsSessionSelectStrategy");
        }
    }

    private void createOcppService(Object serviceBean, String address,
                                   List<Interceptor<? extends Message>> interceptors) {
        JaxWsServerFactoryBean f = new JaxWsServerFactoryBean();
        f.setBus(bus);
        f.setServiceBean(serviceBean);
        f.setAddress(address);
        f.getInInterceptors().addAll(interceptors);
        f.create();
    }
}
