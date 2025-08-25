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
import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.ws.InvocationContext;
import de.rwth.idsg.steve.ocpp.ws.OcppWebSocketHandshakeHandler;
import de.rwth.idsg.steve.ocpp.ws.ocpp12.Ocpp12TypeStore;
import de.rwth.idsg.steve.ocpp.ws.ocpp12.Ocpp12WebSocketEndpoint;
import de.rwth.idsg.steve.ocpp.ws.ocpp15.Ocpp15TypeStore;
import de.rwth.idsg.steve.ocpp.ws.ocpp15.Ocpp15WebSocketEndpoint;
import de.rwth.idsg.steve.ocpp.ws.ocpp16.Ocpp16TypeStore;
import de.rwth.idsg.steve.ocpp.ws.ocpp16.Ocpp16WebSocketEndpoint;
import de.rwth.idsg.steve.service.ChargePointRegistrationService;
import de.rwth.idsg.steve.web.validation.ChargeBoxIdValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.time.Duration;
import java.util.EnumMap;
import java.util.Map;

import static de.rwth.idsg.steve.ocpp.OcppVersion.*;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 11.03.2015
 */
@EnableWebSocket
@Configuration
@Slf4j
@RequiredArgsConstructor
public class OcppWebSocketConfiguration implements WebSocketConfigurer {

    public static final String PATH_INFIX = "/websocket/CentralSystemService/";
    public static final Duration PING_INTERVAL = Duration.ofMinutes(15);
    public static final Duration IDLE_TIMEOUT = Duration.ofHours(2);
    public static final int MAX_MSG_SIZE = 8_388_608; // 8 MB for max message size

    private final ChargePointRegistrationService chargePointRegistrationService;
    private final ChargeBoxIdValidator chargeBoxIdValidator;

    private final Ocpp12WebSocketEndpoint ocpp12WebSocketEndpoint;
    private final Ocpp15WebSocketEndpoint ocpp15WebSocketEndpoint;
    private final Ocpp16WebSocketEndpoint ocpp16WebSocketEndpoint;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        OcppWebSocketHandshakeHandler handshakeHandler = new OcppWebSocketHandshakeHandler(
            chargeBoxIdValidator,
            new DefaultHandshakeHandler(),
            Lists.newArrayList(ocpp16WebSocketEndpoint, ocpp15WebSocketEndpoint, ocpp12WebSocketEndpoint),
            chargePointRegistrationService
        );

        registry.addHandler(handshakeHandler.getDummyWebSocketHandler(), PATH_INFIX + "*")
                .setHandshakeHandler(handshakeHandler)
                .setAllowedOrigins("*");
    }

    @Bean
    public Map<OcppVersion, InvocationContext> invocationContexts(Ocpp12TypeStore ocpp12TypeStore,
                                                                  Ocpp15TypeStore ocpp15TypeStore,
                                                                  Ocpp16TypeStore ocpp16TypeStore) {
        var invocationContexts = new EnumMap<OcppVersion, InvocationContext>(OcppVersion .class);
        invocationContexts.put(V_12, new InvocationContext(ocpp12WebSocketEndpoint, ocpp12TypeStore,
                CommunicationTask::getOcpp12Request));
        invocationContexts.put(V_15, new InvocationContext(ocpp15WebSocketEndpoint, ocpp15TypeStore,
                CommunicationTask::getOcpp15Request));
        invocationContexts.put(V_16, new InvocationContext(ocpp16WebSocketEndpoint, ocpp16TypeStore,
                CommunicationTask::getOcpp16Request));
        return invocationContexts;
    }
}
