/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2025 SteVe Community Team
 * All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.rwth.idsg.steve.config;

import com.google.common.collect.Lists;
import de.rwth.idsg.steve.ocpp.ws.AbstractWebSocketEndpoint;
import de.rwth.idsg.steve.ocpp.ws.OcppWebSocketHandshakeHandler;
import de.rwth.idsg.steve.ocpp.ws.ocpp12.Ocpp12WebSocketEndpoint;
import de.rwth.idsg.steve.ocpp.ws.ocpp15.Ocpp15WebSocketEndpoint;
import de.rwth.idsg.steve.ocpp.ws.ocpp16.Ocpp16WebSocketEndpoint;
import de.rwth.idsg.steve.service.CertificateValidator;
import de.rwth.idsg.steve.service.ChargePointService;
import de.rwth.idsg.steve.web.validation.ChargeBoxIdValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.core.WebSocketConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.jetty.JettyRequestUpgradeStrategy;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.time.Duration;
import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 11.03.2015
 */
@RequiredArgsConstructor
@EnableWebSocket
@Configuration
@Slf4j
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final ChargePointService chargePointService;
    private final ChargeBoxIdValidator chargeBoxIdValidator;
    private final List<AbstractWebSocketEndpoint> endpoints;
    private final CertificateValidator certificateValidator;

    public static final String PATH_INFIX = "/websocket/CentralSystemService/";
    public static final Duration PING_INTERVAL = Duration.ofMinutes(15);
    public static final Duration IDLE_TIMEOUT = Duration.ofHours(2);
    public static final int MAX_MSG_SIZE = 8_388_608; // 8 MB for max message size

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        OcppWebSocketHandshakeHandler handshakeHandler = new OcppWebSocketHandshakeHandler(
            chargeBoxIdValidator,
            handshakeHandler(),
            endpoints,
            chargePointService,
            certificateValidator
        );

        registry.addHandler(handshakeHandler.getDummyWebSocketHandler(), PATH_INFIX + "*")
                .setHandshakeHandler(handshakeHandler)
                .setAllowedOrigins("*");
    }

    /**
     * https://docs.spring.io/spring-framework/reference/web/websocket/server.html#websocket-server-runtime-configuration
     *
     * Otherwise, defaults come from {@link WebSocketConstants}
     */
    @Bean
    public DefaultHandshakeHandler handshakeHandler() {
        JettyRequestUpgradeStrategy strategy = new JettyRequestUpgradeStrategy();

        strategy.addWebSocketConfigurer(configurable -> {
            configurable.setMaxTextMessageSize(MAX_MSG_SIZE);
            configurable.setIdleTimeout(IDLE_TIMEOUT);
        });

        return new DefaultHandshakeHandler(strategy);
    }
}
