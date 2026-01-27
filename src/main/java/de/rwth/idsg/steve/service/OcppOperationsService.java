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

import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.task.GetConfigurationTask;
import de.rwth.idsg.steve.ocpp.task.SetChargingProfileTaskAdhoc;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.web.dto.RestCallback;
import de.rwth.idsg.steve.web.dto.ocpp.CancelReservationParams;
import de.rwth.idsg.steve.web.dto.ocpp.ChangeAvailabilityParams;
import de.rwth.idsg.steve.web.dto.ocpp.ChangeConfigurationParams;
import de.rwth.idsg.steve.web.dto.ocpp.ChargePointSelection;
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
import ocpp.cp._2015._10.GetCompositeScheduleResponse;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Slf4j
@Service
@RequiredArgsConstructor
public class OcppOperationsService {

    private static final Duration STATION_RESPONSE_TIMEOUT = Duration.ofSeconds(30);

    private final ChargePointService chargePointService;
    private final ChargePointServiceClient chargePointServiceClient;

    /**
     * TODO: Improve this method. We make multiple calls to the database to get charge points for each version.
     */
    private List<ChargePointSelect> fetchChargePoints(List<String> chargeBoxIdList) {
        List<ChargePointSelect> returnList = new ArrayList<>();

        for (OcppVersion version : OcppVersion.values()) {
            var temp = chargePointService.getChargePointsWithIds(version, chargeBoxIdList);
            returnList.addAll(temp);
        }

        return returnList;
    }

    private <T> RestCallback<T> createCallback(ChargePointSelection chargePointSelection) {
        return new RestCallback<>(STATION_RESPONSE_TIMEOUT, new CountDownLatch(chargePointSelection.getChargePointSelectList().size()));
    }

    // -------------------------------------------------------------------------
    // Since Ocpp 1.2
    // -------------------------------------------------------------------------

    public RestCallback<String> changeAvailability(ChangeAvailabilityParams params) throws Exception {
        params.setChargePointSelectList(fetchChargePoints(params.getChargeBoxIdList()));

        RestCallback<String> callback = createCallback(params);
        int taskId = chargePointServiceClient.changeAvailability(params, callback);

        callback.setTaskId(taskId);
        callback.waitForResponses();
        return callback;
    }

    public RestCallback<String> changeConfiguration(ChangeConfigurationParams params) throws Exception {
        params.setChargePointSelectList(fetchChargePoints(params.getChargeBoxIdList()));

        RestCallback<String> callback = createCallback(params);
        int taskId = chargePointServiceClient.changeConfiguration(params, callback);

        callback.setTaskId(taskId);
        callback.waitForResponses();
        return callback;
    }

    public RestCallback<String> clearCache(MultipleChargePointSelect params) throws Exception {
        params.setChargePointSelectList(fetchChargePoints(params.getChargeBoxIdList()));

        RestCallback<String> callback = createCallback(params);
        int taskId = chargePointServiceClient.clearCache(params, callback);

        callback.setTaskId(taskId);
        callback.waitForResponses();
        return callback;
    }

    public RestCallback<String> getDiagnostics(GetDiagnosticsParams params) throws Exception {
        params.setChargePointSelectList(fetchChargePoints(params.getChargeBoxIdList()));

        RestCallback<String> callback = createCallback(params);
        int taskId = chargePointServiceClient.getDiagnostics(params, callback);

        callback.setTaskId(taskId);
        callback.waitForResponses();
        return callback;
    }

    public RestCallback<String> remoteStartTransaction(RemoteStartTransactionParams params) throws Exception {
        params.setChargePointSelectList(fetchChargePoints(params.getChargeBoxIdList()));

        RestCallback<String> callback = createCallback(params);
        int taskId = chargePointServiceClient.remoteStartTransaction(params, callback);

        callback.setTaskId(taskId);
        callback.waitForResponses();
        return callback;
    }

    public RestCallback<String> remoteStopTransaction(RemoteStopTransactionParams params) throws Exception {
        params.setChargePointSelectList(fetchChargePoints(params.getChargeBoxIdList()));

        RestCallback<String> callback = createCallback(params);
        int taskId = chargePointServiceClient.remoteStopTransaction(params, callback);

        callback.setTaskId(taskId);
        callback.waitForResponses();
        return callback;
    }

