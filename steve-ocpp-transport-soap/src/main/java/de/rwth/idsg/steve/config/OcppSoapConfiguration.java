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

import de.rwth.idsg.steve.ocpp.soap.MediatorInInterceptor;
import de.rwth.idsg.steve.ocpp.soap.MessageHeaderInterceptor;
import de.rwth.idsg.steve.ocpp.soap.MessageIdInterceptor;
import org.apache.cxf.Bus;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.ext.logging.event.LogEvent;
import org.apache.cxf.ext.logging.slf4j.Slf4jEventSender;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.message.Message;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import jakarta.xml.ws.Endpoint;

/**
 * Configuration and beans related to OCPP.
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 18.11.2014
 */
@Configuration
public class OcppSoapConfiguration {

    @Bean
    public MessageIdInterceptor messageIdInterceptor() {
        return new MessageIdInterceptor();
    }

    @Bean
    public EndpointImpl ocpp12Endpoint(
            Bus bus,
            LoggingFeature loggingFeature,
            MessageIdInterceptor messageIdInterceptor,
            MessageHeaderInterceptor messageHeaderInterceptor,
            ocpp.cs._2010._08.CentralSystemService ocpp12Server) {
        return createDefaultEndpoint(
                bus,
                loggingFeature,
                messageIdInterceptor,
                messageHeaderInterceptor,
                ocpp12Server,
                "/CentralSystemServiceOCPP12");
    }

    @Bean
    public EndpointImpl ocpp15Endpoint(
            Bus bus,
            LoggingFeature loggingFeature,
            MessageIdInterceptor messageIdInterceptor,
            MessageHeaderInterceptor messageHeaderInterceptor,
            ocpp.cs._2012._06.CentralSystemService ocpp15Server) {
        return createDefaultEndpoint(
                bus,
                loggingFeature,
                messageIdInterceptor,
                messageHeaderInterceptor,
                ocpp15Server,
                "/CentralSystemServiceOCPP15");
    }

    @Bean
    public EndpointImpl ocpp16Endpoint(
            Bus bus,
            LoggingFeature loggingFeature,
            MessageIdInterceptor messageIdInterceptor,
            MessageHeaderInterceptor messageHeaderInterceptor,
            ocpp.cs._2015._10.CentralSystemService ocpp16Server) {
        return createDefaultEndpoint(
                bus,
                loggingFeature,
                messageIdInterceptor,
                messageHeaderInterceptor,
                ocpp16Server,
                "/CentralSystemServiceOCPP16");
    }

    /**
     * Just a dummy service to route incoming messages to the appropriate service version.
     */
    @Bean
    public Endpoint routerEndpoint(
            SteveProperties steveProperties,
            Bus bus,
            // TODO find another dummy service to remove to the strong dependency to ocpp12
            ocpp.cs._2010._08.CentralSystemService ocpp12Server,
            List<EndpointImpl> ocppEndpoints) {
        var mediator = new MediatorInInterceptor(ocppEndpoints);
        return createEndpoint(
                bus,
                ocpp12Server,
                steveProperties.getPaths().getRouterEndpointPath(),
                List.of(mediator),
                Collections.emptyList());
    }

    private EndpointImpl createDefaultEndpoint(
            Bus bus,
            LoggingFeature loggingFeature,
            MessageIdInterceptor messageIdInterceptor,
            MessageHeaderInterceptor messageHeaderInterceptor,
            Object serviceBean,
            String address) {
        return createEndpoint(
                bus,
                serviceBean,
                address,
                List.of(messageIdInterceptor, messageHeaderInterceptor),
                List.of(loggingFeature));
    }

    private static EndpointImpl createEndpoint(
            Bus bus,
            Object serviceBean,
            String address,
            Collection<Interceptor<? extends Message>> interceptors,
            Collection<? extends Feature> features) {
        var endpoint = new EndpointImpl(bus, serviceBean);
        endpoint.getInInterceptors().addAll(interceptors);
        endpoint.getFeatures().addAll(features);
        endpoint.publish(address);
        return endpoint;
    }

    @Bean
    public LoggingFeature loggingFeature() {
        var feature = new LoggingFeature();
        feature.setSender(new Slf4jEventSender() {
            @Override
            protected String getLogMessage(LogEvent event) {
                var b = new StringBuilder();

                b.append('\n') // Start from the next line to have the output well-aligned
                        .append("    ExchangeId: ")
                        .append(event.getExchangeId())
                        .append('\n')
                        .append("    Payload: ")
                        .append(event.getPayload());

                return b.toString();
            }
        });
        return feature;
    }
}
