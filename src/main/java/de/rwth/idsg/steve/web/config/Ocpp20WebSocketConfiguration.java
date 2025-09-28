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
package de.rwth.idsg.steve.web.config;

import de.rwth.idsg.steve.ocpp20.ws.Ocpp20WebSocketEndpoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.jetty.JettyRequestUpgradeStrategy;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.time.Duration;

@Slf4j
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ocpp.v20.enabled", havingValue = "true")
public class Ocpp20WebSocketConfiguration implements WebSocketConfigurer {

    private final Ocpp20WebSocketEndpoint ocpp20Endpoint;

    public static final String OCPP20_PATH = "/ocpp/v20/*";
    public static final Duration IDLE_TIMEOUT = Duration.ofHours(2);
    public static final int MAX_MSG_SIZE = 8_388_608;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        log.info("Registering OCPP 2.0 WebSocket endpoint at: {}", OCPP20_PATH);

        registry.addHandler(ocpp20Endpoint, OCPP20_PATH)
                .setHandshakeHandler(createHandshakeHandler())
                .setAllowedOrigins("*");
    }

    private DefaultHandshakeHandler createHandshakeHandler() {
        JettyRequestUpgradeStrategy strategy = new JettyRequestUpgradeStrategy();

        strategy.addWebSocketConfigurer(configurable -> {
            configurable.setMaxTextMessageSize(MAX_MSG_SIZE);
            configurable.setIdleTimeout(IDLE_TIMEOUT);
        });

        return new DefaultHandshakeHandler(strategy);
    }
}