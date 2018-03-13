package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.handler.ocpp15.CancelReservationResponseHandler;
import de.rwth.idsg.steve.handler.ocpp15.ChangeAvailabilityResponseHandler;
import de.rwth.idsg.steve.handler.ocpp15.ChangeConfigurationResponseHandler;
import de.rwth.idsg.steve.handler.ocpp15.ClearCacheResponseHandler;
import de.rwth.idsg.steve.handler.ocpp15.DataTransferResponseHandler;
import de.rwth.idsg.steve.handler.ocpp15.GetConfigurationResponseHandler;
import de.rwth.idsg.steve.handler.ocpp15.GetDiagnosticsResponseHandler;
import de.rwth.idsg.steve.handler.ocpp15.GetLocalListVersionResponseHandler;
import de.rwth.idsg.steve.handler.ocpp15.RemoteStartTransactionResponseHandler;
import de.rwth.idsg.steve.handler.ocpp15.RemoteStopTransactionResponseHandler;
import de.rwth.idsg.steve.handler.ocpp15.ReserveNowResponseHandler;
import de.rwth.idsg.steve.handler.ocpp15.ResetResponseHandler;
import de.rwth.idsg.steve.handler.ocpp15.SendLocalListResponseHandler;
import de.rwth.idsg.steve.handler.ocpp15.UnlockConnectorResponseHandler;
import de.rwth.idsg.steve.handler.ocpp15.UpdateFirmwareResponseHandler;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.repository.OcppTagRepository;
import de.rwth.idsg.steve.repository.RequestTaskStore;
import de.rwth.idsg.steve.repository.ReservationRepository;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.repository.dto.InsertReservationParams;
import de.rwth.idsg.steve.web.dto.common.GetDiagnosticsParams;
import de.rwth.idsg.steve.web.dto.common.MultipleChargePointSelect;
import de.rwth.idsg.steve.web.dto.common.RemoteStartTransactionParams;
import de.rwth.idsg.steve.web.dto.common.RemoteStopTransactionParams;
import de.rwth.idsg.steve.web.dto.common.UnlockConnectorParams;
import de.rwth.idsg.steve.web.dto.common.UpdateFirmwareParams;
import de.rwth.idsg.steve.web.dto.ocpp15.CancelReservationParams;
import de.rwth.idsg.steve.web.dto.ocpp15.ChangeAvailabilityParams;
import de.rwth.idsg.steve.web.dto.ocpp15.ChangeConfigurationParams;
import de.rwth.idsg.steve.web.dto.ocpp15.ConfigurationKeyEnum;
import de.rwth.idsg.steve.web.dto.ocpp15.DataTransferParams;
import de.rwth.idsg.steve.web.dto.ocpp15.GetConfigurationParams;
import de.rwth.idsg.steve.web.dto.ocpp15.ReserveNowParams;
import de.rwth.idsg.steve.web.dto.ocpp15.ResetParams;
import de.rwth.idsg.steve.web.dto.ocpp15.SendLocalListParams;
import de.rwth.idsg.steve.web.dto.task.RequestTask;
import lombok.extern.slf4j.Slf4j;
import ocpp.cp._2012._06.AuthorisationData;
import ocpp.cp._2012._06.CancelReservationRequest;
import ocpp.cp._2012._06.ChangeAvailabilityRequest;
import ocpp.cp._2012._06.ChangeConfigurationRequest;
import ocpp.cp._2012._06.ClearCacheRequest;
import ocpp.cp._2012._06.DataTransferRequest;
import ocpp.cp._2012._06.GetConfigurationRequest;
import ocpp.cp._2012._06.GetDiagnosticsRequest;
import ocpp.cp._2012._06.GetLocalListVersionRequest;
import ocpp.cp._2012._06.RemoteStartTransactionRequest;
import ocpp.cp._2012._06.RemoteStopTransactionRequest;
import ocpp.cp._2012._06.ReserveNowRequest;
import ocpp.cp._2012._06.ResetRequest;
import ocpp.cp._2012._06.SendLocalListRequest;
import ocpp.cp._2012._06.UnlockConnectorRequest;
import ocpp.cp._2012._06.UpdateFirmwareRequest;
import ocpp.cp._2012._06.UpdateType;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static de.rwth.idsg.steve.utils.DateTimeUtils.toDateTime;

