package net.parkl.ocpp.service.middleware;

import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.RequestResult;
import de.rwth.idsg.steve.repository.TaskStore;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.service.ChargePointHelperService;
import de.rwth.idsg.steve.service.IChargePointService12_Client;
import de.rwth.idsg.steve.service.IChargePointService15_Client;
import de.rwth.idsg.steve.service.IChargePointService16_Client;
import net.parkl.ocpp.repositories.OcppChargeBoxRepository;
import net.parkl.ocpp.util.AsyncWaiter;
import ocpp.cp._2012._06.AvailabilityStatus;
import ocpp.cp._2015._10.UnlockStatus;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

public abstract class AbstractOcppMiddleware {


    private static final long WAIT_MS = 100;

    @Autowired
    protected TaskStore requestTaskStore;
    @Autowired
    @Qualifier("ChargePointService15_Client")
    protected IChargePointService15_Client client15;
    @Autowired
    @Qualifier("ChargePointService12_Client")
    protected IChargePointService12_Client client12;
    @Autowired
    @Qualifier("ChargePointService16_Client")
    protected IChargePointService16_Client client16;


    @Autowired
    protected OcppChargeBoxRepository chargeBoxRepo;

    @Autowired
    protected ChargePointHelperService chargePointHelperService;

    protected void processGenericResult(String type, String chargeBoxId,
                                      RequestResult result) {
        if (result != null) {
            if (result.getResponse() != null) {
                if (result.getResponse().equals(AvailabilityStatus.ACCEPTED.value())) {
                    LoggerFactory.getLogger(getClass()).info("{} accepted: {}", type, chargeBoxId);

                } else if (result.getResponse().equals(AvailabilityStatus.REJECTED.value())) {
                    LoggerFactory.getLogger(getClass()).info("{} rejected: {}", type, chargeBoxId);
                    throw new IllegalStateException(type + " rejected: " + chargeBoxId);
                } else if (result.getResponse().equals(UnlockStatus.UNLOCK_FAILED.value())) {
                    LoggerFactory.getLogger(getClass()).info("{} unlock failed: {}", type, chargeBoxId);
                    throw new IllegalStateException(type + " unlock failed: " + chargeBoxId);
                } else if (result.getResponse().equals(UnlockStatus.NOT_SUPPORTED.value())) {
                    LoggerFactory.getLogger(getClass()).info("{} unlock not supported: {}", type, chargeBoxId);
                    throw new IllegalStateException(type + " not supported: " + chargeBoxId);
                } else {
                    LoggerFactory.getLogger(getClass()).info("{} success response {}: {}", type, result.getResponse(), chargeBoxId);
                }
            } else if (result.getErrorMessage() != null) {
                throw new IllegalStateException(result.getErrorMessage());
            } else {
                LoggerFactory.getLogger(getClass()).info("{} unknown error: {}", type, chargeBoxId);
                throw new IllegalStateException("Unknown error: " + chargeBoxId);

            }
        } else {
            LoggerFactory.getLogger(getClass()).info("{} no response error: {}", type, chargeBoxId);
            throw new IllegalStateException("No response from charge box: " + chargeBoxId);
        }
    }





    @SuppressWarnings("rawtypes")
    protected RequestResult getResponse(int taskId, String chargeBoxId) {
        CommunicationTask task = requestTaskStore.get(taskId);
        RequestResult result = (RequestResult) task.getResultMap().get(chargeBoxId);
        if (result != null && (result.getResponse() != null || result.getErrorMessage() != null)) {
            return result;
        }
        return null;
    }



    protected RequestResult waitForResult(String chargeBoxId, int taskId) {
        AsyncWaiter<RequestResult> waiter = new AsyncWaiter<>(90000);
        waiter.setDelayMs(WAIT_MS);
        waiter.setIntervalMs(WAIT_MS);
        return waiter.waitFor(() -> getResponse(taskId, chargeBoxId));
    }



    protected ChargePointSelect getChargePoint(String chargeBoxId, String protocol) {
        if (protocol==null) {
            throw new IllegalArgumentException("No protocol specified for charge box: " + chargeBoxId);
        }
        OcppProtocol ocppProtocol = OcppProtocol.fromCompositeValue(protocol);
        List<ChargePointSelect> chargePoints;
        switch (ocppProtocol) {
            case V_12_SOAP:
            case V_12_JSON:
                chargePoints = chargePointHelperService.getChargePoints(OcppVersion.V_12);
                break;
            case V_15_SOAP:
            case V_15_JSON:
                chargePoints = chargePointHelperService.getChargePoints(OcppVersion.V_15);
                break;
            case V_16_SOAP:
            case V_16_JSON:
                chargePoints = chargePointHelperService.getChargePoints(OcppVersion.V_16);
                break;
            default:
                throw new IllegalStateException("OCPP protocol not supported: " + ocppProtocol);
        }

        for (ChargePointSelect c : chargePoints) {
            if (c.getChargeBoxId().equals(chargeBoxId)) {
                return c;
            }
        }
        return null;
    }

}
