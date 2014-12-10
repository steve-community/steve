package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import de.rwth.idsg.steve.web.dto.ChangeAvailabilityParams;
import lombok.extern.slf4j.Slf4j;
import ocpp.cp._2010._08.*;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Client implementation of OCPP V1.2.
 * 
 * This class has methods to send request to charge points from dynamically created clients.
 * Since there are multiple charge points and their endpoint addresses vary, the clients need to be created dynamically.
 * 
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * 
 */
@Slf4j
@Service
public class ChargePointService12_Client {

    @Autowired
    @Qualifier("ocpp12")
    private JaxWsProxyFactoryBean factory;

    private static final Object LOCK = new Object();

    // -------------------------------------------------------------------------
    // PREPARE Request Payloads
    // -------------------------------------------------------------------------

    public ChangeAvailabilityRequest prepareChangeAvail(ChangeAvailabilityParams params) {
        return new ChangeAvailabilityRequest()
                .withConnectorId(params.getConnectorId())
                .withType(AvailabilityType.fromValue(params.getAvailType()));
    }

    public ChangeConfigurationRequest prepareChangeConfiguration(String confKey, String value) {
        return new ChangeConfigurationRequest()
                .withKey(confKey)
                .withValue(value);
    }

    public ClearCacheRequest prepareClearCache() {
        return new ClearCacheRequest();
    }

    public GetDiagnosticsRequest prepareGetDiagnostics(String location, int retries, int retryInterval,
                                                       String startTime, String stopTime) {
        GetDiagnosticsRequest req = new GetDiagnosticsRequest().withLocation(location);
        if (retries != -1) req.setRetries(retries);
        if (retryInterval != -1) req.setRetryInterval(retryInterval);

        //// Act according to four boolean combinations of the two variables ////

        if (startTime == null) {
            DateTime stop = DateTimeUtils.toDateTime(stopTime);
            if (stop.isBeforeNow()) {
                req.setStopTime(stop);
            } else {
                throw new SteveException("Stop date/time must be in the past.");
            }

        } else if (stopTime == null) {
            DateTime start = DateTimeUtils.toDateTime(startTime);
            if (start.isBeforeNow()) {
                req.setStartTime(start);
            } else {
                throw new SteveException("Start date/time must be in the past.");
            }

        } else {
            DateTime start = DateTimeUtils.toDateTime(startTime);
            DateTime stop = DateTimeUtils.toDateTime(stopTime);
            if (stop.isBeforeNow() && start.isBefore(stop)) {
                req.setStartTime(start);
                req.setStopTime(stop);
            } else {
                throw new SteveException("Start date/time must be before the stop date/time, and both must be in the past.");
            }
        }

        return req;
    }

    public RemoteStartTransactionRequest prepareRemoteStartTransaction(int connectorId, String idTag) {
        if (connectorId == 0) {
            return new RemoteStartTransactionRequest().withIdTag(idTag);
        } else {
            return new RemoteStartTransactionRequest().withIdTag(idTag).withConnectorId(connectorId);
        }
    }

    public RemoteStopTransactionRequest prepareRemoteStopTransaction(int transactionId) {
        return new RemoteStopTransactionRequest().withTransactionId(transactionId);
    }

    public ResetRequest prepareReset(String resetTypeStr){
        return new ResetRequest().withType(ResetType.fromValue(resetTypeStr));
    }

    public UnlockConnectorRequest prepareUnlockConnector(int connectorId) {
        return new UnlockConnectorRequest().withConnectorId(connectorId);
    }

    public UpdateFirmwareRequest prepareUpdateFirmware(String location, int retries, String retrieveDate, int retryInterval) {
        UpdateFirmwareRequest req = new UpdateFirmwareRequest()
                .withLocation(location)
                .withRetrieveDate(DateTimeUtils.toDateTime(retrieveDate));

        if (retries != -1) req.setRetries(retries);
        if (retryInterval != -1) req.setRetryInterval(retryInterval);
        return req;
    }

    // -------------------------------------------------------------------------
    // SEND Request Payloads
    // -------------------------------------------------------------------------

