/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
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
import de.rwth.idsg.steve.ocpp.ChargePointServiceInvokerImpl;
import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.ocpp.task.CancelReservationTask;
import de.rwth.idsg.steve.ocpp.task.CertificateSignedTask;
import de.rwth.idsg.steve.ocpp.task.ChangeAvailabilityTask;
import de.rwth.idsg.steve.ocpp.task.ChangeConfigurationTask;
import de.rwth.idsg.steve.ocpp.task.ClearCacheTask;
import de.rwth.idsg.steve.ocpp.task.ClearChargingProfileTask;
import de.rwth.idsg.steve.ocpp.task.DataTransferTask;
import de.rwth.idsg.steve.ocpp.task.DeleteCertificateTask;
import de.rwth.idsg.steve.ocpp.task.ExtendedTriggerMessageTask;
import de.rwth.idsg.steve.ocpp.task.GetCompositeScheduleTask;
import de.rwth.idsg.steve.ocpp.task.GetConfigurationTask;
import de.rwth.idsg.steve.ocpp.task.GetDiagnosticsTask;
import de.rwth.idsg.steve.ocpp.task.GetInstalledCertificateIdsTask;
import de.rwth.idsg.steve.ocpp.task.GetLocalListVersionTask;
import de.rwth.idsg.steve.ocpp.task.GetLogTask;
import de.rwth.idsg.steve.ocpp.task.InstallCertificateTask;
import de.rwth.idsg.steve.ocpp.task.RemoteStartTransactionTask;
import de.rwth.idsg.steve.ocpp.task.RemoteStopTransactionTask;
import de.rwth.idsg.steve.ocpp.task.ReserveNowTask;
import de.rwth.idsg.steve.ocpp.task.ResetTask;
import de.rwth.idsg.steve.ocpp.task.SendLocalListTask;
import de.rwth.idsg.steve.ocpp.task.SetChargingProfileTask;
import de.rwth.idsg.steve.ocpp.task.SetChargingProfileTaskFromDB;
import de.rwth.idsg.steve.ocpp.task.SignedUpdateFirmwareTask;
import de.rwth.idsg.steve.ocpp.task.TriggerMessageTask;
import de.rwth.idsg.steve.ocpp.task.UnlockConnectorTask;
import de.rwth.idsg.steve.ocpp.task.UpdateFirmwareTask;
import de.rwth.idsg.steve.repository.CertificateRepository;
import de.rwth.idsg.steve.repository.ChargingProfileRepository;
import de.rwth.idsg.steve.repository.EventRepository;
import de.rwth.idsg.steve.repository.ReservationRepository;
import de.rwth.idsg.steve.repository.TaskStore;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.repository.dto.ChargingProfile;
import de.rwth.idsg.steve.repository.dto.InsertReservationParams;
import de.rwth.idsg.steve.utils.mapper.ChargingProfileDetailsMapper;
import de.rwth.idsg.steve.web.dto.ocpp.CancelReservationParams;
import de.rwth.idsg.steve.web.dto.ocpp.CertificateSignedParams;
import de.rwth.idsg.steve.web.dto.ocpp.ChangeAvailabilityParams;
import de.rwth.idsg.steve.web.dto.ocpp.ChangeConfigurationParams;
import de.rwth.idsg.steve.web.dto.ocpp.ClearChargingProfileParams;
import de.rwth.idsg.steve.web.dto.ocpp.DataTransferParams;
import de.rwth.idsg.steve.web.dto.ocpp.DeleteCertificateParams;
import de.rwth.idsg.steve.web.dto.ocpp.ExtendedTriggerMessageParams;
import de.rwth.idsg.steve.web.dto.ocpp.GetCompositeScheduleParams;
import de.rwth.idsg.steve.web.dto.ocpp.GetConfigurationParams;
import de.rwth.idsg.steve.web.dto.ocpp.GetDiagnosticsParams;
import de.rwth.idsg.steve.web.dto.ocpp.GetInstalledCertificateIdsParams;
import de.rwth.idsg.steve.web.dto.ocpp.GetLogParams;
import de.rwth.idsg.steve.web.dto.ocpp.InstallCertificateParams;
import de.rwth.idsg.steve.web.dto.ocpp.MultipleChargePointSelect;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStartTransactionParams;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStopTransactionParams;
import de.rwth.idsg.steve.web.dto.ocpp.ReserveNowParams;
import de.rwth.idsg.steve.web.dto.ocpp.ResetParams;
import de.rwth.idsg.steve.web.dto.ocpp.SendLocalListParams;
import de.rwth.idsg.steve.web.dto.ocpp.SetChargingProfileParams;
import de.rwth.idsg.steve.web.dto.ocpp.SignedUpdateFirmwareParams;
import de.rwth.idsg.steve.web.dto.ocpp.TriggerMessageParams;
import de.rwth.idsg.steve.web.dto.ocpp.UnlockConnectorParams;
import de.rwth.idsg.steve.web.dto.ocpp.UpdateFirmwareParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ocpp._2022._02.security.CertificateSignedResponse.CertificateSignedStatusEnumType;
import ocpp._2022._02.security.DeleteCertificateResponse.DeleteCertificateStatusEnumType;
import ocpp._2022._02.security.GetInstalledCertificateIdsResponse;
import ocpp._2022._02.security.GetLogResponse;
import ocpp._2022._02.security.InstallCertificateResponse.InstallCertificateStatusEnumType;
import ocpp._2022._02.security.SignedUpdateFirmwareResponse.UpdateFirmwareStatusEnumType;
import ocpp.cp._2015._10.AvailabilityStatus;
import ocpp.cp._2015._10.CancelReservationStatus;
import ocpp.cp._2015._10.ChargingProfilePurposeType;
import ocpp.cp._2015._10.ChargingProfileStatus;
import ocpp.cp._2015._10.ClearCacheStatus;
import ocpp.cp._2015._10.ClearChargingProfileStatus;
import ocpp.cp._2015._10.ConfigurationStatus;
import ocpp.cp._2015._10.GetCompositeScheduleResponse;
import ocpp.cp._2015._10.GetDiagnosticsResponse;
import ocpp.cp._2015._10.RemoteStartStopStatus;
import ocpp.cp._2015._10.ReservationStatus;
import ocpp.cp._2015._10.ResetStatus;
import ocpp.cp._2015._10.TriggerMessageStatus;
import ocpp.cp._2015._10.UnlockStatus;
import ocpp.cp._2015._10.UpdateStatus;
import org.joda.time.DateTime;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;

