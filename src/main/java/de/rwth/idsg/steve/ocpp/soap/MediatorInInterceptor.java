package de.rwth.idsg.steve.ocpp.soap;

import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.Bus;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.SoapVersion;
import org.apache.cxf.binding.soap.SoapVersionFactory;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.endpoint.ServerRegistry;
import org.apache.cxf.interceptor.InterceptorChain;
import org.apache.cxf.interceptor.StaxInInterceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.staxutils.DepthXMLStreamReader;
import org.apache.cxf.staxutils.StaxUtils;
import org.apache.cxf.transport.MessageObserver;

import javax.xml.stream.XMLInputFactory;
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
 *
 */
@Slf4j
public class MediatorInInterceptor extends AbstractPhaseInterceptor<SoapMessage> {

    private final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    private final Map<String, Server> actualServers = new HashMap<>(2);

    public MediatorInInterceptor() {
        super(Phase.POST_STREAM);
        super.addBefore(StaxInInterceptor.class.getName());
    }

    public final void handleMessage(SoapMessage message) {
        String schemaNamespace = "";
        InterceptorChain chain = message.getInterceptorChain();

        // Scan the incoming message for its schema namespace
        try {
            // Create a buffered stream so that we get back the original stream after scanning
            InputStream is = message.getContent(InputStream.class);
            BufferedInputStream bis = new BufferedInputStream(is);
            bis.mark(bis.available());
            message.setContent(InputStream.class, bis);

            String encoding = (String) message.get(Message.ENCODING);
            XMLStreamReader reader = xmlInputFactory.createXMLStreamReader(bis, encoding);
            DepthXMLStreamReader xmlReader = new DepthXMLStreamReader(reader);

            if (xmlReader.nextTag() == XMLStreamConstants.START_ELEMENT) {
                String ns = xmlReader.getNamespaceURI();
                SoapVersion soapVersion = SoapVersionFactory.getInstance().getSoapVersion(ns);
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

        // Init the lookup, when the first message ever arrives
        if (actualServers.isEmpty()) {
            initServerLookupMap(message);
        }

        // We redirect the message to the actual OCPP service
        Server targetServer = actualServers.get(schemaNamespace);

        // Redirect the request
        if (targetServer != null) {
            MessageObserver mo = targetServer.getDestination().getMessageObserver();
            mo.onMessage(message);
        }

        // Now the response has been put in the message, abort the chain
        chain.abort();
    }

    /**
     * Iterate over all available servers registered on the bus and build a map
     * consisting of (namespace, server) pairs for later lookup, so we can
     * redirect to the version-specific implementation according to the namespace
     * of the incoming message.
     */
    private void initServerLookupMap(SoapMessage message) {
        Bus bus = message.getExchange().getBus();

        ServerRegistry serverRegistry = bus.getExtension(ServerRegistry.class);
        if (serverRegistry == null) {
            return;
        }

        List<Server> temp = serverRegistry.getServers();
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
    }
}
