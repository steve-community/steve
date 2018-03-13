/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rwth.idsg.steve.service;

import com.sun.org.apache.xpath.internal.operations.Bool;
import de.rwth.idsg.steve.handler.ocpp16.*;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.repository.OcppTagRepository;
import de.rwth.idsg.steve.repository.RequestTaskStore;
import de.rwth.idsg.steve.repository.ReservationRepository;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.repository.dto.InsertReservationParams;
import de.rwth.idsg.steve.utils.JodaDateTimeConverter;
import de.rwth.idsg.steve.web.dto.common.GetDiagnosticsParams;
import de.rwth.idsg.steve.web.dto.common.MultipleChargePointSelect;
import de.rwth.idsg.steve.web.dto.common.RemoteStartTransactionParams;
import de.rwth.idsg.steve.web.dto.common.RemoteStopTransactionParams;
import de.rwth.idsg.steve.web.dto.common.UnlockConnectorParams;
import de.rwth.idsg.steve.web.dto.common.UpdateFirmwareParams;
import de.rwth.idsg.steve.web.dto.ocpp16.*;
import de.rwth.idsg.steve.web.dto.task.RequestTask;
import lombok.extern.slf4j.Slf4j;
import ocpp.cp._2015._10.*;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static de.rwth.idsg.steve.utils.DateTimeUtils.toDateTime;

/**
 *
 * @author david
 */
@Slf4j
@Service
public class ChargePointService16_Client {
    private static final OcppVersion VERSION = OcppVersion.V_16;

    @Autowired private ScheduledExecutorService executorService;
    @Autowired private OcppTagRepository userRepository;
    @Autowired private OcppTagService ocppTagService;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired private RequestTaskStore requestTaskStore;
    @Autowired private ChargePointService16_Dispatcher dispatcher;

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

