package de.rwth.idsg.steve.config;

import de.rwth.idsg.steve.ocpp.soap.MediatorInInterceptor;
import de.rwth.idsg.steve.ocpp.soap.MessageIdInterceptor;
import de.rwth.idsg.steve.ocpp.ws.custom.AlwaysLastStrategy;
import de.rwth.idsg.steve.ocpp.ws.custom.RoundRobinStrategy;
import de.rwth.idsg.steve.ocpp.ws.custom.WsSessionSelectStrategy;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
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

import javax.annotation.PostConstruct;
import java.util.List;

import static de.rwth.idsg.steve.SteveConfiguration.CONFIG;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

/**
 * Configuration and beans related to OCPP.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 18.11.2014
 */
@Configuration
public class OcppConfiguration {

    static {
        LogUtils.setLoggerClass(Slf4jLogger.class);
    }

    @Autowired private ocpp.cs._2010._08.CentralSystemService ocpp12Server;
    @Autowired private ocpp.cs._2012._06.CentralSystemService ocpp15Server;
    @Autowired private ocpp.cs._2015._10.CentralSystemService ocpp16Server;

    @Autowired
    @Qualifier("FromAddressInterceptor")
    private PhaseInterceptor<Message> fromAddressInterceptor;

    @PostConstruct
    public void init() {
        List<Interceptor<? extends Message>> route = singletonList(new MediatorInInterceptor());
        List<Interceptor<? extends Message>> interceptors = asList(new MessageIdInterceptor(), fromAddressInterceptor);

        // Just a dummy service to route incoming messages to the appropriate service version
        createOcppService(ocpp12Server, CONFIG.getRouterEndpointPath(), route);

        createOcppService(ocpp12Server, "/CentralSystemServiceOCPP12", interceptors);
        createOcppService(ocpp15Server, "/CentralSystemServiceOCPP15", interceptors);
        createOcppService(ocpp16Server, "/CentralSystemServiceOCPP16", interceptors);
    }

    /**
     * Help by: http://stackoverflow.com/a/31988136
     *
     * logFeature.initialize(springBus) is not needed, because during the init of bus it will call f.initialize(this)
     * in {@link org.apache.cxf.bus.extension.ExtensionManagerBus#initializeFeatures()} anyway
     */
    @Bean(name = Bus.DEFAULT_BUS_ID, destroyMethod = "shutdown")
    public SpringBus springBus() {
        SpringBus bus = new SpringBus();
        bus.getFeatures().add(new LoggingFeature()); // Log incoming/outgoing messages
        return bus;
    }

    @Bean
    public WsSessionSelectStrategy sessionSelectStrategy() {
        switch (CONFIG.getOcpp().getWsSessionSelectStrategy()) {
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
        f.setBus(springBus());
        f.setServiceBean(serviceBean);
        f.setAddress(address);
        f.getInInterceptors().addAll(interceptors);
        f.create();
    }
}
