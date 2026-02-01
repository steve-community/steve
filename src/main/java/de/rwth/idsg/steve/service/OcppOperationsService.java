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
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.task.GetConfigurationTask;
import de.rwth.idsg.steve.ocpp.task.SetChargingProfileTaskAdhoc;
import de.rwth.idsg.steve.repository.ChargingProfileRepository;
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
import ocpp._2022._02.security.DeleteCertificateResponse;
import ocpp._2022._02.security.ExtendedTriggerMessageResponse.TriggerMessageStatusEnumType;
import ocpp._2022._02.security.GetInstalledCertificateIdsResponse;
import ocpp._2022._02.security.GetLogResponse;
import ocpp._2022._02.security.InstallCertificateResponse.InstallCertificateStatusEnumType;
import ocpp._2022._02.security.SignedUpdateFirmwareResponse.UpdateFirmwareStatusEnumType;
import ocpp.cp._2015._10.AvailabilityStatus;
import ocpp.cp._2015._10.CancelReservationStatus;
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
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.BiFunction;

import static de.rwth.idsg.steve.ocpp.task.UpdateFirmwareTask.UpdateFirmwareResponseStatus;

@Slf4j
@Service
@RequiredArgsConstructor
public class OcppOperationsService {

    private static final Duration STATION_RESPONSE_TIMEOUT = Duration.ofSeconds(30);

    private final ChargePointService chargePointService;
    private final ChargePointServiceClient chargePointServiceClient;
    private final OcppTagService ocppTagService;
    private final TransactionService transactionService;
    private final ChargingProfileRepository chargingProfileRepository;

    // -------------------------------------------------------------------------
    // Since Ocpp 1.2
    // -------------------------------------------------------------------------

    public RestCallback<AvailabilityStatus> changeAvailability(ChangeAvailabilityParams params) throws Exception {
        return execute(params, chargePointServiceClient::changeAvailability);
    }

    public RestCallback<ConfigurationStatus> changeConfiguration(ChangeConfigurationParams params) throws Exception {
        return execute(params, chargePointServiceClient::changeConfiguration);
    }

    public RestCallback<ClearCacheStatus> clearCache(MultipleChargePointSelect params) throws Exception {
        return execute(params, chargePointServiceClient::clearCache);
    }

    public RestCallback<GetDiagnosticsResponse> getDiagnostics(GetDiagnosticsParams params) throws Exception {
        return execute(params, chargePointServiceClient::getDiagnostics);
    }

    public RestCallback<RemoteStartStopStatus> remoteStartTransaction(RemoteStartTransactionParams params) throws Exception {
        validateIdTag(params.getIdTag());
        validateChargingProfile(params.getChargingProfilePk());
        return execute(params, chargePointServiceClient::remoteStartTransaction);
    }

    public RestCallback<RemoteStartStopStatus> remoteStopTransaction(RemoteStopTransactionParams params) throws Exception {
        validateTransactionId(params.getTransactionId(), params.getChargeBoxIdList());
        return execute(params, chargePointServiceClient::remoteStopTransaction);
    }

    public RestCallback<ResetStatus> reset(ResetParams params) throws Exception {
        return execute(params, chargePointServiceClient::reset);
    }

    public RestCallback<UnlockStatus> unlockConnector(UnlockConnectorParams params) throws Exception {
        return execute(params, chargePointServiceClient::unlockConnector);
    }

    public RestCallback<UpdateFirmwareResponseStatus> updateFirmware(UpdateFirmwareParams params) throws Exception {
        return execute(params, chargePointServiceClient::updateFirmware);
    }

    // -------------------------------------------------------------------------
    // Since Ocpp 1.5
    // -------------------------------------------------------------------------

    public RestCallback<ReservationStatus> reserveNow(ReserveNowParams params) throws Exception {
        validateIdTag(params.getIdTag());
        return execute(params, chargePointServiceClient::reserveNow);
    }

    public RestCallback<CancelReservationStatus> cancelReservation(CancelReservationParams params) throws Exception {
        return execute(params, chargePointServiceClient::cancelReservation);
    }

    public RestCallback<ocpp.cp._2015._10.DataTransferResponse> dataTransfer(DataTransferParams params) throws Exception {
        return execute(params, chargePointServiceClient::dataTransfer);
    }

    public RestCallback<GetConfigurationTask.ConfigurationKeyValues> getConfiguration(GetConfigurationParams params) throws Exception {
        return execute(params, chargePointServiceClient::getConfiguration);
    }

    public RestCallback<Integer> getLocalListVersion(MultipleChargePointSelect params) throws Exception {
        return execute(params, chargePointServiceClient::getLocalListVersion);
    }

    public RestCallback<UpdateStatus> sendLocalList(SendLocalListParams params) throws Exception {
        validateIdTags(params.getAddUpdateList());
        validateIdTags(params.getDeleteList());
        return execute(params, chargePointServiceClient::sendLocalList);
    }

    // -------------------------------------------------------------------------
    // Since Ocpp 1.6
    // -------------------------------------------------------------------------

    public RestCallback<TriggerMessageStatus> triggerMessage(TriggerMessageParams params) throws Exception {
        return execute(params, chargePointServiceClient::triggerMessage);
    }

    public RestCallback<GetCompositeScheduleResponse> getCompositeSchedule(GetCompositeScheduleParams params) throws Exception {
        return execute(params, chargePointServiceClient::getCompositeSchedule);
    }

