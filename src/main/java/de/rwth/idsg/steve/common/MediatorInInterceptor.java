package de.rwth.idsg.steve.common;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.cxf.Bus;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.SoapVersion;
import org.apache.cxf.binding.soap.SoapVersionFactory;
import org.apache.cxf.bus.CXFBusFactory;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.endpoint.ServerRegistry;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.InterceptorChain;
import org.apache.cxf.interceptor.StaxInInterceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.staxutils.DepthXMLStreamReader;
import org.apache.cxf.staxutils.StaxUtils;
import org.apache.cxf.transport.MessageObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Taken from http://cxf.apache.org/docs/service-routing.html and modified.
 * 
 */
public class MediatorInInterceptor extends AbstractPhaseInterceptor<SoapMessage> {
	
	private static final Logger LOG = LoggerFactory.getLogger(MediatorInInterceptor.class);

	public MediatorInInterceptor() {
        super(Phase.POST_STREAM);
        super.addBefore(StaxInInterceptor.class.getName());
	}

	public void handleMessage(SoapMessage message) throws Fault {
		String schemaNamespace = "";
        InterceptorChain chain = message.getInterceptorChain();
 
        // Scan the incoming message for its schema namespace
        try {
            // Create a buffered stream so that we get back the original stream after scanning
            InputStream is = message.getContent(InputStream.class);
            BufferedInputStream bis = new BufferedInputStream(is);
            bis.mark(bis.available());
            message.setContent(InputStream.class, bis);
 
            String encoding = (String)message.get(Message.ENCODING);
            XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(bis, encoding);
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
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
 
        // Look up for all available endpoints registered on the bus
        Bus bus = CXFBusFactory.getDefaultBus();
        ServerRegistry serverRegistry = bus.getExtension(ServerRegistry.class);
        List<Server> servers = serverRegistry.getServers();
 
        // If the incoming message has a namespace containing "2012/06/", 
        // we redirect the message to the new version of OCPP service
        Server targetServer = null;
        for (Server server : servers) {
            targetServer = server;
            String address = server.getEndpoint().getEndpointInfo().getAddress();
            if (schemaNamespace.endsWith("2012/06/")) {
                if (address.endsWith("OCPP15")) {
                	LOG.info("Routing the message to CentralSystemServiceOCPP15...");
                    break;
                }
            } else if (address.endsWith("OCPP12")) {
            	LOG.info("Routing the message to CentralSystemServiceOCPP12...");
                break;
            }
        }
 
        // Redirect the request
        MessageObserver mo = targetServer.getDestination().getMessageObserver();
        mo.onMessage(message);
 
        // Now the response has been put in the message, abort the chain
        chain.abort();	
	}
}