import static de.rwth.idsg.steve.ocpp.task.UpdateFirmwareTask.UpdateFirmwareResponseStatus;
import static ocpp._2022._02.security.ExtendedTriggerMessageResponse.TriggerMessageStatusEnumType;

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
    private final ChargePointService chargePointService;
    private final CertificateRepository certificateRepository;
    private final EventRepository eventRepository;

    private final TaskExecutor taskExecutor;
    private final TaskStore taskStore;
    private final ChargePointServiceInvokerImpl invoker;

    // -------------------------------------------------------------------------
    // Multiple Execution - since OCPP 1.2
    // -------------------------------------------------------------------------

    @SafeVarargs
    public final int changeAvailability(ChangeAvailabilityParams params,
                                        OcppCallback<AvailabilityStatus>... callbacks) {
        ChangeAvailabilityTask task = new ChangeAvailabilityTask(params);

        for (var callback : callbacks) {
            task.addCallback(callback);
        }

        BackgroundService.with(taskExecutor)
            .forEach(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.changeAvailability(c, task));

        return taskStore.add(task);
    }

    @SafeVarargs
    public final int changeConfiguration(ChangeConfigurationParams params,
                                         OcppCallback<ConfigurationStatus>... callbacks) {
        ChangeConfigurationTask task = new ChangeConfigurationTask(params, chargePointService);

        for (var callback : callbacks) {
            task.addCallback(callback);
        }

        BackgroundService.with(taskExecutor)
            .forEach(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.changeConfiguration(c, task));

        return taskStore.add(task);
    }

    @SafeVarargs
    public final int clearCache(MultipleChargePointSelect params,
                                OcppCallback<ClearCacheStatus>... callbacks) {
        ClearCacheTask task = new ClearCacheTask(params);

        for (var callback : callbacks) {
            task.addCallback(callback);
        }

        BackgroundService.with(taskExecutor)
            .forEach(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.clearCache(c, task));

        return taskStore.add(task);
    }

    @SafeVarargs
    public final int getDiagnostics(GetDiagnosticsParams params,
                                    OcppCallback<GetDiagnosticsResponse>... callbacks) {
        GetDiagnosticsTask task = new GetDiagnosticsTask(params);

        for (var callback : callbacks) {
            task.addCallback(callback);
        }

        BackgroundService.with(taskExecutor)
            .forEach(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.getDiagnostics(c, task));

        return taskStore.add(task);
    }

    @SafeVarargs
    public final int reset(ResetParams params,
                           OcppCallback<ResetStatus>... callbacks) {
        ResetTask task = new ResetTask(params);

        for (var callback : callbacks) {
            task.addCallback(callback);
        }

        BackgroundService.with(taskExecutor)
            .forEach(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.reset(c, task));

        return taskStore.add(task);
    }

    @SafeVarargs
    public final int updateFirmware(UpdateFirmwareParams params,
                                    OcppCallback<UpdateFirmwareResponseStatus>... callbacks) {
        UpdateFirmwareTask task = new UpdateFirmwareTask(params);

        for (var callback : callbacks) {
            task.addCallback(callback);
        }

        BackgroundService.with(taskExecutor)
            .forEach(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.updateFirmware(c, task));

        return taskStore.add(task);
    }

    // -------------------------------------------------------------------------
    // Single Execution - since OCPP 1.2
    // -------------------------------------------------------------------------

    @SafeVarargs
    public final int remoteStartTransaction(RemoteStartTransactionParams params,
                                            OcppCallback<RemoteStartStopStatus>... callbacks) {
        ocpp.cp._2015._10.ChargingProfile chargingProfile = null;
        Integer chargingProfilePk = params.getChargingProfilePk();
        if (chargingProfilePk != null) {
            ChargingProfile.Details details = chargingProfileRepository.getDetails(chargingProfilePk);
            chargingProfile = ChargingProfileDetailsMapper.mapToOcpp(details, null);
            if (chargingProfile.getChargingProfilePurpose() != ChargingProfilePurposeType.TX_PROFILE) {
                throw new SteveException("ChargingProfilePurposeType is not TX_PROFILE");
            }
        }

        RemoteStartTransactionTask task = new RemoteStartTransactionTask(params, chargingProfile);

        for (var callback : callbacks) {
            task.addCallback(callback);
        }

        BackgroundService.with(taskExecutor)
            .forFirst(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.remoteStartTransaction(c, task));

        return taskStore.add(task);
    }

    @SafeVarargs
    public final int remoteStopTransaction(RemoteStopTransactionParams params,
                                           OcppCallback<RemoteStartStopStatus>... callbacks) {
        RemoteStopTransactionTask task = new RemoteStopTransactionTask(params);

        for (var callback : callbacks) {
            task.addCallback(callback);
        }

        BackgroundService.with(taskExecutor)
            .forFirst(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.remoteStopTransaction(c, task));

        return taskStore.add(task);
    }

    @SafeVarargs
    public final int unlockConnector(UnlockConnectorParams params,
                                     OcppCallback<UnlockStatus>... callbacks) {
        UnlockConnectorTask task = new UnlockConnectorTask(params);

        for (var callback : callbacks) {
            task.addCallback(callback);
        }

        BackgroundService.with(taskExecutor)
            .forFirst(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.unlockConnector(c, task));

        return taskStore.add(task);
    }

    // -------------------------------------------------------------------------
    // Multiple Execution - since OCPP 1.5
    // -------------------------------------------------------------------------

    @SafeVarargs
    public final int dataTransfer(DataTransferParams params,
                                  OcppCallback<ocpp.cp._2015._10.DataTransferResponse>... callbacks) {
        DataTransferTask task = new DataTransferTask(params);

        for (var callback : callbacks) {
            task.addCallback(callback);
        }

        BackgroundService.with(taskExecutor)
            .forEach(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.dataTransfer(c, task));

        return taskStore.add(task);
    }

    @SafeVarargs
    public final int getConfiguration(GetConfigurationParams params,
                                      OcppCallback<GetConfigurationTask.ConfigurationKeyValues>... callbacks) {
        GetConfigurationTask task = new GetConfigurationTask(params, chargePointService);

        for (var callback : callbacks) {
            task.addCallback(callback);
        }

        BackgroundService.with(taskExecutor)
            .forEach(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.getConfiguration(c, task));

        return taskStore.add(task);
    }

    @SafeVarargs
    public final int getLocalListVersion(MultipleChargePointSelect params,
                                         OcppCallback<Integer>... callbacks) {
        GetLocalListVersionTask task = new GetLocalListVersionTask(params);

        for (var callback : callbacks) {
            task.addCallback(callback);
        }

        BackgroundService.with(taskExecutor)
            .forEach(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.getLocalListVersion(c, task));

        return taskStore.add(task);
    }

    @SafeVarargs
    public final int sendLocalList(SendLocalListParams params,
                                   OcppCallback<UpdateStatus>... callbacks) {
        SendLocalListTask task = new SendLocalListTask(params, ocppTagService);

        for (var callback : callbacks) {
            task.addCallback(callback);
        }

        BackgroundService.with(taskExecutor)
            .forEach(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.sendLocalList(c, task));

        return taskStore.add(task);
    }

    // -------------------------------------------------------------------------
    // Single Execution - since OCPP 1.5
    // -------------------------------------------------------------------------

    @SafeVarargs
    public final int reserveNow(ReserveNowParams params,
                                OcppCallback<ReservationStatus>... callbacks) {
        List<ChargePointSelect> list = params.getChargePointSelectList();

        InsertReservationParams res = InsertReservationParams.builder()
            .idTag(params.getIdTag())
            .chargeBoxId(list.get(0).getChargeBoxId())
            .connectorId(params.getConnectorId())
            .startTimestamp(DateTime.now())
            .expiryTimestamp(params.getExpiry())
            .build();

        int reservationId = reservationRepository.insert(res);
        String parentIdTag = ocppTagService.getParentIdtag(params.getIdTag());

        ReserveNowTask task = new ReserveNowTask(params, reservationId, parentIdTag, reservationRepository);

        for (var callback : callbacks) {
            task.addCallback(callback);
        }

        BackgroundService.with(taskExecutor)
            .forFirst(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.reserveNow(c, task));

        return taskStore.add(task);
    }

    @SafeVarargs
    public final int cancelReservation(CancelReservationParams params,
                                       OcppCallback<CancelReservationStatus>... callbacks) {
        CancelReservationTask task = new CancelReservationTask(params, reservationRepository);

        for (var callback : callbacks) {
            task.addCallback(callback);
        }

        BackgroundService.with(taskExecutor)
            .forFirst(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.cancelReservation(c, task));

        return taskStore.add(task);
    }

    // -------------------------------------------------------------------------
    // Multiple Execution - since OCPP 1.6
    // -------------------------------------------------------------------------

    @SafeVarargs
    public final int triggerMessage(TriggerMessageParams params,
                                    OcppCallback<TriggerMessageStatus>... callbacks) {
        TriggerMessageTask task = new TriggerMessageTask(params);

        for (var callback : callbacks) {
            task.addCallback(callback);
        }

        BackgroundService.with(taskExecutor)
            .forEach(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.triggerMessage(c, task));

        return taskStore.add(task);
    }

    @SafeVarargs
    public final int setChargingProfile(SetChargingProfileTask task,
                                        OcppCallback<ChargingProfileStatus>... callbacks) {
        for (var callback : callbacks) {
            task.addCallback(callback);
        }

        BackgroundService.with(taskExecutor)
            .forEach(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.setChargingProfile(c, task));

        return taskStore.add(task);
    }

    @SafeVarargs
    public final int setChargingProfile(SetChargingProfileParams params,
                                        OcppCallback<ChargingProfileStatus>... callbacks) {
        ChargingProfile.Details details = chargingProfileRepository.getDetails(params.getChargingProfilePk());

        SetChargingProfileTaskFromDB task = new SetChargingProfileTaskFromDB(params, details, chargingProfileRepository);

        return setChargingProfile(task, callbacks);
    }

    @SafeVarargs
    public final int clearChargingProfile(ClearChargingProfileParams params,
                                          OcppCallback<ClearChargingProfileStatus>... callbacks) {
        ClearChargingProfileTask task = new ClearChargingProfileTask(params, chargingProfileRepository);

        for (var callback : callbacks) {
            task.addCallback(callback);
        }

        BackgroundService.with(taskExecutor)
            .forEach(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.clearChargingProfile(c, task));

        return taskStore.add(task);
    }

    @SafeVarargs
    public final int getCompositeSchedule(GetCompositeScheduleParams params,
                                          OcppCallback<GetCompositeScheduleResponse>... callbacks) {
        GetCompositeScheduleTask task = new GetCompositeScheduleTask(params);

        for (var callback : callbacks) {
            task.addCallback(callback);
        }

        BackgroundService.with(taskExecutor)
            .forEach(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.getCompositeSchedule(c, task));

        return taskStore.add(task);
    }

    // -------------------------------------------------------------------------
    // "Improved security for OCPP 1.6-J" additions
    // -------------------------------------------------------------------------

    @SafeVarargs
    public final int extendedTriggerMessage(ExtendedTriggerMessageParams params,
                                            OcppCallback<TriggerMessageStatusEnumType>... callbacks) {
        ExtendedTriggerMessageTask task = new ExtendedTriggerMessageTask(params);

        for (var callback : callbacks) {
            task.addCallback(callback);
        }

        BackgroundService.with(taskExecutor)
            .forEach(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.extendedTriggerMessage(c, task));

        return taskStore.add(task);
    }

    @SafeVarargs
    public final int getLog(GetLogParams params,
                            OcppCallback<GetLogResponse>... callbacks) {
        int requestId = eventRepository.insertLogUploadJob(params);

        GetLogTask task = new GetLogTask(params, requestId);

        for (var callback : callbacks) {
            task.addCallback(callback);
        }

        BackgroundService.with(taskExecutor)
            .forEach(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.getLog(c, task));

        return taskStore.add(task);
    }

    @SafeVarargs
    public final int signedUpdateFirmware(SignedUpdateFirmwareParams params,
                                          OcppCallback<UpdateFirmwareStatusEnumType>... callbacks) {
        int requestId = eventRepository.insertFirmwareUpdateJob(params);

        SignedUpdateFirmwareTask task = new SignedUpdateFirmwareTask(params, requestId);

        for (var callback : callbacks) {
            task.addCallback(callback);
        }

        BackgroundService.with(taskExecutor)
            .forEach(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.signedUpdateFirmware(c, task));

        return taskStore.add(task);
    }

    @SafeVarargs
    public final int installCertificate(InstallCertificateParams params,
                                        OcppCallback<InstallCertificateStatusEnumType>... callbacks) {
        InstallCertificateTask task = new InstallCertificateTask(params);

        for (var callback : callbacks) {
            task.addCallback(callback);
        }

        BackgroundService.with(taskExecutor)
            .forEach(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.installCertificate(c, task));

        return taskStore.add(task);
    }

    @SafeVarargs
    public final int deleteCertificate(DeleteCertificateParams params,
                                       OcppCallback<DeleteCertificateStatusEnumType>... callbacks) {
        DeleteCertificateTask task = new DeleteCertificateTask(params, certificateRepository);

        for (var callback : callbacks) {
            task.addCallback(callback);
        }

        BackgroundService.with(taskExecutor)
            .forFirst(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.deleteCertificate(c, task));

        return taskStore.add(task);
    }

    @SafeVarargs
    public final int certificateSigned(CertificateSignedParams params,
                                       OcppCallback<CertificateSignedStatusEnumType>... callbacks) {
        CertificateSignedTask task = new CertificateSignedTask(params, certificateRepository);

        for (var callback : callbacks) {
            task.addCallback(callback);
        }

        BackgroundService.with(taskExecutor)
            .forEach(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.certificateSigned(c, task));

        return taskStore.add(task);
    }

    @SafeVarargs
    public final int getInstalledCertificateIds(GetInstalledCertificateIdsParams params,
                                                OcppCallback<GetInstalledCertificateIdsResponse>... callbacks) {
        GetInstalledCertificateIdsTask task = new GetInstalledCertificateIdsTask(params, certificateRepository);

        for (var callback : callbacks) {
            task.addCallback(callback);
        }

        BackgroundService.with(taskExecutor)
            .forEach(task.getParams().getChargePointSelectList())
            .execute(c -> invoker.getInstalledCertificateIds(c, task));

        return taskStore.add(task);
    }
}
