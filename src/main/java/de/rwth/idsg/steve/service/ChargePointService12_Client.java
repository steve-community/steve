package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.OcppVersion;
import de.rwth.idsg.steve.handler.ocpp12.ChangeAvailabilityResponseHandler;
import de.rwth.idsg.steve.handler.ocpp12.ChangeConfigurationResponseHandler;
import de.rwth.idsg.steve.handler.ocpp12.ClearCacheResponseHandler;
import de.rwth.idsg.steve.handler.ocpp12.GetDiagnosticsResponseHandler;
import de.rwth.idsg.steve.handler.ocpp12.RemoteStartTransactionResponseHandler;
import de.rwth.idsg.steve.handler.ocpp12.RemoteStopTransactionResponseHandler;
import de.rwth.idsg.steve.handler.ocpp12.ResetResponseHandler;
import de.rwth.idsg.steve.handler.ocpp12.UnlockConnectorResponseHandler;
import de.rwth.idsg.steve.handler.ocpp12.UpdateFirmwareResponseHandler;
import de.rwth.idsg.steve.repository.RequestTaskStore;
import de.rwth.idsg.steve.web.RequestTask;
import de.rwth.idsg.steve.web.dto.common.ChargePointSelect;
import de.rwth.idsg.steve.web.dto.common.GetDiagnosticsParams;
import de.rwth.idsg.steve.web.dto.common.MultipleChargePointSelect;
import de.rwth.idsg.steve.web.dto.common.RemoteStartTransactionParams;
import de.rwth.idsg.steve.web.dto.common.RemoteStopTransactionParams;
import de.rwth.idsg.steve.web.dto.common.UnlockConnectorParams;
import de.rwth.idsg.steve.web.dto.common.UpdateFirmwareParams;
import de.rwth.idsg.steve.web.dto.op12.ChangeAvailabilityParams;
import de.rwth.idsg.steve.web.dto.op12.ChangeConfigurationParams;
import de.rwth.idsg.steve.web.dto.op12.ResetParams;
import lombok.extern.slf4j.Slf4j;
import ocpp.cp._2010._08.*;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Autowired private RequestTaskStore requestTaskStore;

    private static final Object LOCK = new Object();

    private ChargePointService create(String endpointAddress) {
        // Should concurrency really be a concern?
        synchronized (LOCK) {
            factory.setAddress(endpointAddress);
            return (ChargePointService) factory.create();
        }
    }

    // -------------------------------------------------------------------------
    // Create Request Payloads
    // -------------------------------------------------------------------------

    private ChangeAvailabilityRequest prepareChangeAvailability(ChangeAvailabilityParams params) {
        return new ChangeAvailabilityRequest()
                .withConnectorId(params.getConnectorId())
                .withType(params.getAvailType());
    }

    private ChangeConfigurationRequest prepareChangeConfiguration(ChangeConfigurationParams params) {
        return new ChangeConfigurationRequest()
                .withKey(params.getConfKey().value())
                .withValue(params.getValue());
    }

    private ClearCacheRequest prepareClearCache() {
        return new ClearCacheRequest();
    }

    private GetDiagnosticsRequest prepareGetDiagnostics(GetDiagnosticsParams params) {
        return new GetDiagnosticsRequest()
                .withLocation(params.getLocation())
                .withRetries(params.getRetries())
                .withRetryInterval(params.getRetryInterval())
                .withStartTime(params.getStart().getDateTime())
                .withStopTime(params.getStop().getDateTime());
    }

    private RemoteStartTransactionRequest prepareRemoteStartTransaction(RemoteStartTransactionParams params) {
        return new RemoteStartTransactionRequest()
                .withIdTag(params.getIdTag())
                .withConnectorId(params.getConnectorId());
    }

    private RemoteStopTransactionRequest prepareRemoteStopTransaction(RemoteStopTransactionParams params) {
        return new RemoteStopTransactionRequest()
                .withTransactionId(params.getTransactionId());
    }

    private ResetRequest prepareReset(ResetParams params){
        return new ResetRequest()
                .withType(params.getResetType());
    }

    private UnlockConnectorRequest prepareUnlockConnector(UnlockConnectorParams params) {
        return new UnlockConnectorRequest()
                .withConnectorId(params.getConnectorId());
    }

    private UpdateFirmwareRequest prepareUpdateFirmware(UpdateFirmwareParams params) {
        return new UpdateFirmwareRequest()
                .withLocation(params.getLocation())
                .withRetrieveDate(params.getRetrieve().getDateTime())
                .withRetries(params.getRetries())
                .withRetryInterval(params.getRetryInterval());
    }

    // -------------------------------------------------------------------------
    // Multiple Execution
    // -------------------------------------------------------------------------

    public int changeAvailability(ChangeAvailabilityParams params) {
        ChangeAvailabilityRequest req = this.prepareChangeAvailability(params);
        List<ChargePointSelect> chargePointSelectList = params.getChargePointSelectList();
        RequestTask requestTask = new RequestTask(OcppVersion.V_12, "Change Availability", chargePointSelectList);

        for (ChargePointSelect c : chargePointSelectList) {
            String chargeBoxId = c.getChargeBoxId();
            ChangeAvailabilityResponseHandler handler = new ChangeAvailabilityResponseHandler(requestTask, chargeBoxId);
            create(c.getEndpointAddress()).changeAvailabilityAsync(req, chargeBoxId, handler);
        }

        return requestTaskStore.add(requestTask);
    }

    public int changeConfiguration(ChangeConfigurationParams params) {
        ChangeConfigurationRequest req = this.prepareChangeConfiguration(params);
        List<ChargePointSelect> chargePointSelectList = params.getChargePointSelectList();
        RequestTask requestTask = new RequestTask(OcppVersion.V_12, "Change Configuration", chargePointSelectList);

        for (ChargePointSelect c : chargePointSelectList) {
            String chargeBoxId = c.getChargeBoxId();
            ChangeConfigurationResponseHandler handler = new ChangeConfigurationResponseHandler(requestTask, chargeBoxId);
            create(c.getEndpointAddress()).changeConfigurationAsync(req, chargeBoxId, handler);
        }

        return requestTaskStore.add(requestTask);
    }

    public int clearCache(MultipleChargePointSelect params) {
        ClearCacheRequest req = this.prepareClearCache();
        List<ChargePointSelect> chargePointSelectList = params.getChargePointSelectList();
        RequestTask requestTask = new RequestTask(OcppVersion.V_12, "Clear Cache", chargePointSelectList);

        for (ChargePointSelect c : chargePointSelectList) {
            String chargeBoxId = c.getChargeBoxId();
            ClearCacheResponseHandler handler = new ClearCacheResponseHandler(requestTask, chargeBoxId);
            create(c.getEndpointAddress()).clearCacheAsync(req, chargeBoxId, handler);
        }

        return requestTaskStore.add(requestTask);
    }

    public int getDiagnostics(GetDiagnosticsParams params) {
        GetDiagnosticsRequest req = this.prepareGetDiagnostics(params);
        List<ChargePointSelect> chargePointSelectList = params.getChargePointSelectList();
        RequestTask requestTask = new RequestTask(OcppVersion.V_12, "Get Diagnostics", chargePointSelectList);

        for (ChargePointSelect c : chargePointSelectList) {
            String chargeBoxId = c.getChargeBoxId();
            GetDiagnosticsResponseHandler handler = new GetDiagnosticsResponseHandler(requestTask, chargeBoxId);
            create(c.getEndpointAddress()).getDiagnosticsAsync(req, chargeBoxId, handler);
        }

        return requestTaskStore.add(requestTask);
    }

    public int reset(ResetParams params) {
        ResetRequest req = this.prepareReset(params);
        List<ChargePointSelect> chargePointSelectList = params.getChargePointSelectList();
        RequestTask requestTask = new RequestTask(OcppVersion.V_12, "Reset", chargePointSelectList);

        for (ChargePointSelect c : chargePointSelectList) {
            String chargeBoxId = c.getChargeBoxId();
            ResetResponseHandler handler = new ResetResponseHandler(requestTask, chargeBoxId);
            create(c.getEndpointAddress()).resetAsync(req, chargeBoxId, handler);
        }

        return requestTaskStore.add(requestTask);
    }

    public int updateFirmware(UpdateFirmwareParams params) {
        UpdateFirmwareRequest req = this.prepareUpdateFirmware(params);
        List<ChargePointSelect> chargePointSelectList = params.getChargePointSelectList();
        RequestTask requestTask = new RequestTask(OcppVersion.V_12, "Update Firmware", chargePointSelectList);

        for (ChargePointSelect c : chargePointSelectList) {
            String chargeBoxId = c.getChargeBoxId();
            UpdateFirmwareResponseHandler handler = new UpdateFirmwareResponseHandler(requestTask, chargeBoxId);
            create(c.getEndpointAddress()).updateFirmwareAsync(req, chargeBoxId, handler);
        }

        return requestTaskStore.add(requestTask);
    }

    // -------------------------------------------------------------------------
    // Single Execution
    // -------------------------------------------------------------------------

    public int remoteStartTransaction(RemoteStartTransactionParams params) {
        RemoteStartTransactionRequest req = this.prepareRemoteStartTransaction(params);
        List<ChargePointSelect> chargePointSelectList = params.getChargePointSelectList();
        RequestTask requestTask = new RequestTask(OcppVersion.V_12, "Remote Start Transaction", chargePointSelectList);

        ChargePointSelect c = chargePointSelectList.get(0);

        String chargeBoxId = c.getChargeBoxId();
        RemoteStartTransactionResponseHandler handler = new RemoteStartTransactionResponseHandler(requestTask, chargeBoxId);
        create(c.getEndpointAddress()).remoteStartTransactionAsync(req, chargeBoxId, handler);

        return requestTaskStore.add(requestTask);
    }

    public int remoteStopTransaction(RemoteStopTransactionParams params) {
        RemoteStopTransactionRequest req = this.prepareRemoteStopTransaction(params);
        List<ChargePointSelect> chargePointSelectList = params.getChargePointSelectList();
        RequestTask requestTask = new RequestTask(OcppVersion.V_12, "Remote Stop Transaction", chargePointSelectList);

        ChargePointSelect c = chargePointSelectList.get(0);

        String chargeBoxId = c.getChargeBoxId();
        RemoteStopTransactionResponseHandler handler = new RemoteStopTransactionResponseHandler(requestTask, chargeBoxId);
        create(c.getEndpointAddress()).remoteStopTransactionAsync(req, chargeBoxId, handler);

        return requestTaskStore.add(requestTask);
    }

    public int unlockConnector(UnlockConnectorParams params) {
        UnlockConnectorRequest req = this.prepareUnlockConnector(params);
        List<ChargePointSelect> chargePointSelectList = params.getChargePointSelectList();
        RequestTask requestTask = new RequestTask(OcppVersion.V_12, "Unlock Connector", chargePointSelectList);

        ChargePointSelect c = chargePointSelectList.get(0);

        String chargeBoxId = c.getChargeBoxId();
        UnlockConnectorResponseHandler handler = new UnlockConnectorResponseHandler(requestTask, chargeBoxId);
        create(c.getEndpointAddress()).unlockConnectorAsync(req, chargeBoxId, handler);

        return requestTaskStore.add(requestTask);
    }
}