    public RestCallback<ClearChargingProfileStatus> clearChargingProfile(ClearChargingProfileParams params) throws Exception {
        validateChargingProfile(params.getChargingProfilePk());
        return execute(params, chargePointServiceClient::clearChargingProfile);
    }

    public RestCallback<ChargingProfileStatus> setChargingProfile(SetChargingProfileParams params) throws Exception {
        validateChargingProfile(params.getChargingProfilePk());
        validateTransactionId(params.getTransactionId(), params.getChargeBoxIdList());
        return execute(params, chargePointServiceClient::setChargingProfile);
    }

    public RestCallback<ChargingProfileStatus> setChargingProfile(SetChargingProfileParams params,
                                                                  SetChargingProfileTaskAdhoc task) throws Exception {
        validateChargingProfile(params.getChargingProfilePk());
        validateTransactionId(params.getTransactionId(), params.getChargeBoxIdList());

        params.setChargePointSelectList(fetchChargePoints(params.getChargeBoxIdList()));

        RestCallback<ChargingProfileStatus> callback = createCallback(params);
        int taskId = chargePointServiceClient.setChargingProfile(task, callback);

        callback.setTaskId(taskId);
        callback.waitForResponses();
        return callback;
    }

    // -------------------------------------------------------------------------
    // "Improved security for OCPP 1.6-J" additions
    // -------------------------------------------------------------------------

    public RestCallback<TriggerMessageStatusEnumType> extendedTriggerMessage(ExtendedTriggerMessageParams params) throws Exception {
        return execute(params, chargePointServiceClient::extendedTriggerMessage);
    }

    public RestCallback<GetLogResponse> getLog(GetLogParams params) throws Exception {
        return execute(params, chargePointServiceClient::getLog);
    }

    public RestCallback<UpdateFirmwareStatusEnumType> signedUpdateFirmware(SignedUpdateFirmwareParams params) throws Exception {
        return execute(params, chargePointServiceClient::signedUpdateFirmware);
    }

    public RestCallback<InstallCertificateStatusEnumType> installCertificate(InstallCertificateParams params) throws Exception {
        return execute(params, chargePointServiceClient::installCertificate);
    }

    public RestCallback<DeleteCertificateResponse.DeleteCertificateStatusEnumType> deleteCertificate(DeleteCertificateParams params) throws Exception {
        return execute(params, chargePointServiceClient::deleteCertificate);
    }

    public RestCallback<GetInstalledCertificateIdsResponse> getInstalledCertificateIds(GetInstalledCertificateIdsParams params) throws Exception {
        return execute(params, chargePointServiceClient::getInstalledCertificateIds);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * TODO: Improve this method. We make multiple calls to the database to get charge points for each version.
     */
    private List<ChargePointSelect> fetchChargePoints(List<String> chargeBoxIdList) {
        List<ChargePointSelect> returnList = new ArrayList<>();

        for (OcppVersion version : OcppVersion.values()) {
            var temp = chargePointService.getChargePointsWithIds(version, chargeBoxIdList);
            returnList.addAll(temp);
        }

        if (returnList.isEmpty()) {
            throw new SteveException.BadRequest("No stations are eligible for communication. Ensure that the chargeBox IDs are correct and the stations are online.");
        }

        return returnList;
    }

    private void validateIdTags(List<String> idTagList) {
        if (CollectionUtils.isEmpty(idTagList)) {
            return;
        }

        List<String> idTagListFromDB = ocppTagService.getIdTags(idTagList);
        if (idTagList.size() != idTagListFromDB.size()) {
            throw new SteveException.BadRequest("Some OCPP Tags are unknown. Cannot continue with this operation.");
        }
    }

    private void validateIdTag(String idTag) {
        if (!ocppTagService.isActive(idTag)) {
            throw new SteveException.BadRequest("Requested OCPP Tag is not eligible for this operation. Ensure that the OCPP Tag is registered and active.");
        }
    }

    private void validateChargingProfile(Integer chargingProfilePk) {
        if (chargingProfilePk == null) {
            return; // ClearChargingProfile allows chargingProfilePk to be optional.
        }

        if (!chargingProfileRepository.exists(chargingProfilePk)) {
            throw new SteveException.BadRequest("chargingProfilePk is unknown.");
        }
    }

    private void validateTransactionId(Integer transactionId, List<String> chargeBoxIdList) {
        if (transactionId == null) {
            return; // not this method's responsibility
        }

        // if transactionId is non-null, there must be only one station
        String chargeBoxId = chargeBoxIdList.getFirst();

        List<Integer> activeTransactionIds = transactionService.getActiveTransactionIds(chargeBoxId);
        if (!activeTransactionIds.contains(transactionId)) {
            throw new SteveException.BadRequest("This transaction is not active at this station.");
        }
    }

    private <T> RestCallback<T> createCallback(ChargePointSelection chargePointSelection) {
        return new RestCallback<>(STATION_RESPONSE_TIMEOUT, new CountDownLatch(chargePointSelection.getChargePointSelectList().size()));
    }

    private <P extends ChargePointSelection, T> RestCallback<T> execute(
        P params,
        BiFunction<P, RestCallback<T>, Integer> function
    ) throws Exception {
        params.setChargePointSelectList(fetchChargePoints(params.getChargeBoxIdList()));

        RestCallback<T> callback = createCallback(params);
        int taskId = function.apply(params, callback);

        callback.setTaskId(taskId);
        callback.waitForResponses();
        return callback;
    }
}
