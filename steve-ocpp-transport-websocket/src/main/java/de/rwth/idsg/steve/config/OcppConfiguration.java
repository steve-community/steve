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

import com.fasterxml.jackson.databind.ObjectMapper;
import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.ws.InvocationContext;
import de.rwth.idsg.steve.ocpp.ws.JsonObjectMapper;
import de.rwth.idsg.steve.ocpp.ws.WebSocketLogger;
import de.rwth.idsg.steve.ocpp.ws.ocpp12.Ocpp12TypeStore;
import de.rwth.idsg.steve.ocpp.ws.ocpp12.Ocpp12WebSocketEndpoint;
import de.rwth.idsg.steve.ocpp.ws.ocpp15.Ocpp15TypeStore;
import de.rwth.idsg.steve.ocpp.ws.ocpp15.Ocpp15WebSocketEndpoint;
import de.rwth.idsg.steve.ocpp.ws.ocpp16.Ocpp16TypeStore;
import de.rwth.idsg.steve.ocpp.ws.ocpp16.Ocpp16WebSocketEndpoint;
import de.rwth.idsg.steve.ocpp.ws.pipeline.Sender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.Map;

import static de.rwth.idsg.steve.ocpp.OcppVersion.V_12;
import static de.rwth.idsg.steve.ocpp.OcppVersion.V_15;
import static de.rwth.idsg.steve.ocpp.OcppVersion.V_16;

@Configuration
public class OcppConfiguration {

    @Bean
    public Map<OcppVersion, InvocationContext> invocationContexts(
            Ocpp12TypeStore ocpp12TypeStore,
            Ocpp12WebSocketEndpoint ocpp12WebSocketEndpoint,
            Ocpp15TypeStore ocpp15TypeStore,
            Ocpp15WebSocketEndpoint ocpp15WebSocketEndpoint,
            Ocpp16TypeStore ocpp16TypeStore,
            Ocpp16WebSocketEndpoint ocpp16WebSocketEndpoint) {
        var invocationContexts = new EnumMap<OcppVersion, InvocationContext>(OcppVersion.class);
        invocationContexts.put(
                V_12,
                new InvocationContext(ocpp12WebSocketEndpoint, ocpp12TypeStore, CommunicationTask::getOcpp12Request));
        invocationContexts.put(
                V_15,
                new InvocationContext(ocpp15WebSocketEndpoint, ocpp15TypeStore, CommunicationTask::getOcpp15Request));
        invocationContexts.put(
                V_16,
                new InvocationContext(ocpp16WebSocketEndpoint, ocpp16TypeStore, CommunicationTask::getOcpp16Request));
        return invocationContexts;
    }

    @Bean("ocppObjectMapper")
    public ObjectMapper ocppObjectMapper() {
        return JsonObjectMapper.createObjectMapper();
    }

    @Bean
    public Sender sender(WebSocketLogger webSocketLogger) {
        return new Sender(webSocketLogger);
    }
}
