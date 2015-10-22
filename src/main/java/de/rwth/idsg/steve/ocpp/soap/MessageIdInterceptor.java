package de.rwth.idsg.steve.ocpp.soap;

import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.ws.addressing.AddressingProperties;
import org.apache.cxf.ws.addressing.ContextUtils;

import static org.apache.cxf.ws.addressing.JAXWSAConstants.ADDRESSING_PROPERTIES_INBOUND;

/**
 * There are some implementations of SOAP/WS-A, which leave the required MessageID element out.
 * This behaviour is incorrect! Since CXF throws an exception in absence of this element,
 * we provide a workaround which adds this SOAP header to the incoming message.
 *
 * Further reading:
 * http://www.w3.org/TR/2006/REC-ws-addr-core-20060509/#formreplymsg
 * http://comments.gmane.org/gmane.comp.apache.cxf.user/17599
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 17.02.2015
 */
@Slf4j
public class MessageIdInterceptor extends AbstractPhaseInterceptor<Message> {

    public MessageIdInterceptor() {
        super(Phase.PRE_LOGICAL);
        addBefore(org.apache.cxf.ws.addressing.impl.MAPAggregatorImpl.class.getName());
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        AddressingProperties addressProp = (AddressingProperties) message.get(ADDRESSING_PROPERTIES_INBOUND);

        // Ws-Addressing is not used in the message. Early exit
        if (addressProp == null) {
            return;
        }

        if (addressProp.getMessageID() == null) {
            log.debug("The required MessageID element is missing! Adding one to the incoming message");
            addressProp.setMessageID(ContextUtils.getAttributedURI(ContextUtils.generateUUID()));
        }
    }

}
