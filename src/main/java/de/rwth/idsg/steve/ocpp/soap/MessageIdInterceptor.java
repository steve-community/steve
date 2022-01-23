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
 * @author Sevket Goekay <sevketgokay@gmail.com>
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
