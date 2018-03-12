package de.rwth.idsg.steve.config;

import com.google.common.collect.Lists;
import de.rwth.idsg.steve.ocpp.ws.AbstractWebSocketEndpoint;
import de.rwth.idsg.steve.ocpp.ws.OcppWebSocketUpgrader;
import de.rwth.idsg.steve.ocpp.ws.ocpp12.Ocpp12WebSocketEndpoint;
import de.rwth.idsg.steve.ocpp.ws.ocpp15.Ocpp15WebSocketEndpoint;
import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.WebSocketBehavior;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.ServletWebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 11.03.2015
 */
@EnableWebSocket
@Configuration
@Slf4j
public class WebSocketConfiguration implements WebSocketConfigurer {

    @Autowired private ChargePointRepository chargePointRepository;
    @Autowired private NotificationService notificationService;

    @Autowired private Ocpp12WebSocketEndpoint ocpp12WebSocketEndpoint;
    @Autowired private Ocpp15WebSocketEndpoint ocpp15WebSocketEndpoint;

    public static final long PING_INTERVAL = TimeUnit.MINUTES.toMinutes(15);
    private static final long IDLE_TIMEOUT = TimeUnit.HOURS.toMillis(2);
    private static final int MAX_MSG_SIZE = 8_388_608; // 8 MB for max message size

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        WebSocketPolicy policy = new WebSocketPolicy(WebSocketBehavior.SERVER);
        policy.setMaxTextMessageBufferSize(MAX_MSG_SIZE);
        policy.setMaxTextMessageSize(MAX_MSG_SIZE);
        policy.setIdleTimeout(IDLE_TIMEOUT);

        List<AbstractWebSocketEndpoint> endpoints = getEndpoints();
        String[] protocols = endpoints.stream().map(e -> e.getVersion().getValue()).toArray(String[]::new);

        OcppWebSocketUpgrader upgradeStrategy = new OcppWebSocketUpgrader(policy, endpoints, chargePointRepository, notificationService);

        DefaultHandshakeHandler handler = new DefaultHandshakeHandler(upgradeStrategy);
        handler.setSupportedProtocols(protocols);

        for (AbstractWebSocketEndpoint endpoint : endpoints) {
            registry.addHandler(endpoint, "/websocket/CentralSystemService/*")
                    .setHandshakeHandler(handler)
                    .setAllowedOrigins("*");
        }
    }

    /**
     * The order affects the choice!
     */
    private List<AbstractWebSocketEndpoint> getEndpoints() {
        return Lists.newArrayList(ocpp15WebSocketEndpoint, ocpp12WebSocketEndpoint);
    }

    // -------------------------------------------------------------------------
    // We don't need no SockJS fallback. But, nevertheless, Spring initializes
    // a scheduler for it, to be used in the implementation
    // AbstractWebSocketHandlerRegistration.withSockJS(). We don't call
    // WebSocketHandlerRegistry.withSockJS() above to add SockJS support,
    // so the scheduler is useless.
    //
    // Hereby, we override the default beans provided by
    // org.springframework.web.socket.config.annotation.WebSocketConfigurationSupport
    // -------------------------------------------------------------------------

    @Bean
    public HandlerMapping webSocketHandlerMapping() {
        ServletWebSocketHandlerRegistry registry = new ServletWebSocketHandlerRegistry();
        registerWebSocketHandlers(registry);
        return registry.getHandlerMapping();
    }

    @Bean
    public ThreadPoolTaskScheduler defaultSockJsTaskScheduler() {
        return null;
    }
}