    public RestCallback<String> reset(ResetParams params) throws Exception {
        params.setChargePointSelectList(fetchChargePoints(params.getChargeBoxIdList()));


        RestCallback<String> callback = createCallback(params);
        int taskId = chargePointServiceClient.reset(params, callback);

        callback.setTaskId(taskId);
        callback.waitForResponses();
        return callback;
    }

    public RestCallback<String> unlockConnector(UnlockConnectorParams params) throws Exception {
        params.setChargePointSelectList(fetchChargePoints(params.getChargeBoxIdList()));

        RestCallback<String> callback = createCallback(params);
        int taskId = chargePointServiceClient.unlockConnector(params, callback);

        callback.setTaskId(taskId);
        callback.waitForResponses();
        return callback;
    }

    public RestCallback<String> updateFirmware(UpdateFirmwareParams params) throws Exception {
        params.setChargePointSelectList(fetchChargePoints(params.getChargeBoxIdList()));

        RestCallback<String> callback = createCallback(params);
        int taskId = chargePointServiceClient.updateFirmware(params, callback);

        callback.setTaskId(taskId);
        callback.waitForResponses();
        return callback;
    }

    // -------------------------------------------------------------------------
    // Since Ocpp 1.5
    // -------------------------------------------------------------------------

    public RestCallback<String> reserveNow(ReserveNowParams params) throws Exception {
        params.setChargePointSelectList(fetchChargePoints(params.getChargeBoxIdList()));

        RestCallback<String> callback = createCallback(params);
        int taskId = chargePointServiceClient.reserveNow(params, callback);

        callback.setTaskId(taskId);
        callback.waitForResponses();
        return callback;
    }

    public RestCallback<String> cancelReservation(CancelReservationParams params) throws Exception {
        params.setChargePointSelectList(fetchChargePoints(params.getChargeBoxIdList()));

        RestCallback<String> callback = createCallback(params);
        int taskId = chargePointServiceClient.cancelReservation(params, callback);

        callback.setTaskId(taskId);
        callback.waitForResponses();
        return callback;
    }

    public RestCallback<ocpp.cp._2015._10.DataTransferResponse> dataTransfer(DataTransferParams params) throws Exception {
        params.setChargePointSelectList(fetchChargePoints(params.getChargeBoxIdList()));

        RestCallback<ocpp.cp._2015._10.DataTransferResponse> callback = createCallback(params);
        int taskId = chargePointServiceClient.dataTransfer(params, callback);

        callback.setTaskId(taskId);
        callback.waitForResponses();
        return callback;
    }

    public RestCallback<GetConfigurationTask.ConfigurationKeyValues> getConfiguration(GetConfigurationParams params) throws Exception {
        params.setChargePointSelectList(fetchChargePoints(params.getChargeBoxIdList()));

        RestCallback<GetConfigurationTask.ConfigurationKeyValues> callback = createCallback(params);
        int taskId = chargePointServiceClient.getConfiguration(params, callback);

        callback.setTaskId(taskId);
        callback.waitForResponses();
        return callback;
    }

    public RestCallback<String> getLocalListVersion(MultipleChargePointSelect params) throws Exception {
        params.setChargePointSelectList(fetchChargePoints(params.getChargeBoxIdList()));

        RestCallback<String> callback = createCallback(params);
        int taskId = chargePointServiceClient.getLocalListVersion(params, callback);

        callback.setTaskId(taskId);
        callback.waitForResponses();
        return callback;
    }

    public RestCallback<String> sendLocalList(SendLocalListParams params) throws Exception {
        params.setChargePointSelectList(fetchChargePoints(params.getChargeBoxIdList()));

        RestCallback<String> callback = createCallback(params);
        int taskId = chargePointServiceClient.sendLocalList(params, callback);

        callback.setTaskId(taskId);
        callback.waitForResponses();
        return callback;
    }

    // -------------------------------------------------------------------------
    // Since Ocpp 1.6
    // -------------------------------------------------------------------------

    public RestCallback<String> triggerMessage(TriggerMessageParams params) throws Exception {
        params.setChargePointSelectList(fetchChargePoints(params.getChargeBoxIdList()));

        RestCallback<String> callback = createCallback(params);
        int taskId = chargePointServiceClient.triggerMessage(params, callback);

        callback.setTaskId(taskId);
        callback.waitForResponses();
        return callback;
    }

