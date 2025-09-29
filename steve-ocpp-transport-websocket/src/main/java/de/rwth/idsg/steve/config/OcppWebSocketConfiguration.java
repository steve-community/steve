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
import de.rwth.idsg.steve.ocpp.ws.OcppWebSocketHandshakeHandler;
import de.rwth.idsg.steve.ocpp.ws.ocpp12.Ocpp12WebSocketEndpoint;
import de.rwth.idsg.steve.ocpp.ws.ocpp15.Ocpp15WebSocketEndpoint;
import de.rwth.idsg.steve.ocpp.ws.ocpp16.Ocpp16WebSocketEndpoint;
import de.rwth.idsg.steve.service.ChargePointRegistrationService;
import de.rwth.idsg.steve.web.validation.ChargeBoxIdValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.jetty.JettyRequestUpgradeStrategy;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.time.Duration;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 11.03.2015
 */
@EnableWebSocket
@Configuration
@Slf4j
@RequiredArgsConstructor
public class OcppWebSocketConfiguration implements WebSocketConfigurer {

    public static final Duration PING_INTERVAL = Duration.ofMinutes(15);
    public static final Duration IDLE_TIMEOUT = Duration.ofHours(2);
    public static final int MAX_MSG_SIZE = 8_388_608; // 8 MB for max message size

    private final ChargePointRegistrationService chargePointRegistrationService;
    private final ChargeBoxIdValidator chargeBoxIdValidator;

    private final Ocpp12WebSocketEndpoint ocpp12WebSocketEndpoint;
    private final Ocpp15WebSocketEndpoint ocpp15WebSocketEndpoint;
    private final Ocpp16WebSocketEndpoint ocpp16WebSocketEndpoint;
    private final SteveProperties steveProperties;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        var pathInfix = steveProperties.getPaths().getWebsocketMapping()
                + steveProperties.getPaths().getRouterEndpointPath() + "/";

        var handshakeHandler = new OcppWebSocketHandshakeHandler(
                chargeBoxIdValidator,
                createHandshakeHandler(),
                Lists.newArrayList(ocpp16WebSocketEndpoint, ocpp15WebSocketEndpoint, ocpp12WebSocketEndpoint),
                chargePointRegistrationService,
                pathInfix);

        registry.addHandler(handshakeHandler.getDummyWebSocketHandler(), pathInfix + "*")
                .setHandshakeHandler(handshakeHandler)
                .setAllowedOrigins("*");
    }

    /**
     * See Spring docs:
     * https://docs.spring.io/spring-framework/reference/web/websocket/server.html#websocket-server-runtime-configurationCheck failure[checkstyle] src/main/java/de/rwth/idsg/steve/config/WebSocketConfiguration.java#L73 <com.puppycrawl.tools.checkstyle.checks.sizes.LineLengthCheck>Check failure: [checkstyle] src/main/java/de/rwth/idsg/steve/config/WebSocketConfiguration.java#L73 <com.puppycrawl.tools.checkstyle.checks.sizes.LineLengthCheck>Line is longer than 120 characters (found 121).build and run tests / checkstyleView detailsCode has alerts. Press enter to view.
     * Otherwise, defaults come from {@link WebSocketConstants}
     */
    private static DefaultHandshakeHandler createHandshakeHandler() {
        var strategy = new JettyRequestUpgradeStrategy();

        strategy.addWebSocketConfigurer(configurable -> {
            configurable.setMaxTextMessageSize(MAX_MSG_SIZE);
            configurable.setIdleTimeout(IDLE_TIMEOUT);
        });

        return new DefaultHandshakeHandler(strategy);
    }
}