/**
 * Transport-level agnostic client implementation of OCPP V1.5
 * which builds the request payloads and delegates to an invoker.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 */
@Slf4j
@Service
public class ChargePointService15_Client {
    private static final OcppVersion VERSION = OcppVersion.V_15;

    @Autowired private ScheduledExecutorService executorService;
    @Autowired private OcppTagRepository userRepository;
    @Autowired private OcppTagService ocppTagService;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired private RequestTaskStore requestTaskStore;
    @Autowired private ChargePointService15_Dispatcher dispatcher;

    // -------------------------------------------------------------------------
    // Create Request Payloads
    // -------------------------------------------------------------------------

    private static ChangeAvailabilityRequest prepareChangeAvailability(ChangeAvailabilityParams params) {
        return new ChangeAvailabilityRequest()
                .withConnectorId(params.getConnectorId())
                .withType(params.getAvailType());
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
                .withType(params.getResetType());
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

    /**
     * Dummy implementation. It must be vendor-specific.
     */
    private static DataTransferRequest prepareDataTransfer(DataTransferParams params) {
        return new DataTransferRequest()
                .withVendorId(params.getVendorId())
                .withMessageId(params.getMessageId())
                .withData(params.getData());
    }

    private static GetConfigurationRequest prepareGetConfiguration(GetConfigurationParams params) {
        if (params.isSetConfKeyList()) {
            List<String> stringList = params.getConfKeyList()
                                            .stream()
                                            .map(ConfigurationKeyEnum::value)
                                            .collect(Collectors.toList());

            return new GetConfigurationRequest().withKey(stringList);
        } else {
            return new GetConfigurationRequest();
        }
    }

    private static GetLocalListVersionRequest prepareGetLocalListVersion() {
        return new GetLocalListVersionRequest();
    }

    private SendLocalListRequest prepareSendLocalList(SendLocalListParams params) {
        // DIFFERENTIAL update
        if (UpdateType.DIFFERENTIAL.equals(params.getUpdateType())) {
            List<AuthorisationData> auths = new ArrayList<>();

            // Step 1: For the idTags to be deleted, insert only the idTag
            for (String idTag : params.getDeleteList()) {
                auths.add(new AuthorisationData().withIdTag(idTag));
            }

            // Step 2: For the idTags to be added or updated, insert them with their IdTagInfos
            auths.addAll(ocppTagService.getAuthData(params.getAddUpdateList()));

            return new SendLocalListRequest()
                    .withListVersion(params.getListVersion())
                    .withUpdateType(UpdateType.DIFFERENTIAL)
                    .withLocalAuthorisationList(auths);

        // FULL update
        } else {
            return new SendLocalListRequest()
                    .withListVersion(params.getListVersion())
                    .withUpdateType(UpdateType.FULL)
                    .withLocalAuthorisationList(ocppTagService.getAuthDataOfAllTags());
        }
    }

    private ReserveNowRequest prepareReserveNow(ReserveNowParams params, int reservationId) {
        String idTag = params.getIdTag();
        return new ReserveNowRequest()
                .withConnectorId(params.getConnectorId())
                .withReservationId(reservationId)
                .withExpiryDate(params.getExpiry().toDateTime())
                .withIdTag(idTag)
                .withParentIdTag(userRepository.getParentIdtag(idTag));
    }

    private static CancelReservationRequest prepareCancelReservation(CancelReservationParams params) {
        return new CancelReservationRequest()
                .withReservationId(params.getReservationId());
    }

    // -------------------------------------------------------------------------
    // Multiple Execution
    // -------------------------------------------------------------------------

    public int changeAvailability(ChangeAvailabilityParams params) {
        ChangeAvailabilityRequest req = prepareChangeAvailability(params);
        List<ChargePointSelect> list = params.getChargePointSelectList();
        RequestTask<ChangeAvailabilityRequest> task = new RequestTask<>(VERSION, req, list);

        execute(list,
                c -> dispatcher.changeAvailability(
                        c, new ChangeAvailabilityResponseHandler(task, c.getChargeBoxId())));

        return requestTaskStore.add(task);
    }

    public int changeConfiguration(ChangeConfigurationParams params) {
        ChangeConfigurationRequest req = prepareChangeConfiguration(params);
        List<ChargePointSelect> list = params.getChargePointSelectList();
        RequestTask<ChangeConfigurationRequest> task = new RequestTask<>(VERSION, req, list);

        execute(list,
                c -> dispatcher.changeConfiguration(
                        c, new ChangeConfigurationResponseHandler(task, c.getChargeBoxId())));

        return requestTaskStore.add(task);
    }

    public int clearCache(MultipleChargePointSelect params) {
        ClearCacheRequest req = prepareClearCache();
        List<ChargePointSelect> list = params.getChargePointSelectList();
        RequestTask<ClearCacheRequest> task = new RequestTask<>(VERSION, req, list);

        execute(list,
                c -> dispatcher.clearCache(
                        c, new ClearCacheResponseHandler(task, c.getChargeBoxId())));

        return requestTaskStore.add(task);
    }

    public int getDiagnostics(GetDiagnosticsParams params) {
        GetDiagnosticsRequest req = prepareGetDiagnostics(params);
        List<ChargePointSelect> list = params.getChargePointSelectList();
        RequestTask<GetDiagnosticsRequest> task = new RequestTask<>(VERSION, req, list);

        execute(list,
                c -> dispatcher.getDiagnostics(
                        c, new GetDiagnosticsResponseHandler(task, c.getChargeBoxId())));

        return requestTaskStore.add(task);
    }

    public int reset(ResetParams params) {
        ResetRequest req = prepareReset(params);
        List<ChargePointSelect> list = params.getChargePointSelectList();
        RequestTask<ResetRequest> task = new RequestTask<>(VERSION, req, list);

        execute(list,
                c -> dispatcher.reset(
                        c, new ResetResponseHandler(task, c.getChargeBoxId())));

        return requestTaskStore.add(task);
    }

    public int updateFirmware(UpdateFirmwareParams params) {
        UpdateFirmwareRequest req = prepareUpdateFirmware(params);
        List<ChargePointSelect> list = params.getChargePointSelectList();
        RequestTask<UpdateFirmwareRequest> task = new RequestTask<>(VERSION, req, list);

        execute(list,
                c -> dispatcher.updateFirmware(
                        c, new UpdateFirmwareResponseHandler(task, c.getChargeBoxId())));

        return requestTaskStore.add(task);
    }

    public int dataTransfer(DataTransferParams params) {
        DataTransferRequest req = prepareDataTransfer(params);
        List<ChargePointSelect> list = params.getChargePointSelectList();
        RequestTask<DataTransferRequest> task = new RequestTask<>(VERSION, req, list);

        execute(list,
                c -> dispatcher.dataTransfer(
                        c, new DataTransferResponseHandler(task, c.getChargeBoxId())));

        return requestTaskStore.add(task);
    }

    public int getConfiguration(GetConfigurationParams params) {
        GetConfigurationRequest req = prepareGetConfiguration(params);
        List<ChargePointSelect> list = params.getChargePointSelectList();
        RequestTask<GetConfigurationRequest> task = new RequestTask<>(VERSION, req, list);

        execute(list,
                c -> dispatcher.getConfiguration(
                        c, new GetConfigurationResponseHandler(task, c.getChargeBoxId())));

        return requestTaskStore.add(task);
    }

    public int getLocalListVersion(MultipleChargePointSelect params) {
        GetLocalListVersionRequest req = prepareGetLocalListVersion();
        List<ChargePointSelect> list = params.getChargePointSelectList();
        RequestTask<GetLocalListVersionRequest> task = new RequestTask<>(VERSION, req, list);

        execute(list,
                c -> dispatcher.getLocalListVersion(
                        c, new GetLocalListVersionResponseHandler(task, c.getChargeBoxId())));

        return requestTaskStore.add(task);
    }

    public int sendLocalList(SendLocalListParams params) {
        SendLocalListRequest req = prepareSendLocalList(params);
        List<ChargePointSelect> list = params.getChargePointSelectList();
        RequestTask<SendLocalListRequest> task = new RequestTask<>(VERSION, req, list);

        execute(list,
                c -> dispatcher.sendLocalList(
                        c, new SendLocalListResponseHandler(task, c.getChargeBoxId())));

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
        RemoteStartTransactionRequest req = prepareRemoteStartTransaction(params);
        List<ChargePointSelect> list = params.getChargePointSelectList();
        RequestTask<RemoteStartTransactionRequest> task = new RequestTask<>(VERSION, req, list);
        ChargePointSelect c = list.get(0);

        execute(() -> dispatcher.remoteStartTransaction(
                c, new RemoteStartTransactionResponseHandler(task, c.getChargeBoxId())));

        return requestTaskStore.add(task);
    }

    public int remoteStopTransaction(RemoteStopTransactionParams params) {
        RemoteStopTransactionRequest req = prepareRemoteStopTransaction(params);
        List<ChargePointSelect> list = params.getChargePointSelectList();
        RequestTask<RemoteStopTransactionRequest> task = new RequestTask<>(VERSION, req, list);
        ChargePointSelect c = list.get(0);

        execute(() -> dispatcher.remoteStopTransaction(
                c, new RemoteStopTransactionResponseHandler(task, c.getChargeBoxId())));

        return requestTaskStore.add(task);
    }

    public int unlockConnector(UnlockConnectorParams params) {
        UnlockConnectorRequest req = prepareUnlockConnector(params);
        List<ChargePointSelect> list = params.getChargePointSelectList();
        RequestTask<UnlockConnectorRequest> task = new RequestTask<>(VERSION, req, list);
        ChargePointSelect c = list.get(0);

        execute(() -> dispatcher.unlockConnector(
                c, new UnlockConnectorResponseHandler(task, c.getChargeBoxId())));

        return requestTaskStore.add(task);
    }

    public int reserveNow(ReserveNowParams params) {
        List<ChargePointSelect> list = params.getChargePointSelectList();
        ChargePointSelect c = list.get(0);
        String chargeBoxId = c.getChargeBoxId();

        InsertReservationParams res = InsertReservationParams.builder()
                                                             .idTag(params.getIdTag())
                                                             .chargeBoxId(chargeBoxId)
                                                             .connectorId(params.getConnectorId())
                                                             .startTimestamp(DateTime.now())
                                                             .expiryTimestamp(params.getExpiry().toDateTime())
                                                             .build();

        int reservationId = reservationRepository.insert(res);

        ReserveNowRequest req = this.prepareReserveNow(params, reservationId);
        RequestTask<ReserveNowRequest> task = new RequestTask<>(VERSION, req, list);

        execute(() -> dispatcher.reserveNow(
                c, new ReserveNowResponseHandler(task, chargeBoxId, reservationRepository)));

        return requestTaskStore.add(task);
    }

    public int cancelReservation(CancelReservationParams params) {
        CancelReservationRequest req = prepareCancelReservation(params);
        List<ChargePointSelect> list = params.getChargePointSelectList();
        RequestTask<CancelReservationRequest> task = new RequestTask<>(VERSION, req, list);
        ChargePointSelect c = list.get(0);

        execute(() -> dispatcher.cancelReservation(
                c, new CancelReservationResponseHandler(task, c.getChargeBoxId(), reservationRepository)));

        return requestTaskStore.add(task);
    }

    /**
     * Executes the requests for single charge point invocation
     */
    private void execute(Runnable r) {
        executorService.execute(r);
    }
}
