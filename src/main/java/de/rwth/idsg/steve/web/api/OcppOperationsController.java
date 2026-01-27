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
package de.rwth.idsg.steve.web.api;

import de.rwth.idsg.steve.ocpp.task.GetConfigurationTask;
import de.rwth.idsg.steve.service.OcppOperationsService;
import de.rwth.idsg.steve.web.dto.OcppOperationResponse;
import de.rwth.idsg.steve.web.dto.ocpp.CancelReservationParams;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ocpp.cp._2015._10.GetCompositeScheduleResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 05.01.2025
 */
@Tag(name = "ocpp-operations-controller",
    description = """
        Operations that can be initiated by the Central System at the Charge Points.
        Some of these operations allows multiple Charge Points to be selected, whereas others are specific to a single Charge Point.
        For the detailed information on the parameters, please refer to the OCPP specification.
        """
)
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/operations", produces = MediaType.APPLICATION_JSON_VALUE)
public class OcppOperationsController {

    private final OcppOperationsService operationsService;

    // -------------------------------------------------------------------------
    // Since Ocpp 1.2
    // -------------------------------------------------------------------------

    @PostMapping(value = "/ChangeAvailability")
    public OcppOperationResponse<String> changeAvailability(@RequestBody @Valid ChangeAvailabilityParams params) throws Exception {
        var callback = operationsService.changeAvailability(params);
        return OcppOperationResponse.from(callback);
    }

    @PostMapping(value = "/ChangeConfiguration")
    public OcppOperationResponse<String> changeConfiguration(@RequestBody @Valid ChangeConfigurationParams params) throws Exception {
        var callback = operationsService.changeConfiguration(params);
        return OcppOperationResponse.from(callback);
    }

    @PostMapping(value = "/ClearCache")
    public OcppOperationResponse<String> clearCache(@RequestBody @Valid MultipleChargePointSelect params) throws Exception {
        var callback = operationsService.clearCache(params);
        return OcppOperationResponse.from(callback);
    }

    @PostMapping(value = "/GetDiagnostics")
    public OcppOperationResponse<String> getDiagnostics(@RequestBody @Valid GetDiagnosticsParams params) throws Exception {
        var callback = operationsService.getDiagnostics(params);
        return OcppOperationResponse.from(callback);
    }

    @Operation(description = """
        Only 1 charge point can be selected.
        """)
    @PostMapping(value = "/RemoteStartTransaction")
    public OcppOperationResponse<String> remoteStartTransaction(@RequestBody @Valid RemoteStartTransactionParams params) throws Exception {
        var callback = operationsService.remoteStartTransaction(params);
        return OcppOperationResponse.from(callback);
    }

    @Operation(description = """
        Only 1 charge point can be selected.
        """)
    @PostMapping(value = "/RemoteStopTransaction")
    public OcppOperationResponse<String> remoteStopTransaction(@RequestBody @Valid RemoteStopTransactionParams params) throws Exception {
        var callback = operationsService.remoteStopTransaction(params);
        return OcppOperationResponse.from(callback);
    }

    @PostMapping(value = "/Reset")
    public OcppOperationResponse<String> reset(@RequestBody @Valid ResetParams params) throws Exception {
        var callback = operationsService.reset(params);
        return OcppOperationResponse.from(callback);
    }

    @Operation(description = """
        Only 1 charge point can be selected.
        """)
    @PostMapping(value = "/UnlockConnector")
    public OcppOperationResponse<String> unlockConnector(@RequestBody @Valid UnlockConnectorParams params) throws Exception {
        var callback = operationsService.unlockConnector(params);
        return OcppOperationResponse.from(callback);
    }

    @PostMapping(value = "/UpdateFirmware")
    public OcppOperationResponse<String> updateFirmware(@RequestBody @Valid UpdateFirmwareParams params) throws Exception {
        var callback = operationsService.updateFirmware(params);
        return OcppOperationResponse.from(callback);
    }

    // -------------------------------------------------------------------------
    // Since Ocpp 1.5
    // -------------------------------------------------------------------------

    @Operation(description = """
        Only 1 charge point can be selected.
        """)
    @PostMapping(value = "/ReserveNow")
    public OcppOperationResponse<String> reserveNow(@RequestBody @Valid ReserveNowParams params) throws Exception {
        var callback = operationsService.reserveNow(params);
        return OcppOperationResponse.from(callback);
    }

    @Operation(description = """
        Only 1 charge point can be selected.
        """)
    @PostMapping(value = "/CancelReservation")
    public OcppOperationResponse<String> cancelReservation(@RequestBody @Valid CancelReservationParams params) throws Exception {
        var callback = operationsService.cancelReservation(params);
        return OcppOperationResponse.from(callback);
    }

    @PostMapping(value = "/DataTransfer")
    public OcppOperationResponse<ocpp.cp._2015._10.DataTransferResponse> dataTransfer(@RequestBody @Valid DataTransferParams params) throws Exception {
        var callback = operationsService.dataTransfer(params);
        return OcppOperationResponse.from(callback);
    }

