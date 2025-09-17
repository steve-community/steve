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

import de.rwth.idsg.steve.SteveConfiguration;
import de.rwth.idsg.steve.ocpp.soap.MediatorInInterceptor;
import de.rwth.idsg.steve.ocpp.soap.MessageHeaderInterceptor;
import de.rwth.idsg.steve.ocpp.soap.MessageIdInterceptor;
import ocpp.cs._2010._08.CentralSystemService;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.common.logging.Slf4jLogger;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.ext.logging.event.LogEvent;
import org.apache.cxf.ext.logging.slf4j.Slf4jEventSender;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.message.Message;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

/**
 * Configuration and beans related to OCPP.
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 18.11.2014
 */
@Configuration
public class OcppSoapConfiguration {

    static {
        LogUtils.setLoggerClass(Slf4jLogger.class);
    }

    private final SteveConfiguration config;
    private final ocpp.cs._2010._08.CentralSystemService ocpp12Server;
    private final ocpp.cs._2012._06.CentralSystemService ocpp15Server;
    private final ocpp.cs._2015._10.CentralSystemService ocpp16Server;

    private final MessageHeaderInterceptor messageHeaderInterceptor;
    private final Bus bus;

    public OcppSoapConfiguration(
            CentralSystemService ocpp12Server,
            ocpp.cs._2012._06.CentralSystemService ocpp15Server,
            ocpp.cs._2015._10.CentralSystemService ocpp16Server,
            MessageHeaderInterceptor messageHeaderInterceptor,
            SteveConfiguration config,
            Bus bus) {
        this.config = config;
        this.ocpp12Server = ocpp12Server;
        this.ocpp15Server = ocpp15Server;
        this.ocpp16Server = ocpp16Server;
        this.messageHeaderInterceptor = messageHeaderInterceptor;
        this.bus = bus;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void configure() {
        List<Interceptor<? extends Message>> interceptors =
                asList(new MessageIdInterceptor(), messageHeaderInterceptor);
        var logging = singletonList(loggingFeature());

        createOcppService(bus, ocpp12Server, "/CentralSystemServiceOCPP12", interceptors, logging);
        createOcppService(bus, ocpp15Server, "/CentralSystemServiceOCPP15", interceptors, logging);
        createOcppService(bus, ocpp16Server, "/CentralSystemServiceOCPP16", interceptors, logging);

        // Just a dummy service to route incoming messages to the appropriate service version. This should be the last
        // one to be created, since in MediatorInInterceptor we go over created/registered services and build a map.
        List<Interceptor<? extends Message>> mediator = singletonList(new MediatorInInterceptor(bus, config));
        createOcppService(
                bus, ocpp12Server, config.getPaths().getRouterEndpointPath(), mediator, Collections.emptyList());
    }

    @Bean(name = Bus.DEFAULT_BUS_ID, destroyMethod = "shutdown")
    public static SpringBus springBus() {
        return new SpringBus();
    }

    private static void createOcppService(
            Bus bus,
            Object serviceBean,
            String address,
            List<Interceptor<? extends Message>> interceptors,
            Collection<? extends Feature> features) {
        JaxWsServerFactoryBean f = new JaxWsServerFactoryBean();
        f.setBus(bus);
        f.setServiceBean(serviceBean);
        f.setAddress(address);
        f.getFeatures().addAll(features);
        f.getInInterceptors().addAll(interceptors);
        f.create();
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