    private static SetChargingProfileRequest prepareSetChargingProfile(SetChargingProfileParams params) {
        //TODO Fix TypeConstraintViolation??
        //OCA Example as in Specification
        List<ChargingSchedulePeriod> cspList = new ArrayList<>();

        cspList.add(new ChargingSchedulePeriod()
                .withStartPeriod(0) // = 00:00
                .withLimit(new BigDecimal("11000"))
                .withNumberPhases(3));
        cspList.add(new ChargingSchedulePeriod()
                .withStartPeriod(28800) // = 08:00
                .withLimit(new BigDecimal("6000"))
                .withNumberPhases(3));
        cspList.add(new ChargingSchedulePeriod()
                .withStartPeriod(72000) // = 20:00
                .withLimit(new BigDecimal("11000"))
                .withNumberPhases(3));

        return new SetChargingProfileRequest()
                .withConnectorId(2)
                .withCsChargingProfiles(new ChargingProfile()
                        .withChargingProfileId(100)
                        .withStackLevel(0)
                        .withChargingProfilePurpose(ChargingProfilePurposeType.TX_DEFAULT_PROFILE)
                        .withChargingProfileKind(ChargingProfileKindType.RECURRING)
                        .withRecurrencyKind(RecurrencyKindType.DAILY)
                        .withChargingSchedule(new ChargingSchedule()
                                .withDuration(86400) //24 hours
                                .withStartSchedule(new DateTime("2013-01-01T00:00Z"))
                                //.withStartSchedule(params.getStartSchedule().toDateTime())
                                .withChargingRateUnit(ChargingRateUnitType.W)
                                .withChargingSchedulePeriod(cspList)));

        /*return new SetChargingProfileRequest()
                .withConnectorId(params.getConnectorId())
                .withCsChargingProfiles(new ChargingProfile().withChargingProfileId(params.getChargingProfileId())
                        //.withTransactionId(params.getTransactionId)
                        //.withTransactionId(null) //Can't Set Charging Profile outside transaction momentarily
                        .withStackLevel(params.getStackLevel())
                        .withChargingProfilePurpose(params.getChargingProfilePurpose())
                        .withChargingProfileKind(params.getChargingProfileKind())
                        .withRecurrencyKind(params.getRecurrencyKind())
                        .withValidFrom(params.getValidFrom().toDateTime())
                        .withValidTo(params.getValidTo().toDateTime())
                        .withChargingSchedule(new ChargingSchedule()
                                .withDuration(params.getDuration())
                                .withStartSchedule(params.getStartSchedule().toDateTime())
                                .withChargingRateUnit(params.getChargingRateUnit())
                                .withChargingSchedulePeriod(new ChargingSchedulePeriod()
                                        .withStartPeriod(params.getStartPeriod())
                                        .withLimit(params.getLimit())
                                        .withNumberPhases(params.getNumberPhases()))
                                .withMinChargingRate(params.getMinChargingRate())));*/
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

    private static ClearChargingProfileRequest prepareClearChargingProfile(ClearChargingProfileParams params) {
        if(params.isSetConnectorId())
        {
            return new ClearChargingProfileRequest()
                    .withChargingProfilePurpose(params.getChargingProfilePurpose())
                    .withConnectorId(params.getConnectorId())
                    .withId(params.getId())
                    .withStackLevel(params.getStackLevel());
        }
        else
        {
            return new ClearChargingProfileRequest()
                    .withChargingProfilePurpose(params.getChargingProfilePurpose())
                    .withId(params.getId())
                    .withStackLevel(params.getStackLevel());
        }
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

    private static TriggerMessageRequest prepareTriggerMessage(TriggerMessageParams params) {
        MessageTrigger TriggerMessage = params.getTriggerMessage();
        if (params.isSetConnectorId())
        {
            return new TriggerMessageRequest().withRequestedMessage(TriggerMessage).withConnectorId(params.getConnectorId());
        } else {
            return new TriggerMessageRequest().withRequestedMessage(TriggerMessage);
        }
    }

    private static GetCompositeScheduleRequest prepareGetCompositeSchedule(GetCompositeScheduleParams params) {
        ChargingRateUnitType unitType = params.getChargingRateUnit();
        if (params.isSetChargingRateUnit())
        {
            return new GetCompositeScheduleRequest().withChargingRateUnit(unitType).withConnectorId(params.getConnectorId()).withDuration(params.getDuration());
        } else {
            return new GetCompositeScheduleRequest().withConnectorId(params.getConnectorId()).withDuration(params.getDuration());
        }
    }

    private static GetLocalListVersionRequest prepareGetLocalListVersion() {
        return new GetLocalListVersionRequest();
    }

    private SendLocalListRequest prepareSendLocalList(SendLocalListParams params) {
        // DIFFERENTIAL update
        if (UpdateType.DIFFERENTIAL.equals(params.getUpdateType())) {
            List<AuthorizationData> auths = new ArrayList<>();

            // Step 1: For the idTags to be deleted, insert only the idTag
            for (String idTag : params.getDeleteList()) {
                auths.add(new AuthorizationData().withIdTag(idTag));
            }

            // Step 2: For the idTags to be added or updated, insert them with their IdTagInfos
            auths.addAll(ocppTagService.getAuthData16(params.getAddUpdateList()));

            return new SendLocalListRequest()
                    .withListVersion(params.getListVersion())
                    .withUpdateType(UpdateType.DIFFERENTIAL)
                    .withLocalAuthorizationList(auths);

        // FULL update
        } else {
            return new SendLocalListRequest()
                    .withListVersion(params.getListVersion())
                    .withUpdateType(UpdateType.FULL)
                    .withLocalAuthorizationList(ocppTagService.getAuthDataOfAllTags16());
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

    public int clearChargingProfile(ClearChargingProfileParams params) {
        ClearChargingProfileRequest req = prepareClearChargingProfile(params);
        List<ChargePointSelect> list = params.getChargePointSelectList();
        RequestTask<ClearChargingProfileRequest> task = new RequestTask<>(VERSION, req, list);

        execute(list,
                c -> dispatcher.clearChargingProfile(
                        c, new ClearChargingProfileResponseHandler(task, c.getChargeBoxId())));

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

    public int setChargingProfile(SetChargingProfileParams params)
    {
        List<ChargePointSelect> list = params.getChargePointSelectList();
        ChargePointSelect c = list.get(0);
        SetChargingProfileRequest req = prepareSetChargingProfile(params);

        RequestTask<SetChargingProfileRequest> task = new RequestTask<>(VERSION, req, list);


        execute(() -> dispatcher.setChargingProfile(
                c, new SetChargingProfileResponseHandler(task, c.getChargeBoxId())));

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

    public int triggerMessage(TriggerMessageParams params) {
        TriggerMessageRequest req = prepareTriggerMessage(params);
        List<ChargePointSelect> list = params.getChargePointSelectList();
        RequestTask<TriggerMessageRequest> task = new RequestTask<>(VERSION, req, list);
        ChargePointSelect c = list.get(0);

        execute(() -> dispatcher.triggerMessage(
                c, new TriggerMessageResponseHandler(task, c.getChargeBoxId())));

        return requestTaskStore.add(task);
    }

    public int getCompositeSchedule(GetCompositeScheduleParams params) {
        GetCompositeScheduleRequest req = prepareGetCompositeSchedule(params);
        List<ChargePointSelect> list = params.getChargePointSelectList();
        RequestTask<GetCompositeScheduleRequest> task = new RequestTask<>(VERSION, req, list);
        ChargePointSelect c = list.get(0);

        execute(() -> dispatcher.getCompositeSchedule(
                c, new GetCompositeScheduleResponseHandler(task, c.getChargeBoxId())));

        return requestTaskStore.add(task);
    }

    /**
     * Executes the requests for single charge point invocation
     */
    private void execute(Runnable r) {
        executorService.execute(r);
    }
}
