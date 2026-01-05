/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
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

import de.rwth.idsg.steve.ocpp.OcppEnabledCondition;
import de.rwth.idsg.steve.ocpp.soap.CentralSystemService12_SoapServer;
import de.rwth.idsg.steve.ocpp.soap.CentralSystemService15_SoapServer;
import de.rwth.idsg.steve.ocpp.soap.CentralSystemService16_SoapServer;
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
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
    private final MessageHeaderInterceptor messageHeaderInterceptor;

    private final MessageIdInterceptor messageIdInterceptor = new MessageIdInterceptor();

    @Bean
    @Conditional(OcppEnabledCondition.V12.Soap.class)
    public EndpointImpl ocpp12Endpoint(CentralSystemService12_SoapServer ocpp12Server) {
        return createDefaultEndpoint(ocpp12Server, "/CentralSystemServiceOCPP12");
    }

    @Bean
    @Conditional(OcppEnabledCondition.V15.Soap.class)
    public EndpointImpl ocpp15Endpoint(CentralSystemService15_SoapServer ocpp15Server) {
        return createDefaultEndpoint(ocpp15Server, "/CentralSystemServiceOCPP15");
    }

    @Bean
    @Conditional(OcppEnabledCondition.V16.Soap.class)
    public EndpointImpl ocpp16Endpoint(CentralSystemService16_SoapServer ocpp16Server) {
        return createDefaultEndpoint(ocpp16Server, "/CentralSystemServiceOCPP16");
    }

    /**
     * Just a dummy service to route incoming messages to the appropriate service version.
     * If there are no endpoints, we should not have any router endpoint either, and hence the ConditionalOnBean.
     */
    @Bean
    @ConditionalOnBean(EndpointImpl.class)
    public EndpointImpl routerEndpoint(List<EndpointImpl> endpoints) {
        var mediator = new MediatorInInterceptor(endpoints);

        // get and use any existing implementor for this router endpoint. we will not use the implementor anyway.
        var implementor = endpoints.getFirst().getImplementor();

        return createEndpoint(
            implementor, SteveProperties.ROUTER_ENDPOINT_PATH, List.of(mediator), Collections.emptyList()
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
