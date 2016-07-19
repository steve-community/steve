package de.rwth.idsg.steve.ocpp.soap;

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.repository.OcppServerRepository;
import de.rwth.idsg.steve.repository.impl.ChargePointRepositoryImpl;
import lombok.extern.slf4j.Slf4j;
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

import java.util.concurrent.ScheduledExecutorService;

import static org.apache.cxf.ws.addressing.JAXWSAConstants.ADDRESSING_PROPERTIES_INBOUND;

/**
 * Intercepts incoming OCPP messages to update the endpoint address ("From" field of the WS-A header) in DB.
 * And the absence of the field is not a deal breaker anymore. But, as a side effect, the user will not be able
 * to send commands to the charging station, since the DB call to list the charge points will filter it out. See
 * {@link ChargePointRepositoryImpl#getChargePointSelect(OcppProtocol)}.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 15.06.2015
 */
@Slf4j
@Component("FromAddressInterceptor")
public class FromAddressInterceptor extends AbstractPhaseInterceptor<Message> {

    @Autowired private OcppServerRepository ocppServerRepository;
    @Autowired private ScheduledExecutorService executorService;

    private static final String CHARGEBOX_ID_HEADER = "ChargeBoxIdentity";

    public FromAddressInterceptor() {
        super(Phase.PRE_INVOKE);
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        executorService.execute(() -> handleMessageInternal(message));
    }

    private void handleMessageInternal(Message message) {
        try {
            String chargeBoxId = getChargeBoxId(message);
            String endpointAddress = getEndpointAddress(message);

            if (chargeBoxId != null && endpointAddress != null) {
                ocppServerRepository.updateEndpointAddress(chargeBoxId, endpointAddress);
            }
        } catch (Exception e) {
            log.error("Exception occurred", e);
        }
    }

    private String getChargeBoxId(Message message) {
        MessageContentsList lst = MessageContentsList.getContentsList(message);
        if (lst == null) {
            return null;
        }

        MessageInfo mi = (MessageInfo) message.get("org.apache.cxf.service.model.MessageInfo");
        for (MessagePartInfo mpi : mi.getMessageParts()) {
            if (CHARGEBOX_ID_HEADER.equals(mpi.getName().getLocalPart())) {
                return (String) lst.get(mpi);
            }
        }

        return null;
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
}
