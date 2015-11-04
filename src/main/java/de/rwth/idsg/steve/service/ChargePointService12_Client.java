package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.handler.ocpp12.ChangeAvailabilityResponseHandler;
import de.rwth.idsg.steve.handler.ocpp12.ChangeConfigurationResponseHandler;
import de.rwth.idsg.steve.handler.ocpp12.ClearCacheResponseHandler;
import de.rwth.idsg.steve.handler.ocpp12.GetDiagnosticsResponseHandler;
import de.rwth.idsg.steve.handler.ocpp12.RemoteStartTransactionResponseHandler;
import de.rwth.idsg.steve.handler.ocpp12.RemoteStopTransactionResponseHandler;
import de.rwth.idsg.steve.handler.ocpp12.ResetResponseHandler;
import de.rwth.idsg.steve.handler.ocpp12.UnlockConnectorResponseHandler;
import de.rwth.idsg.steve.handler.ocpp12.UpdateFirmwareResponseHandler;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.repository.RequestTaskStore;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.web.dto.RequestTask;
import de.rwth.idsg.steve.web.dto.common.GetDiagnosticsParams;
import de.rwth.idsg.steve.web.dto.common.MultipleChargePointSelect;
import de.rwth.idsg.steve.web.dto.common.RemoteStartTransactionParams;
import de.rwth.idsg.steve.web.dto.common.RemoteStopTransactionParams;
import de.rwth.idsg.steve.web.dto.common.UnlockConnectorParams;
import de.rwth.idsg.steve.web.dto.common.UpdateFirmwareParams;
import de.rwth.idsg.steve.web.dto.ocpp12.ChangeAvailabilityParams;
import de.rwth.idsg.steve.web.dto.ocpp12.ChangeConfigurationParams;
import de.rwth.idsg.steve.web.dto.ocpp12.ResetParams;
import lombok.extern.slf4j.Slf4j;
import ocpp.cp._2010._08.ChangeAvailabilityRequest;
import ocpp.cp._2010._08.ChangeConfigurationRequest;
import ocpp.cp._2010._08.ClearCacheRequest;
import ocpp.cp._2010._08.GetDiagnosticsRequest;
import ocpp.cp._2010._08.RemoteStartTransactionRequest;
import ocpp.cp._2010._08.RemoteStopTransactionRequest;
import ocpp.cp._2010._08.ResetRequest;
import ocpp.cp._2010._08.UnlockConnectorRequest;
import ocpp.cp._2010._08.UpdateFirmwareRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static de.rwth.idsg.steve.utils.DateTimeUtils.toDateTime;

/**
 * Transport-level agnostic client implementation of OCPP V1.2
 * which builds the request payloads and delegates to dispatcher.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 */
@Slf4j
@Service
public class ChargePointService12_Client {
    private static final OcppVersion VERSION = OcppVersion.V_12;

    @Autowired private RequestTaskStore requestTaskStore;
    @Autowired private ChargePointService12_Dispatcher dispatcher;

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
                .withStartTime(toDateTime(params.getStart()))
                .withStopTime(toDateTime(params.getStop()));
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

    private ResetRequest prepareReset(ResetParams params) {
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
                .withRetrieveDate(toDateTime(params.getRetrieve()))
                .withRetries(params.getRetries())
                .withRetryInterval(params.getRetryInterval());
    }

    // -------------------------------------------------------------------------
    // Multiple Execution
    // -------------------------------------------------------------------------

    public int changeAvailability(ChangeAvailabilityParams params) {
        ChangeAvailabilityRequest req = this.prepareChangeAvailability(params);
        List<ChargePointSelect> chargePointSelectList = params.getChargePointSelectList();
        RequestTask requestTask = new RequestTask(VERSION, req, chargePointSelectList);

        for (ChargePointSelect c : chargePointSelectList) {
            ChangeAvailabilityResponseHandler handler =
                    new ChangeAvailabilityResponseHandler(requestTask, c.getChargeBoxId());
            dispatcher.changeAvailability(c, req, handler);
        }
        return requestTaskStore.add(requestTask);
    }

    public int changeConfiguration(ChangeConfigurationParams params) {
        ChangeConfigurationRequest req = this.prepareChangeConfiguration(params);
        List<ChargePointSelect> chargePointSelectList = params.getChargePointSelectList();
        RequestTask requestTask = new RequestTask(VERSION, req, chargePointSelectList);

        for (ChargePointSelect c : chargePointSelectList) {
            ChangeConfigurationResponseHandler handler =
                    new ChangeConfigurationResponseHandler(requestTask, c.getChargeBoxId());
            dispatcher.changeConfiguration(c, req, handler);
        }
        return requestTaskStore.add(requestTask);
    }

