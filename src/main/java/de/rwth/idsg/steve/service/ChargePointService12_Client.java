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
import de.rwth.idsg.steve.web.dto.common.GetDiagnosticsParams;
import de.rwth.idsg.steve.web.dto.common.MultipleChargePointSelect;
import de.rwth.idsg.steve.web.dto.common.RemoteStartTransactionParams;
import de.rwth.idsg.steve.web.dto.common.RemoteStopTransactionParams;
import de.rwth.idsg.steve.web.dto.common.UnlockConnectorParams;
import de.rwth.idsg.steve.web.dto.common.UpdateFirmwareParams;
import de.rwth.idsg.steve.web.dto.ocpp12.ChangeAvailabilityParams;
import de.rwth.idsg.steve.web.dto.ocpp12.ChangeConfigurationParams;
import de.rwth.idsg.steve.web.dto.ocpp12.ResetParams;
import de.rwth.idsg.steve.web.dto.task.RequestTask;
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
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

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

    @Autowired private ScheduledExecutorService executorService;
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
                .withKey(params.getKey())
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
        List<ChargePointSelect> list = params.getChargePointSelectList();
        RequestTask<ChangeAvailabilityRequest> task = new RequestTask<>(VERSION, req, list);

        execute(list,
                c -> dispatcher.changeAvailability(
                        c, new ChangeAvailabilityResponseHandler(task, c.getChargeBoxId())));

        return requestTaskStore.add(task);
    }

    public int changeConfiguration(ChangeConfigurationParams params) {
        ChangeConfigurationRequest req = this.prepareChangeConfiguration(params);
        List<ChargePointSelect> list = params.getChargePointSelectList();
        RequestTask<ChangeConfigurationRequest> task = new RequestTask<>(VERSION, req, list);

        execute(list,
                c -> dispatcher.changeConfiguration(
                        c, new ChangeConfigurationResponseHandler(task, c.getChargeBoxId())));

        return requestTaskStore.add(task);
    }

    public int clearCache(MultipleChargePointSelect params) {
        ClearCacheRequest req = this.prepareClearCache();
        List<ChargePointSelect> list = params.getChargePointSelectList();
        RequestTask<ClearCacheRequest> task = new RequestTask<>(VERSION, req, list);

        execute(list,
                c -> dispatcher.clearCache(
                        c, new ClearCacheResponseHandler(task, c.getChargeBoxId())));

        return requestTaskStore.add(task);
    }

    public int getDiagnostics(GetDiagnosticsParams params) {
        GetDiagnosticsRequest req = this.prepareGetDiagnostics(params);
        List<ChargePointSelect> list = params.getChargePointSelectList();
        RequestTask<GetDiagnosticsRequest> task = new RequestTask<>(VERSION, req, list);

        execute(list,
                c -> dispatcher.getDiagnostics(
                        c, new GetDiagnosticsResponseHandler(task, c.getChargeBoxId())));

        return requestTaskStore.add(task);
    }

    public int reset(ResetParams params) {
        ResetRequest req = this.prepareReset(params);
        List<ChargePointSelect> list = params.getChargePointSelectList();
        RequestTask<ResetRequest> task = new RequestTask<>(VERSION, req, list);

        execute(list,
                c -> dispatcher.reset(
                        c, new ResetResponseHandler(task, c.getChargeBoxId())));

        return requestTaskStore.add(task);
    }

    public int updateFirmware(UpdateFirmwareParams params) {
        UpdateFirmwareRequest req = this.prepareUpdateFirmware(params);
        List<ChargePointSelect> list = params.getChargePointSelectList();
        RequestTask<UpdateFirmwareRequest> task = new RequestTask<>(VERSION, req, list);

        execute(list,
                c -> dispatcher.updateFirmware(
                        c, new UpdateFirmwareResponseHandler(task, c.getChargeBoxId())));

        return requestTaskStore.add(task);
    }

    /**
     * Executes the requests
     */
    private void execute(List<ChargePointSelect> list, Consumer<ChargePointSelect> consumer) {
        executorService.execute(() -> list.forEach(consumer));
    }

    // -------------------------------------------------------------------------
    // Single Execution
    // -------------------------------------------------------------------------

    public int remoteStartTransaction(RemoteStartTransactionParams params) {
        RemoteStartTransactionRequest req = this.prepareRemoteStartTransaction(params);
        List<ChargePointSelect> list = params.getChargePointSelectList();
        RequestTask<RemoteStartTransactionRequest> task = new RequestTask<>(VERSION, req, list);
        ChargePointSelect c = list.get(0);

        execute(() -> dispatcher.remoteStartTransaction(
                c, new RemoteStartTransactionResponseHandler(task, c.getChargeBoxId())));

        return requestTaskStore.add(task);
    }

    public int remoteStopTransaction(RemoteStopTransactionParams params) {
        RemoteStopTransactionRequest req = this.prepareRemoteStopTransaction(params);
        List<ChargePointSelect> list = params.getChargePointSelectList();
        RequestTask<RemoteStopTransactionRequest> task = new RequestTask<>(VERSION, req, list);
        ChargePointSelect c = list.get(0);

        execute(() -> dispatcher.remoteStopTransaction(
                c, new RemoteStopTransactionResponseHandler(task, c.getChargeBoxId())));

        return requestTaskStore.add(task);
    }

    public int unlockConnector(UnlockConnectorParams params) {
        UnlockConnectorRequest req = this.prepareUnlockConnector(params);
        List<ChargePointSelect> list = params.getChargePointSelectList();
        RequestTask<UnlockConnectorRequest> task = new RequestTask<>(VERSION, req, list);
        ChargePointSelect c = list.get(0);

        execute(() -> dispatcher.unlockConnector(
                c, new UnlockConnectorResponseHandler(task, c.getChargeBoxId())));

        return requestTaskStore.add(task);
    }

    /**
     * Executes the requests for single charge point invocation
     */
    private void execute(Runnable r) {
        executorService.execute(r);
    }
}
