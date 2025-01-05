/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2025 SteVe Community Team
 * All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.ocpp.ChargePointServiceInvoker;
import de.rwth.idsg.steve.ocpp.ChargePointServiceInvokerImpl;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.task.CancelReservationTask;
import de.rwth.idsg.steve.ocpp.task.ChangeAvailabilityTask;
import de.rwth.idsg.steve.ocpp.task.ChangeConfigurationTask;
import de.rwth.idsg.steve.ocpp.task.ClearCacheTask;
import de.rwth.idsg.steve.ocpp.task.ClearChargingProfileTask;
import de.rwth.idsg.steve.ocpp.task.DataTransferTask;
import de.rwth.idsg.steve.ocpp.task.GetCompositeScheduleTask;
import de.rwth.idsg.steve.ocpp.task.GetConfigurationTask;
import de.rwth.idsg.steve.ocpp.task.GetDiagnosticsTask;
import de.rwth.idsg.steve.ocpp.task.GetLocalListVersionTask;
import de.rwth.idsg.steve.ocpp.task.RemoteStartTransactionTask;
import de.rwth.idsg.steve.ocpp.task.RemoteStopTransactionTask;
import de.rwth.idsg.steve.ocpp.task.ReserveNowTask;
import de.rwth.idsg.steve.ocpp.task.ResetTask;
import de.rwth.idsg.steve.ocpp.task.SendLocalListTask;
import de.rwth.idsg.steve.ocpp.task.SetChargingProfileTask;
import de.rwth.idsg.steve.ocpp.task.TriggerMessageTask;
import de.rwth.idsg.steve.ocpp.task.UnlockConnectorTask;
import de.rwth.idsg.steve.ocpp.task.UpdateFirmwareTask;
import de.rwth.idsg.steve.repository.ChargingProfileRepository;
import de.rwth.idsg.steve.repository.ReservationRepository;
import de.rwth.idsg.steve.repository.TaskStore;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.repository.dto.ChargingProfile;
import de.rwth.idsg.steve.repository.dto.InsertReservationParams;
import de.rwth.idsg.steve.service.dto.EnhancedReserveNowParams;
import de.rwth.idsg.steve.service.dto.EnhancedSetChargingProfileParams;
import de.rwth.idsg.steve.web.dto.ocpp.CancelReservationParams;
import de.rwth.idsg.steve.web.dto.ocpp.ChangeAvailabilityParams;
import de.rwth.idsg.steve.web.dto.ocpp.ChangeConfigurationParams;
import de.rwth.idsg.steve.web.dto.ocpp.ClearChargingProfileParams;
import de.rwth.idsg.steve.web.dto.ocpp.DataTransferParams;
import de.rwth.idsg.steve.web.dto.ocpp.GetCompositeScheduleParams;
import de.rwth.idsg.steve.web.dto.ocpp.GetConfigurationParams;
import de.rwth.idsg.steve.web.dto.ocpp.GetDiagnosticsParams;
import de.rwth.idsg.steve.web.dto.ocpp.MultipleChargePointSelect;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStartTransactionParams;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStopTransactionParams;
import de.rwth.idsg.steve.web.dto.ocpp.ReserveNowParams;
import de.rwth.idsg.steve.web.dto.ocpp.ResetParams;
import de.rwth.idsg.steve.web.dto.ocpp.SendLocalListParams;
import de.rwth.idsg.steve.web.dto.ocpp.SetChargingProfileParams;
import de.rwth.idsg.steve.web.dto.ocpp.TriggerMessageParams;
import de.rwth.idsg.steve.web.dto.ocpp.UnlockConnectorParams;
import de.rwth.idsg.steve.web.dto.ocpp.UpdateFirmwareParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ocpp.cp._2015._10.ChargingProfilePurposeType;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 05.01.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChargePointServiceClient {

    private final ChargingProfileRepository chargingProfileRepository;
    private final ReservationRepository reservationRepository;
    private final OcppTagService ocppTagService;

    private final ScheduledExecutorService executorService;
    private final TaskStore taskStore;
    private final ChargePointServiceInvokerImpl invoker;

    // -------------------------------------------------------------------------
    // Multiple Execution - since OCPP 1.2
    // -------------------------------------------------------------------------

    public int changeAvailability(OcppVersion ocppVersion, ChangeAvailabilityParams params) {
        ChangeAvailabilityTask task = new ChangeAvailabilityTask(ocppVersion, params);

        BackgroundService.with(executorService)
            .forEach(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.changeAvailability(c, task));

        return taskStore.add(task);
    }

    public int changeConfiguration(OcppVersion ocppVersion, ChangeConfigurationParams params) {
        ChangeConfigurationTask task = new ChangeConfigurationTask(ocppVersion, params);

        BackgroundService.with(executorService)
            .forEach(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.changeConfiguration(c, task));

        return taskStore.add(task);
    }

    public int clearCache(OcppVersion ocppVersion, MultipleChargePointSelect params) {
        ClearCacheTask task = new ClearCacheTask(ocppVersion, params);

        BackgroundService.with(executorService)
            .forEach(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.clearCache(c, task));

        return taskStore.add(task);
    }

    public int getDiagnostics(OcppVersion ocppVersion, GetDiagnosticsParams params) {
        GetDiagnosticsTask task = new GetDiagnosticsTask(ocppVersion, params);

        BackgroundService.with(executorService)
            .forEach(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.getDiagnostics(c, task));

        return taskStore.add(task);
    }

    public int reset(OcppVersion ocppVersion, ResetParams params) {
        ResetTask task = new ResetTask(ocppVersion, params);

        BackgroundService.with(executorService)
            .forEach(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.reset(c, task));

        return taskStore.add(task);
    }

    public int updateFirmware(OcppVersion ocppVersion, UpdateFirmwareParams params) {
        UpdateFirmwareTask task = new UpdateFirmwareTask(ocppVersion, params);

        BackgroundService.with(executorService)
            .forEach(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.updateFirmware(c, task));

        return taskStore.add(task);
    }

    // -------------------------------------------------------------------------
    // Single Execution - since OCPP 1.2
    // -------------------------------------------------------------------------

    public int remoteStartTransaction(OcppVersion ocppVersion, RemoteStartTransactionParams params) {
        RemoteStartTransactionTask task = new RemoteStartTransactionTask(ocppVersion, params);

        BackgroundService.with(executorService)
            .forFirst(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.remoteStartTransaction(c, task));

        return taskStore.add(task);
    }

    public int remoteStopTransaction(OcppVersion ocppVersion, RemoteStopTransactionParams params) {
        RemoteStopTransactionTask task = new RemoteStopTransactionTask(ocppVersion, params);

        BackgroundService.with(executorService)
            .forFirst(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.remoteStopTransaction(c, task));

        return taskStore.add(task);
    }

    public int unlockConnector(OcppVersion ocppVersion, UnlockConnectorParams params) {
        UnlockConnectorTask task = new UnlockConnectorTask(ocppVersion, params);

        BackgroundService.with(executorService)
            .forFirst(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.unlockConnector(c, task));

        return taskStore.add(task);
    }

    // -------------------------------------------------------------------------
    // Multiple Execution - since OCPP 1.5
    // -------------------------------------------------------------------------

    public int dataTransfer(OcppVersion ocppVersion, DataTransferParams params) {
        DataTransferTask task = new DataTransferTask(ocppVersion, params);

        BackgroundService.with(executorService)
            .forEach(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.dataTransfer(c, task));

        return taskStore.add(task);
    }

    public int getConfiguration(OcppVersion ocppVersion, GetConfigurationParams params) {
        GetConfigurationTask task = new GetConfigurationTask(ocppVersion, params);

        BackgroundService.with(executorService)
            .forEach(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.getConfiguration(c, task));

        return taskStore.add(task);
    }

    public int getLocalListVersion(OcppVersion ocppVersion, MultipleChargePointSelect params) {
        GetLocalListVersionTask task = new GetLocalListVersionTask(ocppVersion, params);

        BackgroundService.with(executorService)
            .forEach(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.getLocalListVersion(c, task));

        return taskStore.add(task);
    }

    public int sendLocalList(OcppVersion ocppVersion, SendLocalListParams params) {
        SendLocalListTask task = new SendLocalListTask(ocppVersion, params, ocppTagService);

        BackgroundService.with(executorService)
            .forEach(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.sendLocalList(c, task));

        return taskStore.add(task);
    }

    // -------------------------------------------------------------------------
    // Single Execution - since OCPP 1.5
    // -------------------------------------------------------------------------

    public int reserveNow(OcppVersion ocppVersion, ReserveNowParams params) {
        List<ChargePointSelect> list = params.getChargePointSelectList();

        InsertReservationParams res = InsertReservationParams.builder()
            .idTag(params.getIdTag())
            .chargeBoxId(list.get(0).getChargeBoxId())
            .connectorId(params.getConnectorId())
            .startTimestamp(DateTime.now())
            .expiryTimestamp(params.getExpiry().toDateTime())
            .build();

        int reservationId = reservationRepository.insert(res);
        String parentIdTag = ocppTagService.getParentIdtag(params.getIdTag());

        EnhancedReserveNowParams enhancedParams = new EnhancedReserveNowParams(params, reservationId, parentIdTag);
        ReserveNowTask task = new ReserveNowTask(ocppVersion, enhancedParams, reservationRepository);

        BackgroundService.with(executorService)
            .forFirst(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.reserveNow(c, task));

        return taskStore.add(task);
    }

    public int cancelReservation(OcppVersion ocppVersion, CancelReservationParams params) {
        CancelReservationTask task = new CancelReservationTask(ocppVersion, params, reservationRepository);

        BackgroundService.with(executorService)
            .forFirst(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.cancelReservation(c, task));

        return taskStore.add(task);
    }

    // -------------------------------------------------------------------------
    // Multiple Execution - since OCPP 1.6
    // -------------------------------------------------------------------------

    public int triggerMessage(OcppVersion ocppVersion, TriggerMessageParams params) {
        TriggerMessageTask task = new TriggerMessageTask(ocppVersion, params);

        BackgroundService.with(executorService)
            .forEach(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.triggerMessage(c, task));

        return taskStore.add(task);
    }

    public int setChargingProfile(OcppVersion ocppVersion, SetChargingProfileParams params) {
        ChargingProfile.Details details = chargingProfileRepository.getDetails(params.getChargingProfilePk());

        checkAdditionalConstraints(params, details);

        EnhancedSetChargingProfileParams enhancedParams = new EnhancedSetChargingProfileParams(params, details);
        SetChargingProfileTask task = new SetChargingProfileTask(ocppVersion, enhancedParams, chargingProfileRepository);

        BackgroundService.with(executorService)
            .forEach(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.setChargingProfile(c, task));

        return taskStore.add(task);
    }

    public int clearChargingProfile(OcppVersion ocppVersion, ClearChargingProfileParams params) {
        ClearChargingProfileTask task = new ClearChargingProfileTask(ocppVersion, params, chargingProfileRepository);

        BackgroundService.with(executorService)
            .forEach(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.clearChargingProfile(c, task));

        return taskStore.add(task);
    }

    public int getCompositeSchedule(OcppVersion ocppVersion, GetCompositeScheduleParams params) {
        GetCompositeScheduleTask task = new GetCompositeScheduleTask(ocppVersion, params);

        BackgroundService.with(executorService)
            .forEach(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.getCompositeSchedule(c, task));

        return taskStore.add(task);
    }

    /**
     * Do some additional checks defined by OCPP spec, which cannot be captured with javax.validation
     */
    private static void checkAdditionalConstraints(SetChargingProfileParams params, ChargingProfile.Details details) {
        ChargingProfilePurposeType purpose = ChargingProfilePurposeType.fromValue(details.getProfile().getChargingProfilePurpose());

        if (ChargingProfilePurposeType.CHARGE_POINT_MAX_PROFILE == purpose
            && params.getConnectorId() != null
            && params.getConnectorId() != 0) {
            throw new SteveException("ChargePointMaxProfile can only be set at Charge Point ConnectorId 0");
        }

        if (ChargingProfilePurposeType.TX_PROFILE == purpose
            && params.getConnectorId() != null
            && params.getConnectorId() < 1) {
            throw new SteveException("TxProfile should only be set at Charge Point ConnectorId > 0");
        }
    }
}
