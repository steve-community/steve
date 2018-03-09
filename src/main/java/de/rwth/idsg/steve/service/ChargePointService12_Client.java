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
import de.rwth.idsg.steve.ocpp.ChargePointService12_Invoker;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.soap.ChargePointService12_SoapInvoker;
import de.rwth.idsg.steve.ocpp.ws.ocpp12.ChargePointService12_WsInvoker;
import de.rwth.idsg.steve.repository.RequestTaskStore;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.web.dto.ocpp.ChangeAvailabilityParams;
import de.rwth.idsg.steve.web.dto.ocpp.ChangeConfigurationParams;
import de.rwth.idsg.steve.web.dto.ocpp.GetDiagnosticsParams;
import de.rwth.idsg.steve.web.dto.ocpp.MultipleChargePointSelect;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStartTransactionParams;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStopTransactionParams;
import de.rwth.idsg.steve.web.dto.ocpp.ResetParams;
import de.rwth.idsg.steve.web.dto.ocpp.UnlockConnectorParams;
import de.rwth.idsg.steve.web.dto.ocpp.UpdateFirmwareParams;
import de.rwth.idsg.steve.web.dto.task.RequestTask;
import lombok.extern.slf4j.Slf4j;
import ocpp.cp._2010._08.AvailabilityType;
import ocpp.cp._2010._08.ChangeAvailabilityRequest;
import ocpp.cp._2010._08.ChangeConfigurationRequest;
import ocpp.cp._2010._08.ClearCacheRequest;
import ocpp.cp._2010._08.GetDiagnosticsRequest;
import ocpp.cp._2010._08.RemoteStartTransactionRequest;
import ocpp.cp._2010._08.RemoteStopTransactionRequest;
import ocpp.cp._2010._08.ResetRequest;
import ocpp.cp._2010._08.ResetType;
import ocpp.cp._2010._08.UnlockConnectorRequest;
import ocpp.cp._2010._08.UpdateFirmwareRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import static de.rwth.idsg.steve.utils.DateTimeUtils.toDateTime;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 */
@Slf4j
@Service
public class ChargePointService12_Client {
    private static final OcppVersion VERSION = OcppVersion.V_12;

    @Autowired private ScheduledExecutorService executorService;
    @Autowired private RequestTaskStore requestTaskStore;

    @Autowired private ChargePointService12_SoapInvoker soapInvoker;
    @Autowired private ChargePointService12_WsInvoker wsInvoker;

    // -------------------------------------------------------------------------
    // Create Request Payloads
    // -------------------------------------------------------------------------

    private static ChangeAvailabilityRequest prepareChangeAvailability(ChangeAvailabilityParams params) {
        return new ChangeAvailabilityRequest()
                .withConnectorId(params.getConnectorId())
                .withType(AvailabilityType.fromValue(params.getAvailType().value()));
    }

    private static ChangeConfigurationRequest prepareChangeConfiguration(ChangeConfigurationParams params) {
        return new ChangeConfigurationRequest()
                .withKey(params.getKey())
                .withValue(params.getValue());
    }

    private static ClearCacheRequest prepareClearCache() {
        return new ClearCacheRequest();
    }

    private static GetDiagnosticsRequest prepareGetDiagnostics(GetDiagnosticsParams params) {
        return new GetDiagnosticsRequest()
                .withLocation(params.getLocation())
                .withRetries(params.getRetries())
                .withRetryInterval(params.getRetryInterval())
                .withStartTime(toDateTime(params.getStart()))
                .withStopTime(toDateTime(params.getStop()));
    }

    private static RemoteStartTransactionRequest prepareRemoteStartTransaction(RemoteStartTransactionParams params) {
        return new RemoteStartTransactionRequest()
                .withIdTag(params.getIdTag())
                .withConnectorId(params.getConnectorId());
    }

    private static RemoteStopTransactionRequest prepareRemoteStopTransaction(RemoteStopTransactionParams params) {
        return new RemoteStopTransactionRequest()
                .withTransactionId(params.getTransactionId());
    }

    private static ResetRequest prepareReset(ResetParams params) {
        return new ResetRequest()
                .withType(ResetType.fromValue(params.getResetType().value()));
    }

    private static UnlockConnectorRequest prepareUnlockConnector(UnlockConnectorParams params) {
        return new UnlockConnectorRequest()
                .withConnectorId(params.getConnectorId());
    }

    private static UpdateFirmwareRequest prepareUpdateFirmware(UpdateFirmwareParams params) {
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
        ChangeAvailabilityRequest req = prepareChangeAvailability(params);
        List<ChargePointSelect> list = params.getChargePointSelectList();
        RequestTask<ChangeAvailabilityRequest> task = new RequestTask<>(VERSION, req, list);

        BackgroundService.with(executorService)
                         .forEach(list)
                         .execute(c -> getInvoker(c).changeAvailability(c, new ChangeAvailabilityResponseHandler(task, c.getChargeBoxId())));

        return requestTaskStore.add(task);
    }

