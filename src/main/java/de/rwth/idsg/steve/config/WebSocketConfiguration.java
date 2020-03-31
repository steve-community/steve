/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2020 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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
import de.rwth.idsg.steve.ocpp.ws.OcppWebSocketUpgrader;
import de.rwth.idsg.steve.ocpp.ws.ocpp12.Ocpp12WebSocketEndpoint;
import de.rwth.idsg.steve.ocpp.ws.ocpp15.Ocpp15WebSocketEndpoint;
import de.rwth.idsg.steve.ocpp.ws.ocpp16.Ocpp16WebSocketEndpoint;
import de.rwth.idsg.steve.service.ChargePointHelperService;
import de.rwth.idsg.steve.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.WebSocketBehavior;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
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

    @Autowired private NotificationService notificationService;
    @Autowired private ChargePointHelperService chargePointHelperService;

    @Autowired private Ocpp12WebSocketEndpoint ocpp12WebSocketEndpoint;
    @Autowired private Ocpp15WebSocketEndpoint ocpp15WebSocketEndpoint;
    @Autowired private Ocpp16WebSocketEndpoint ocpp16WebSocketEndpoint;

    public static final long PING_INTERVAL = TimeUnit.MINUTES.toMinutes(15);
    private static final long IDLE_TIMEOUT = TimeUnit.HOURS.toMillis(2);
    public static final int MAX_MSG_SIZE = 8_388_608; // 8 MB for max message size

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        WebSocketPolicy policy = new WebSocketPolicy(WebSocketBehavior.SERVER);
        policy.setMaxTextMessageBufferSize(MAX_MSG_SIZE);
        policy.setMaxTextMessageSize(MAX_MSG_SIZE);
        policy.setIdleTimeout(IDLE_TIMEOUT);

        List<AbstractWebSocketEndpoint> endpoints = getEndpoints();
        String[] protocols = endpoints.stream().map(e -> e.getVersion().getValue()).toArray(String[]::new);

        OcppWebSocketUpgrader upgradeStrategy = new OcppWebSocketUpgrader(
                policy, endpoints, notificationService, chargePointHelperService);

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
        return Lists.newArrayList(ocpp16WebSocketEndpoint, ocpp15WebSocketEndpoint, ocpp12WebSocketEndpoint);
    }
}
