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

import de.rwth.idsg.steve.ocpp.soap.LoggingFeatureProxy;
import de.rwth.idsg.steve.ocpp.soap.MediatorInInterceptor;
import de.rwth.idsg.steve.ocpp.soap.MessageHeaderInterceptor;
import de.rwth.idsg.steve.ocpp.soap.MessageIdInterceptor;
import lombok.RequiredArgsConstructor;
import org.apache.cxf.Bus;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.message.Message;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

/**
 * Configuration and beans related to OCPP.
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 18.11.2014
 */
@RequiredArgsConstructor
@Configuration
public class OcppConfiguration {

    private final Bus bus;
    private final ocpp.cs._2010._08.CentralSystemService ocpp12Server;
    private final ocpp.cs._2012._06.CentralSystemService ocpp15Server;
    private final ocpp.cs._2015._10.CentralSystemService ocpp16Server;
    private final MessageHeaderInterceptor messageHeaderInterceptor;

    private final MessageIdInterceptor messageIdInterceptor = new MessageIdInterceptor();

    @Bean
    public EndpointImpl ocpp12Endpoint() {
        return createDefaultEndpoint(ocpp12Server, "/CentralSystemServiceOCPP12");
    }

    @Bean
    public EndpointImpl ocpp15Endpoint() {
        return createDefaultEndpoint(ocpp15Server, "/CentralSystemServiceOCPP15");
    }

    @Bean
    public EndpointImpl ocpp16Endpoint() {
        return createDefaultEndpoint(ocpp16Server, "/CentralSystemServiceOCPP16");
    }

    /**
     * Just a dummy service to route incoming messages to the appropriate service version.
     */
    @Bean
    public EndpointImpl routerEndpoint(EndpointImpl ocpp12Endpoint,
                                       EndpointImpl ocpp15Endpoint,
                                       EndpointImpl ocpp16Endpoint) {
        var mediator = new MediatorInInterceptor(List.of(ocpp12Endpoint, ocpp15Endpoint, ocpp16Endpoint));
        return createEndpoint(
            ocpp12Server, SteveProperties.ROUTER_ENDPOINT_PATH, List.of(mediator), Collections.emptyList()
        );
    }

    private EndpointImpl createDefaultEndpoint(Object serviceBean, String address) {
        return createEndpoint(
            serviceBean, address, List.of(messageIdInterceptor, messageHeaderInterceptor), List.of(LoggingFeatureProxy.INSTANCE.get())
        );
    }

    private EndpointImpl createEndpoint(Object serviceBean, String address,
                                        List<Interceptor<? extends Message>> interceptors,
                                        List<? extends Feature> features) {
        EndpointImpl endpoint = new EndpointImpl(bus, serviceBean);
        endpoint.getInInterceptors().addAll(interceptors);
        endpoint.getFeatures().addAll(features);
        endpoint.publish(address);
        return endpoint;
    }
}