    public int changeConfiguration(ChangeConfigurationParams params) {
        ChangeConfigurationRequest req = prepareChangeConfiguration(params);
        List<ChargePointSelect> list = params.getChargePointSelectList();
        RequestTask<ChangeConfigurationRequest> task = new RequestTask<>(VERSION, req, list);

        BackgroundService.with(executorService)
                         .forEach(list)
                         .execute(c -> getInvoker(c).changeConfiguration(c, new ChangeConfigurationResponseHandler(task, c.getChargeBoxId())));

        return requestTaskStore.add(task);
    }

    public int clearCache(MultipleChargePointSelect params) {
        ClearCacheRequest req = prepareClearCache();
        List<ChargePointSelect> list = params.getChargePointSelectList();
        RequestTask<ClearCacheRequest> task = new RequestTask<>(VERSION, req, list);

        BackgroundService.with(executorService)
                         .forEach(list)
                         .execute(c -> getInvoker(c).clearCache(c, new ClearCacheResponseHandler(task, c.getChargeBoxId())));

        return requestTaskStore.add(task);
    }

    public int getDiagnostics(GetDiagnosticsParams params) {
        GetDiagnosticsRequest req = prepareGetDiagnostics(params);
        List<ChargePointSelect> list = params.getChargePointSelectList();
        RequestTask<GetDiagnosticsRequest> task = new RequestTask<>(VERSION, req, list);

        BackgroundService.with(executorService)
                         .forEach(list)
                         .execute(c -> getInvoker(c).getDiagnostics(c, new GetDiagnosticsResponseHandler(task, c.getChargeBoxId())));

        return requestTaskStore.add(task);
    }

    public int reset(ResetParams params) {
        ResetRequest req = prepareReset(params);
        List<ChargePointSelect> list = params.getChargePointSelectList();
        RequestTask<ResetRequest> task = new RequestTask<>(VERSION, req, list);

        BackgroundService.with(executorService)
                         .forEach(list)
                         .execute(c -> getInvoker(c).reset(c, new ResetResponseHandler(task, c.getChargeBoxId())));

        return requestTaskStore.add(task);
    }

    public int updateFirmware(UpdateFirmwareParams params) {
        UpdateFirmwareRequest req = prepareUpdateFirmware(params);
        List<ChargePointSelect> list = params.getChargePointSelectList();
        RequestTask<UpdateFirmwareRequest> task = new RequestTask<>(VERSION, req, list);

        BackgroundService.with(executorService)
                         .forEach(list)
                         .execute(c -> getInvoker(c).updateFirmware(c, new UpdateFirmwareResponseHandler(task, c.getChargeBoxId())));

        return requestTaskStore.add(task);
    }

    // -------------------------------------------------------------------------
    // Single Execution
    // -------------------------------------------------------------------------

    public int remoteStartTransaction(RemoteStartTransactionParams params) {
        RemoteStartTransactionRequest req = prepareRemoteStartTransaction(params);
        List<ChargePointSelect> list = params.getChargePointSelectList();
        RequestTask<RemoteStartTransactionRequest> task = new RequestTask<>(VERSION, req, list);

        BackgroundService.with(executorService)
                         .forFirst(list)
                         .execute(c -> getInvoker(c).remoteStartTransaction(c, new RemoteStartTransactionResponseHandler(task, c.getChargeBoxId())));

        return requestTaskStore.add(task);
    }

    public int remoteStopTransaction(RemoteStopTransactionParams params) {
        RemoteStopTransactionRequest req = prepareRemoteStopTransaction(params);
        List<ChargePointSelect> list = params.getChargePointSelectList();
        RequestTask<RemoteStopTransactionRequest> task = new RequestTask<>(VERSION, req, list);

        BackgroundService.with(executorService)
                         .forFirst(list)
                         .execute(c -> getInvoker(c).remoteStopTransaction(c, new RemoteStopTransactionResponseHandler(task, c.getChargeBoxId())));

        return requestTaskStore.add(task);
    }

    public int unlockConnector(UnlockConnectorParams params) {
        UnlockConnectorRequest req = prepareUnlockConnector(params);
        List<ChargePointSelect> list = params.getChargePointSelectList();
        RequestTask<UnlockConnectorRequest> task = new RequestTask<>(VERSION, req, list);

        BackgroundService.with(executorService)
                         .forFirst(list)
                         .execute(c -> getInvoker(c).unlockConnector(c, new UnlockConnectorResponseHandler(task, c.getChargeBoxId())));

        return requestTaskStore.add(task);
    }

    private ChargePointService12_Invoker getInvoker(ChargePointSelect cp) {
        if (cp.isSoap()) {
            return soapInvoker;
        } else {
            return wsInvoker;
        }
    }
}