    @PostMapping(value = "/GetConfiguration")
    public OcppOperationResponse<GetConfigurationTask.ConfigurationKeyValues> getConfiguration(@RequestBody @Valid GetConfigurationParams params) throws Exception {
        var callback = operationsService.getConfiguration(params);
        return OcppOperationResponse.from(callback);
    }

    @PostMapping(value = "/GetLocalListVersion")
    public OcppOperationResponse<String> getLocalListVersion(@RequestBody @Valid MultipleChargePointSelect params) throws Exception {
        var callback = operationsService.getLocalListVersion(params);
        return OcppOperationResponse.from(callback);
    }

    @PostMapping(value = "/SendLocalList")
    public OcppOperationResponse<String> sendLocalList(@RequestBody @Valid SendLocalListParams params) throws Exception {
        var callback = operationsService.sendLocalList(params);
        return OcppOperationResponse.from(callback);
    }

    // -------------------------------------------------------------------------
    // Since Ocpp 1.6
    // -------------------------------------------------------------------------

    @PostMapping(value = "/TriggerMessage")
    public OcppOperationResponse<String> triggerMessage(@RequestBody @Valid TriggerMessageParams params) throws Exception {
        var callback = operationsService.triggerMessage(params);
        return OcppOperationResponse.from(callback);
    }

    @PostMapping(value = "/GetCompositeSchedule")
    public OcppOperationResponse<GetCompositeScheduleResponse> getCompositeSchedule(@RequestBody @Valid GetCompositeScheduleParams params) throws Exception {
        var callback = operationsService.getCompositeSchedule(params);
        return OcppOperationResponse.from(callback);
    }

    @PostMapping(value = "/ClearChargingProfile")
    public OcppOperationResponse<String> clearChargingProfile(@RequestBody @Valid ClearChargingProfileParams params) throws Exception {
        var callback = operationsService.clearChargingProfile(params);
        return OcppOperationResponse.from(callback);
    }

    @PostMapping(value = "/SetChargingProfile")
    public OcppOperationResponse<String> setChargingProfile(@RequestBody @Valid SetChargingProfileParams params) throws Exception {
        var callback = operationsService.setChargingProfile(params);
        return OcppOperationResponse.from(callback);
    }

    // -------------------------------------------------------------------------
    // "Improved security for OCPP 1.6-J" additions
    // -------------------------------------------------------------------------

    @Operation(description = """
        As specified in <i>Improved security for OCPP 1.6-J</i> white paper.
        """)
    @PostMapping(value = "/ExtendedTriggerMessage")
    public OcppOperationResponse<String> extendedTriggerMessage(@RequestBody @Valid ExtendedTriggerMessageParams params) throws Exception {
        var callback = operationsService.extendedTriggerMessage(params);
        return OcppOperationResponse.from(callback);
    }

    @Operation(description = """
        As specified in <i>Improved security for OCPP 1.6-J</i> white paper.
        Request ID will be set automatically by backend.
        Once this operation is triggered at a station, it will send status notifications about the progress and result.
        """)
    @PostMapping(value = "/GetLog")
    public OcppOperationResponse<String> getLog(@RequestBody @Valid GetLogParams params) throws Exception {
        var callback = operationsService.getLog(params);
        return OcppOperationResponse.from(callback);
    }

    @Operation(description = """
        As specified in <i>Improved security for OCPP 1.6-J</i> white paper.
        Request ID will be set automatically by backend.
        Once this operation is triggered at a station, it will send status notifications about the progress and result.
        """)
    @PostMapping(value = "/SignedUpdateFirmware")
    public OcppOperationResponse<String> signedUpdateFirmware(@RequestBody @Valid SignedUpdateFirmwareParams params) throws Exception {
        var callback = operationsService.signedUpdateFirmware(params);
        return OcppOperationResponse.from(callback);
    }

    @Operation(description = """
        As specified in <i>Improved security for OCPP 1.6-J</i> white paper.
        The certificates sent by the station will be stored in database.
        """)
    @PostMapping(value = "/GetInstalledCertificateIds")
    public OcppOperationResponse<String> getInstalledCertificateIds(@RequestBody @Valid GetInstalledCertificateIdsParams params) throws Exception {
        var callback = operationsService.getInstalledCertificateIds(params);
        return OcppOperationResponse.from(callback);
    }

    @Operation(description = """
        As specified in <i>Improved security for OCPP 1.6-J</i> white paper.
        """)
    @PostMapping(value = "/InstallCertificate")
    public OcppOperationResponse<String> installCertificate(@RequestBody @Valid InstallCertificateParams params) throws Exception {
        var callback = operationsService.installCertificate(params);
        return OcppOperationResponse.from(callback);
    }

    @Operation(description = """
        As specified in <i>Improved security for OCPP 1.6-J</i> white paper.
        <code>installedCertificateId</code> in payload is the database ID.
        Only 1 charge point can be selected.
        """)
    @PostMapping(value = "/DeleteCertificate")
    public OcppOperationResponse<String> deleteCertificate(@RequestBody @Valid DeleteCertificateParams params) throws Exception {
        var callback = operationsService.deleteCertificate(params);
        return OcppOperationResponse.from(callback);
    }
}