    public int clearCache(MultipleChargePointSelect params) {
        ClearCacheRequest req = this.prepareClearCache();
        List<ChargePointSelect> chargePointSelectList = params.getChargePointSelectList();
        RequestTask requestTask = new RequestTask(VERSION, req, chargePointSelectList);

        for (ChargePointSelect c : chargePointSelectList) {
            ClearCacheResponseHandler handler = new ClearCacheResponseHandler(requestTask, c.getChargeBoxId());
            dispatcher.clearCache(c, req, handler);
        }
        return requestTaskStore.add(requestTask);
    }

    public int getDiagnostics(GetDiagnosticsParams params) {
        GetDiagnosticsRequest req = this.prepareGetDiagnostics(params);
        List<ChargePointSelect> chargePointSelectList = params.getChargePointSelectList();
        RequestTask requestTask = new RequestTask(VERSION, req, chargePointSelectList);

        for (ChargePointSelect c : chargePointSelectList) {
            GetDiagnosticsResponseHandler handler = new GetDiagnosticsResponseHandler(requestTask, c.getChargeBoxId());
            dispatcher.getDiagnostics(c, req, handler);
        }
        return requestTaskStore.add(requestTask);
    }

    public int reset(ResetParams params) {
        ResetRequest req = this.prepareReset(params);
        List<ChargePointSelect> chargePointSelectList = params.getChargePointSelectList();
        RequestTask requestTask = new RequestTask(VERSION, req, chargePointSelectList);

        for (ChargePointSelect c : chargePointSelectList) {
            ResetResponseHandler handler = new ResetResponseHandler(requestTask, c.getChargeBoxId());
            dispatcher.reset(c, req, handler);
        }
        return requestTaskStore.add(requestTask);
    }

    public int updateFirmware(UpdateFirmwareParams params) {
        UpdateFirmwareRequest req = this.prepareUpdateFirmware(params);
        List<ChargePointSelect> chargePointSelectList = params.getChargePointSelectList();
        RequestTask requestTask = new RequestTask(VERSION, req, chargePointSelectList);

        for (ChargePointSelect c : chargePointSelectList) {
            UpdateFirmwareResponseHandler handler = new UpdateFirmwareResponseHandler(requestTask, c.getChargeBoxId());
            dispatcher.updateFirmware(c, req, handler);
        }
        return requestTaskStore.add(requestTask);
    }

    // -------------------------------------------------------------------------
    // Single Execution
    // -------------------------------------------------------------------------

    public int remoteStartTransaction(RemoteStartTransactionParams params) {
        RemoteStartTransactionRequest req = this.prepareRemoteStartTransaction(params);
        List<ChargePointSelect> chargePointSelectList = params.getChargePointSelectList();
        RequestTask requestTask = new RequestTask(VERSION, req, chargePointSelectList);

        ChargePointSelect c = chargePointSelectList.get(0);
        RemoteStartTransactionResponseHandler handler =
                new RemoteStartTransactionResponseHandler(requestTask, c.getChargeBoxId());
        dispatcher.remoteStartTransaction(c, req, handler);
        return requestTaskStore.add(requestTask);
    }

    public int remoteStopTransaction(RemoteStopTransactionParams params) {
        RemoteStopTransactionRequest req = this.prepareRemoteStopTransaction(params);
        List<ChargePointSelect> chargePointSelectList = params.getChargePointSelectList();
        RequestTask requestTask = new RequestTask(VERSION, req, chargePointSelectList);

        ChargePointSelect c = chargePointSelectList.get(0);
        RemoteStopTransactionResponseHandler handler =
                new RemoteStopTransactionResponseHandler(requestTask, c.getChargeBoxId());
        dispatcher.remoteStopTransaction(c, req, handler);
        return requestTaskStore.add(requestTask);
    }

    public int unlockConnector(UnlockConnectorParams params) {
        UnlockConnectorRequest req = this.prepareUnlockConnector(params);
        List<ChargePointSelect> chargePointSelectList = params.getChargePointSelectList();
        RequestTask requestTask = new RequestTask(VERSION, req, chargePointSelectList);

        ChargePointSelect c = chargePointSelectList.get(0);
        UnlockConnectorResponseHandler handler = new UnlockConnectorResponseHandler(requestTask, c.getChargeBoxId());
        dispatcher.unlockConnector(c, req, handler);
        return requestTaskStore.add(requestTask);
    }
}