    private ChargePointService create(String endpoint_address) {
        // Should concurrency really be a concern?
        synchronized (LOCK) {
            factory.setAddress(endpoint_address);
            return (ChargePointService) factory.create();
        }
    }

    public String sendChangeAvailability(String chargeBoxId, String endpoint_address, ChangeAvailabilityRequest req) {
        log.debug("Invoking changeAvailability at {}", chargeBoxId);
        ChangeAvailabilityResponse response = create(endpoint_address).changeAvailability(req, chargeBoxId);
        return response.getStatus().value();
        //return "Charge point: " + chargeBoxId + ", Request: ChangeAvailability, Response: " + response.getStatus().value();
    }

    public String sendChangeConfiguration(String chargeBoxId, String endpoint_address, ChangeConfigurationRequest req) {
        log.debug("Invoking changeConfiguration at {}", chargeBoxId);
        ChangeConfigurationResponse response = create(endpoint_address).changeConfiguration(req, chargeBoxId);
        return "Charge point: " + chargeBoxId + ", Request: ChangeConfiguration, Response: " + response.getStatus().value();
    }

    public String sendClearCache(String chargeBoxId, String endpoint_address, ClearCacheRequest req) {
        log.debug("Invoking clearCache at {}", chargeBoxId);
        ClearCacheResponse response = create(endpoint_address).clearCache(req, chargeBoxId);
        return "Charge point: " + chargeBoxId + ", Request: ClearCache, Response: " + response.getStatus().value();
    }

    public String sendGetDiagnostics(String chargeBoxId, String endpoint_address, GetDiagnosticsRequest req) {
        log.debug("Invoking getDiagnostics at {}", chargeBoxId);
        GetDiagnosticsResponse response = create(endpoint_address).getDiagnostics(req, chargeBoxId);
        return "Charge point: " + chargeBoxId + ", Request: GetDiagnostics, Response: " + response.getFileName();
    }

    public String sendRemoteStartTransaction(String chargeBoxId, String endpoint_address, RemoteStartTransactionRequest req) {
        log.debug("Invoking remoteStartTransaction at {}", chargeBoxId);
        RemoteStartTransactionResponse response = create(endpoint_address).remoteStartTransaction(req, chargeBoxId);
        return "Charge point: " + chargeBoxId + ", Request: RemoteStartTransaction, Response: " + response.getStatus().value();
    }

    public String sendRemoteStopTransaction(String chargeBoxId, String endpoint_address, RemoteStopTransactionRequest req) {
        log.debug("Invoking remoteStopTransaction at {}", chargeBoxId);
        RemoteStopTransactionResponse response = create(endpoint_address).remoteStopTransaction(req, chargeBoxId);
        return "Charge point: " + chargeBoxId + ", Request: RemoteStopTransaction, Response: " + response.getStatus().value();
    }

    public String sendReset(String chargeBoxId, String endpoint_address, ResetRequest req) {
        log.debug("Invoking reset at {}", chargeBoxId);
        ResetResponse response = create(endpoint_address).reset(req, chargeBoxId);
        return "Charge point: " + chargeBoxId + ", Request: Reset, Response: " + response.getStatus().value();
    }

    public String sendUnlockConnector(String chargeBoxId, String endpoint_address, UnlockConnectorRequest req) {
        log.debug("Invoking unlockConnector at {}", chargeBoxId);
        UnlockConnectorResponse response = create(endpoint_address).unlockConnector(req, chargeBoxId);
        return "Charge point: " + chargeBoxId + ", Request: UnlockConnector, Response: " + response.getStatus().value();
    }

    public String sendUpdateFirmware(String chargeBoxId, String endpoint_address, UpdateFirmwareRequest req) {
        log.debug("Invoking updateFirmware at {}", chargeBoxId);
        UpdateFirmwareResponse response = create(endpoint_address).updateFirmware(req, chargeBoxId);
        String str = "";
        if (response != null) str = "OK";
        return "Charge point: " + chargeBoxId + ", Request: UpdateFirmware, Response: " + str;
    }
}