    public RestCallback<GetCompositeScheduleResponse> getCompositeSchedule(GetCompositeScheduleParams params) throws Exception {
        params.setChargePointSelectList(fetchChargePoints(params.getChargeBoxIdList()));

        RestCallback<GetCompositeScheduleResponse> callback = createCallback(params);
        int taskId = chargePointServiceClient.getCompositeSchedule(params, callback);

        callback.setTaskId(taskId);
        callback.waitForResponses();
        return callback;
    }

    public RestCallback<String> clearChargingProfile(ClearChargingProfileParams params) throws Exception {
        params.setChargePointSelectList(fetchChargePoints(params.getChargeBoxIdList()));

        RestCallback<String> callback = createCallback(params);
        int taskId = chargePointServiceClient.clearChargingProfile(params, callback);

        callback.setTaskId(taskId);
        callback.waitForResponses();
        return callback;
    }

    public RestCallback<String> setChargingProfile(SetChargingProfileParams params) throws Exception {
        params.setChargePointSelectList(fetchChargePoints(params.getChargeBoxIdList()));

        RestCallback<String> callback = createCallback(params);
        int taskId = chargePointServiceClient.setChargingProfile(params, callback);

        callback.setTaskId(taskId);
        callback.waitForResponses();
        return callback;
    }

    public RestCallback<String> setChargingProfile(SetChargingProfileParams params,
                                                   SetChargingProfileTaskAdhoc task) throws Exception {
        params.setChargePointSelectList(fetchChargePoints(params.getChargeBoxIdList()));

        RestCallback<String> callback = createCallback(params);
        int taskId = chargePointServiceClient.setChargingProfile(task, callback);

        callback.setTaskId(taskId);
        callback.waitForResponses();
        return callback;
    }

    // -------------------------------------------------------------------------
    // "Improved security for OCPP 1.6-J" additions
    // -------------------------------------------------------------------------

    public RestCallback<String> extendedTriggerMessage(ExtendedTriggerMessageParams params) throws Exception {
        params.setChargePointSelectList(fetchChargePoints(params.getChargeBoxIdList()));

        RestCallback<String> callback = createCallback(params);
        int taskId = chargePointServiceClient.extendedTriggerMessage(params);

        callback.setTaskId(taskId);
        callback.waitForResponses();
        return callback;
    }

    public RestCallback<String> getLog(GetLogParams params) throws Exception {
        params.setChargePointSelectList(fetchChargePoints(params.getChargeBoxIdList()));

        RestCallback<String> callback = createCallback(params);
        int taskId = chargePointServiceClient.getLog(params);

        callback.setTaskId(taskId);
        callback.waitForResponses();
        return callback;
    }

    public RestCallback<String> signedUpdateFirmware(SignedUpdateFirmwareParams params) throws Exception {
        params.setChargePointSelectList(fetchChargePoints(params.getChargeBoxIdList()));

        RestCallback<String> callback = createCallback(params);
        int taskId = chargePointServiceClient.signedUpdateFirmware(params);

        callback.setTaskId(taskId);
        callback.waitForResponses();
        return callback;
    }

    public RestCallback<String> installCertificate(InstallCertificateParams params) throws Exception {
        params.setChargePointSelectList(fetchChargePoints(params.getChargeBoxIdList()));

        RestCallback<String> callback = createCallback(params);
        int taskId = chargePointServiceClient.installCertificate(params);

        callback.setTaskId(taskId);
        callback.waitForResponses();
        return callback;
    }

    public RestCallback<String> deleteCertificate(DeleteCertificateParams params) throws Exception {
        params.setChargePointSelectList(fetchChargePoints(params.getChargeBoxIdList()));

        RestCallback<String> callback = createCallback(params);
        int taskId = chargePointServiceClient.deleteCertificate(params);

        callback.setTaskId(taskId);
        callback.waitForResponses();
        return callback;
    }

    public RestCallback<String> getInstalledCertificateIds(GetInstalledCertificateIdsParams params) throws Exception {
        params.setChargePointSelectList(fetchChargePoints(params.getChargeBoxIdList()));

        RestCallback<String> callback = createCallback(params);
        int taskId = chargePointServiceClient.getInstalledCertificateIds(params);

        callback.setTaskId(taskId);
        callback.waitForResponses();
        return callback;
    }
}
