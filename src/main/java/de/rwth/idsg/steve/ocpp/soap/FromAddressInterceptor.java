package de.rwth.idsg.steve.ocpp.soap;

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.repository.OcppServerRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.ws.addressing.AddressingProperties;
import org.apache.cxf.ws.addressing.ContextUtils;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.jooq.exception.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

import java.util.List;

import static org.apache.cxf.ws.addressing.JAXWSAConstants.ADDRESSING_PROPERTIES_INBOUND;

/**
 * Intercepts incoming OCPP messages to update the endpoint address ("From" field of the WS-A header) in DB.
 * And the absence of the field is not a deal breaker anymore. But, as a side effect, the user will not be able
 * to send commands to the charging station, since the DB call to list the charge points will filter it out. See
 * {@link de.rwth.idsg.steve.repository.ChargePointRepositoryImpl#getChargePointSelect(OcppProtocol)}.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 15.06.2015
 */
@Slf4j
@Component("FromAddressInterceptor")
public class FromAddressInterceptor extends AbstractPhaseInterceptor<SoapMessage> {

    @Autowired private OcppServerRepository ocppServerRepository;

    private static final String CHARGEBOX_ID_HEADER = "chargeBoxIdentity";

    public FromAddressInterceptor() {
        super(Phase.POST_PROTOCOL);
    }

    @Override
    public void handleMessage(SoapMessage message) throws Fault {
        String chargeBoxId = getChargeBoxId(message);
        String endpointAddress = getEndpointAddress(message);

        if (chargeBoxId != null && endpointAddress != null) {
            try {
                ocppServerRepository.updateEndpointAddress(chargeBoxId, endpointAddress);
            } catch (DataAccessException e) {
                // Fail silently, allow subsequent processes to finish
                log.error("Exception occurred", e);
            }
        }
    }

    private String getChargeBoxId(SoapMessage message) {
        List<Header> headers = message.getHeaders();
        for (Header h : headers) {
            if (CHARGEBOX_ID_HEADER.equals(h.getName().getLocalPart())) {
                Element he = (Element) h.getObject();
                return he.getFirstChild().getTextContent();
            }
        }
        return null;
    }

    private String getEndpointAddress(SoapMessage message) {
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
