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

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.repository.OcppServerRepository;
import de.rwth.idsg.steve.repository.impl.ChargePointRepositoryImpl;
import de.rwth.idsg.steve.service.ChargePointHelperService;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2015._10.RegistrationStatus;
import org.apache.cxf.binding.soap.Soap12;
import org.apache.cxf.binding.soap.SoapFault;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageContentsList;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.service.model.MessageInfo;
import org.apache.cxf.service.model.MessagePartInfo;
import org.apache.cxf.ws.addressing.AddressingProperties;
import org.apache.cxf.ws.addressing.ContextUtils;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.namespace.QName;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;

import static org.apache.cxf.ws.addressing.JAXWSAConstants.ADDRESSING_PROPERTIES_INBOUND;

/**
 * 1. Checks the registration status of a station for operations other than BootNotification.
 *
 * 2. Intercepts incoming OCPP messages to update the endpoint address ("From" field of the WS-A header) in DB.
 * And the absence of the field is not a deal breaker anymore. But, as a side effect, the user will not be able
 * to send commands to the charging station, since the DB call to list the charge points will filter it out. See
 * {@link ChargePointRepositoryImpl#getChargePointSelect(OcppProtocol, java.util.List)}.
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 15.06.2015
 */
@Slf4j
@Component("MessageHeaderInterceptor")
public class MessageHeaderInterceptor extends AbstractPhaseInterceptor<Message> {

    @Autowired private OcppServerRepository ocppServerRepository;
    @Autowired private ChargePointHelperService chargePointHelperService;
    @Autowired private ScheduledExecutorService executorService;

    private static final String BOOT_OPERATION_NAME = "BootNotification";
    private static final String CHARGEBOX_ID_HEADER = "ChargeBoxIdentity";

    public MessageHeaderInterceptor() {
        super(Phase.PRE_INVOKE);
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        String chargeBoxId = getChargeBoxId(message);

        // -------------------------------------------------------------------------
        // 1. check registration for operations other than BootNotification
        // -------------------------------------------------------------------------

        QName opName = message.getExchange().getBindingOperationInfo().getOperationInfo().getName();

        if (!BOOT_OPERATION_NAME.equals(opName.getLocalPart())) {
            Optional<RegistrationStatus> status = chargePointHelperService.getRegistrationStatus(chargeBoxId);
            boolean allow = status.isPresent() && status.get() != RegistrationStatus.REJECTED;
            if (!allow) {
                throw createAuthFault(opName);
            }
        }

        // -------------------------------------------------------------------------
        // 2. update endpoint
        // -------------------------------------------------------------------------

        executorService.execute(() -> {
            try {
                String endpointAddress = getEndpointAddress(message);
                if (endpointAddress != null) {
                    ocppServerRepository.updateEndpointAddress(chargeBoxId, endpointAddress);
                }
            } catch (Exception e) {
                log.error("Exception occurred", e);
            }
        });
    }

    private String getChargeBoxId(Message message) {
        MessageContentsList lst = MessageContentsList.getContentsList(message);
        if (lst != null) {
            MessageInfo mi = (MessageInfo) message.get("org.apache.cxf.service.model.MessageInfo");
            for (MessagePartInfo mpi : mi.getMessageParts()) {
                if (CHARGEBOX_ID_HEADER.equals(mpi.getName().getLocalPart())) {
                    return (String) lst.get(mpi);
                }
            }
        }
        // should not happen
        throw createSpecFault(message.getExchange().getBindingOperationInfo().getOperationInfo().getName());
    }

    private String getEndpointAddress(Message message) {
        AddressingProperties addressProp = (AddressingProperties) message.get(ADDRESSING_PROPERTIES_INBOUND);
        if (addressProp == null) {
            return null;
        }

        EndpointReferenceType from = addressProp.getFrom();
        if (ContextUtils.isGenericAddress(from)) {
            return null;
        } else {
            return from.getAddress().getValue();
        }
    }

    private static SoapFault createAuthFault(QName qName) {
        // as defined by OCPP spec
        String message = "Sender failed authentication or is not authorized to use the requested operation.";
        SoapFault sf = new SoapFault(message, Soap12.getInstance().getSender());
        sf.addSubCode(new QName(qName.getNamespaceURI(), "SecurityError"));
        return sf;
    }

    private static SoapFault createSpecFault(QName qName) {
        // as defined by OCPP spec
        String message = "Sender's message does not comply with protocol specification.";
        SoapFault sf = new SoapFault(message, Soap12.getInstance().getSender());
        sf.addSubCode(new QName(qName.getNamespaceURI(), "ProtocolError"));
        return sf;
    }
}
