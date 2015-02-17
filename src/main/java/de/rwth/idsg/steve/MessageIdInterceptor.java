package de.rwth.idsg.steve;

import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.binding.soap.SoapHeader;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.UUID;

import static org.apache.cxf.headers.Header.Direction.DIRECTION_IN;
import static org.apache.cxf.headers.Header.HEADER_LIST;
import static org.apache.cxf.ws.addressing.Names.*;

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
        super(Phase.PRE_PROTOCOL);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handleMessage(Message message) throws Fault {
        ArrayList<SoapHeader> headerList = (ArrayList<SoapHeader>) message.get(HEADER_LIST);

        if (!isMessageIdSet(headerList)) {
            log.debug("The required MessageID element is missing! Adding one to the incoming message");

            Element headerElement = (Element) headerList.get(0).getObject();
            Document doc = headerElement.getOwnerDocument();

            Element messageIdElement = doc.createElementNS(WSA_NAMESPACE_NAME, WSA_MESSAGEID_QNAME.getLocalPart());
            messageIdElement.appendChild(doc.createTextNode("uuid:" + UUID.randomUUID().toString()));

            SoapHeader messageIdHeader = new SoapHeader(WSA_MESSAGEID_QNAME, messageIdElement);
            messageIdHeader.setDirection(DIRECTION_IN);

            headerList.add(messageIdHeader);
        }
    }

    private boolean isMessageIdSet(ArrayList<SoapHeader> headerList) {
        for (SoapHeader header : headerList) {
            if (WSA_MESSAGEID_NAME.equals(header.getName().getLocalPart())) {
                return true;
            }
        }
        return false;
    }
}
