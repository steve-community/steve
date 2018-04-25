package de.rwth.idsg.steve.ocpp.soap;

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.OcppServerRepository;
import de.rwth.idsg.steve.repository.impl.ChargePointRepositoryImpl;
import lombok.extern.slf4j.Slf4j;
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
import java.util.concurrent.ScheduledExecutorService;

import static org.apache.cxf.ws.addressing.JAXWSAConstants.ADDRESSING_PROPERTIES_INBOUND;

/**
 * 1. Checks the registration status of a station for operations other than BootNotification.
 *
 * 2. Intercepts incoming OCPP messages to update the endpoint address ("From" field of the WS-A header) in DB.
 * And the absence of the field is not a deal breaker anymore. But, as a side effect, the user will not be able
 * to send commands to the charging station, since the DB call to list the charge points will filter it out. See
 * {@link ChargePointRepositoryImpl#getChargePointSelect(OcppProtocol)}.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 15.06.2015
 */
@Slf4j
@Component("MessageHeaderInterceptor")
public class MessageHeaderInterceptor extends AbstractPhaseInterceptor<Message> {

    @Autowired private OcppServerRepository ocppServerRepository;
    @Autowired private ChargePointRepository chargePointRepository;
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
            if (!chargePointRepository.isRegistered(chargeBoxId)) {
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
