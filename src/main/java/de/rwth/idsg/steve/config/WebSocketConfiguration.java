package de.rwth.idsg.steve.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.ws.OcppWebSocketUpgrader;
import de.rwth.idsg.steve.ocpp.ws.ocpp12.Ocpp12JacksonModule;
import de.rwth.idsg.steve.ocpp.ws.ocpp12.Ocpp12WebSocketEndpoint;
import de.rwth.idsg.steve.ocpp.ws.ocpp15.Ocpp15JacksonModule;
import de.rwth.idsg.steve.ocpp.ws.ocpp15.Ocpp15WebSocketEndpoint;
import de.rwth.idsg.steve.repository.ChargePointRepository;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.WebSocketBehavior;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 11.03.2015
 */
@EnableWebSocket
@Configuration
@Slf4j
public class WebSocketConfiguration implements WebSocketConfigurer {

    @Autowired private Ocpp12WebSocketEndpoint ocpp12WebSocketEndpoint;
    @Autowired private Ocpp15WebSocketEndpoint ocpp15WebSocketEndpoint;
    @Autowired private ChargePointRepository chargePointRepository;

    public final static long IDLE_TIMEOUT = TimeUnit.HOURS.toMillis(2);
    public final static long PING_INTERVAL = 15; // in minutes
    private final static int MAX_MSG_SIZE = 8388608; // 8 MB for max message size

    // The order affects the choice
    private final static String[] PROTOCOLS = {
            OcppVersion.V_15.getValue(),
            OcppVersion.V_12.getValue()
    };

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        WebSocketPolicy policy = new WebSocketPolicy(WebSocketBehavior.SERVER);
        policy.setMaxTextMessageBufferSize(MAX_MSG_SIZE);
        policy.setMaxTextMessageSize(MAX_MSG_SIZE);
        policy.setIdleTimeout(IDLE_TIMEOUT);

        OcppWebSocketUpgrader upgradeStrategy = new OcppWebSocketUpgrader(
                policy, ocpp12WebSocketEndpoint, ocpp15WebSocketEndpoint, chargePointRepository);

        DefaultHandshakeHandler handler = new DefaultHandshakeHandler(upgradeStrategy);
        handler.setSupportedProtocols(PROTOCOLS);

        registry.addHandler(ocpp12WebSocketEndpoint, "/websocket/CentralSystemService/*")
                .addHandler(ocpp15WebSocketEndpoint, "/websocket/CentralSystemService/*")
                .setHandshakeHandler(handler)
                .setAllowedOrigins("*");
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        mapper.registerModule(new Ocpp12JacksonModule());
        mapper.registerModule(new Ocpp15JacksonModule());

        mapper.setAnnotationIntrospector(
                AnnotationIntrospector.pair(
                        new JacksonAnnotationIntrospector(),
                        new JaxbAnnotationIntrospector(mapper.getTypeFactory())
                )
        );
        return mapper;
    }
}
