/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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
package de.rwth.idsg.steve.ocpp.soap;

import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.Bus;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.SoapVersion;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.endpoint.ServerRegistry;
import org.apache.cxf.interceptor.StaxInInterceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.staxutils.DepthXMLStreamReader;
import org.apache.cxf.staxutils.StaxUtils;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.rwth.idsg.steve.SteveConfiguration.CONFIG;

/**
 * Taken from http://cxf.apache.org/docs/service-routing.html and modified.
 */
@Slf4j
public class MediatorInInterceptor extends AbstractPhaseInterceptor<SoapMessage> {

    private final Map<String, Server> actualServers;

    public MediatorInInterceptor(Bus bus) {
        super(Phase.POST_STREAM);
        super.addBefore(StaxInInterceptor.class.getName());
        actualServers = initServerLookupMap(bus);
    }

    public final void handleMessage(SoapMessage message) {
        String schemaNamespace = "";

        // Scan the incoming message for its schema namespace
        try {
            // Create a buffered stream so that we get back the original stream after scanning
            InputStream is = message.getContent(InputStream.class);
            BufferedInputStream bis = new BufferedInputStream(is);
            bis.mark(bis.available());
            message.setContent(InputStream.class, bis);

            String encoding = (String) message.get(Message.ENCODING);
            XMLStreamReader reader = StaxUtils.createXMLStreamReader(bis, encoding);
            DepthXMLStreamReader xmlReader = new DepthXMLStreamReader(reader);

            if (xmlReader.nextTag() == XMLStreamConstants.START_ELEMENT) {
                SoapVersion soapVersion = message.getVersion();
                // Advance just past header
                StaxUtils.toNextTag(xmlReader, soapVersion.getBody());
                // Past body
                xmlReader.nextTag();
            }
            schemaNamespace = xmlReader.getName().getNamespaceURI();
            bis.reset();

        } catch (IOException | XMLStreamException ex) {
            log.error("Exception happened", ex);
        }

        // We redirect the message to the actual OCPP service
        Server targetServer = actualServers.get(schemaNamespace);

        // Redirect the request
        if (targetServer != null) {
            targetServer.getDestination().getMessageObserver().onMessage(message);
        }

        // Now the response has been put in the message, abort the chain
        message.getInterceptorChain().abort();
    }

    /**
     * Iterate over all available servers registered on the bus and build a map
     * consisting of (namespace, server) pairs for later lookup, so we can
     * redirect to the version-specific implementation according to the namespace
     * of the incoming message.
     */
    private static Map<String, Server> initServerLookupMap(Bus bus) {
        String exceptionMsg = "The services are not created and/or registered to the bus yet.";

        ServerRegistry serverRegistry = bus.getExtension(ServerRegistry.class);
        if (serverRegistry == null) {
            throw new RuntimeException(exceptionMsg);
        }

        List<Server> temp = serverRegistry.getServers();
        if (temp.isEmpty()) {
            throw new RuntimeException(exceptionMsg);
        }

        Map<String, Server> actualServers = new HashMap<>(temp.size() - 1);
        for (Server server : temp) {
            EndpointInfo info = server.getEndpoint().getEndpointInfo();
            String address = info.getAddress();

            // exclude the 'dummy' routing server
            if (CONFIG.getRouterEndpointPath().equals(address)) {
                continue;
            }

            String serverNamespace = info.getName().getNamespaceURI();
            actualServers.put(serverNamespace, server);
        }
        return actualServers;
    }
}
