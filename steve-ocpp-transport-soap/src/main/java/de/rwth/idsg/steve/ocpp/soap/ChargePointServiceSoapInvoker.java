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
package de.rwth.idsg.steve.ocpp.soap;

import de.rwth.idsg.steve.ocpp.ChargePointServiceInvoker;
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
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 05.01.2025
 */
@Slf4j
@Service
public class ChargePointServiceSoapInvoker implements ChargePointServiceInvoker {

    public static final Exception EXCEPTION_V12 =
            new IllegalArgumentException("This operation is not supported by this OCPP 1.2 station");
    public static final Exception EXCEPTION_V15 =
            new IllegalArgumentException("This operation is not supported by this OCPP 1.5 station");

    private final ClientProviderWithCache<ocpp.cp._2010._08.ChargePointService> soapV12Helper;
    private final ClientProviderWithCache<ocpp.cp._2012._06.ChargePointService> soapV15Helper;
    private final ClientProviderWithCache<ocpp.cp._2015._10.ChargePointService> soapV16Helper;

    public ChargePointServiceSoapInvoker(ClientProvider clientProvider) {
        this.soapV12Helper = new ClientProviderWithCache<>(clientProvider);
        this.soapV15Helper = new ClientProviderWithCache<>(clientProvider);
        this.soapV16Helper = new ClientProviderWithCache<>(clientProvider);
    }

    @Override
    public void reset(ChargePointSelect cp, ResetTask task) {
        OcppVersion ocppVersion = cp.getOcppProtocol().getVersion();
        switch (ocppVersion) {
            case V_12 ->
                createV12(cp)
                        .resetAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
            case V_15 ->
                createV15(cp)
                        .resetAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
            case V_16 ->
                createV16(cp)
                        .resetAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
        }
    }

    @Override
    public void clearCache(ChargePointSelect cp, ClearCacheTask task) {
        OcppVersion ocppVersion = cp.getOcppProtocol().getVersion();
        switch (ocppVersion) {
            case V_12 ->
                createV12(cp)
                        .clearCacheAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
            case V_15 ->
                createV15(cp)
                        .clearCacheAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
            case V_16 ->
                createV16(cp)
                        .clearCacheAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
        }
    }

    @Override
    public void getDiagnostics(ChargePointSelect cp, GetDiagnosticsTask task) {
        OcppVersion ocppVersion = cp.getOcppProtocol().getVersion();
        switch (ocppVersion) {
            case V_12 ->
                createV12(cp)
                        .getDiagnosticsAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
            case V_15 ->
                createV15(cp)
                        .getDiagnosticsAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
            case V_16 ->
                createV16(cp)
                        .getDiagnosticsAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
        }
    }

    @Override
    public void updateFirmware(ChargePointSelect cp, UpdateFirmwareTask task) {
        OcppVersion ocppVersion = cp.getOcppProtocol().getVersion();
        switch (ocppVersion) {
            case V_12 ->
                createV12(cp)
                        .updateFirmwareAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
            case V_15 ->
                createV15(cp)
                        .updateFirmwareAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
            case V_16 ->
                createV16(cp)
                        .updateFirmwareAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
        }
    }

    @Override
    public void unlockConnector(ChargePointSelect cp, UnlockConnectorTask task) {
        OcppVersion ocppVersion = cp.getOcppProtocol().getVersion();
        switch (ocppVersion) {
            case V_12 ->
                createV12(cp)
                        .unlockConnectorAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
            case V_15 ->
                createV15(cp)
                        .unlockConnectorAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
            case V_16 ->
                createV16(cp)
                        .unlockConnectorAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
        }
    }

    public void changeAvailability(ChargePointSelect cp, ChangeAvailabilityTask task) {
        OcppVersion ocppVersion = cp.getOcppProtocol().getVersion();
        switch (ocppVersion) {
            case V_12 ->
                createV12(cp)
                        .changeAvailabilityAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
            case V_15 ->
                createV15(cp)
                        .changeAvailabilityAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
            case V_16 ->
                createV16(cp)
                        .changeAvailabilityAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
        }
    }

    @Override
    public void changeConfiguration(ChargePointSelect cp, ChangeConfigurationTask task) {
        OcppVersion ocppVersion = cp.getOcppProtocol().getVersion();
        switch (ocppVersion) {
            case V_12 ->
                createV12(cp)
                        .changeConfigurationAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
            case V_15 ->
                createV15(cp)
                        .changeConfigurationAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
            case V_16 ->
                createV16(cp)
                        .changeConfigurationAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
        }
    }

    @Override
    public void remoteStartTransaction(ChargePointSelect cp, RemoteStartTransactionTask task) {
        OcppVersion ocppVersion = cp.getOcppProtocol().getVersion();
        switch (ocppVersion) {
            case V_12 ->
                createV12(cp)
                        .remoteStartTransactionAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
            case V_15 ->
                createV15(cp)
                        .remoteStartTransactionAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
            case V_16 ->
                createV16(cp)
                        .remoteStartTransactionAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
        }
    }

    @Override
    public void remoteStopTransaction(ChargePointSelect cp, RemoteStopTransactionTask task) {
        OcppVersion ocppVersion = cp.getOcppProtocol().getVersion();
        switch (ocppVersion) {
            case V_12 ->
                createV12(cp)
                        .remoteStopTransactionAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
            case V_15 ->
                createV15(cp)
                        .remoteStopTransactionAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
            case V_16 ->
                createV16(cp)
                        .remoteStopTransactionAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
        }
    }

    @Override
    public void dataTransfer(ChargePointSelect cp, DataTransferTask task) {
        OcppVersion ocppVersion = cp.getOcppProtocol().getVersion();
        switch (ocppVersion) {
            case V_12 -> task.failed(cp.getChargeBoxId(), EXCEPTION_V12);
            case V_15 ->
                createV15(cp)
                        .dataTransferAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
            case V_16 ->
                createV16(cp)
                        .dataTransferAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
        }
    }

    @Override
    public void getConfiguration(ChargePointSelect cp, GetConfigurationTask task) {
        OcppVersion ocppVersion = cp.getOcppProtocol().getVersion();
        switch (ocppVersion) {
            case V_12 -> task.failed(cp.getChargeBoxId(), EXCEPTION_V12);
            case V_15 ->
                createV15(cp)
                        .getConfigurationAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
            case V_16 ->
                createV16(cp)
                        .getConfigurationAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
        }
    }

    @Override
    public void getLocalListVersion(ChargePointSelect cp, GetLocalListVersionTask task) {
        OcppVersion ocppVersion = cp.getOcppProtocol().getVersion();
        switch (ocppVersion) {
            case V_12 -> task.failed(cp.getChargeBoxId(), EXCEPTION_V12);
            case V_15 ->
                createV15(cp)
                        .getLocalListVersionAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
            case V_16 ->
                createV16(cp)
                        .getLocalListVersionAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
        }
    }

    @Override
    public void sendLocalList(ChargePointSelect cp, SendLocalListTask task) {
        OcppVersion ocppVersion = cp.getOcppProtocol().getVersion();
        switch (ocppVersion) {
            case V_12 -> task.failed(cp.getChargeBoxId(), EXCEPTION_V12);
            case V_15 ->
                createV15(cp)
                        .sendLocalListAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
            case V_16 ->
                createV16(cp)
                        .sendLocalListAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
        }
    }

    @Override
    public void reserveNow(ChargePointSelect cp, ReserveNowTask task) {
        OcppVersion ocppVersion = cp.getOcppProtocol().getVersion();
        switch (ocppVersion) {
            case V_12 -> task.failed(cp.getChargeBoxId(), EXCEPTION_V12);
            case V_15 ->
                createV15(cp)
                        .reserveNowAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
            case V_16 ->
                createV16(cp)
                        .reserveNowAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
        }
    }

    @Override
    public void cancelReservation(ChargePointSelect cp, CancelReservationTask task) {
        OcppVersion ocppVersion = cp.getOcppProtocol().getVersion();
        switch (ocppVersion) {
            case V_12 -> task.failed(cp.getChargeBoxId(), EXCEPTION_V12);
            case V_15 ->
                createV15(cp)
                        .cancelReservationAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
            case V_16 ->
                createV16(cp)
                        .cancelReservationAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
        }
    }

    @Override
    public void clearChargingProfile(ChargePointSelect cp, ClearChargingProfileTask task) {
        OcppVersion ocppVersion = cp.getOcppProtocol().getVersion();
        switch (ocppVersion) {
            case V_12 -> task.failed(cp.getChargeBoxId(), EXCEPTION_V12);
            case V_15 -> task.failed(cp.getChargeBoxId(), EXCEPTION_V15);
            case V_16 ->
                createV16(cp)
                        .clearChargingProfileAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
        }
    }

    @Override
    public void setChargingProfile(ChargePointSelect cp, SetChargingProfileTask task) {
        OcppVersion ocppVersion = cp.getOcppProtocol().getVersion();
        switch (ocppVersion) {
            case V_12 -> task.failed(cp.getChargeBoxId(), EXCEPTION_V12);
            case V_15 -> task.failed(cp.getChargeBoxId(), EXCEPTION_V15);
            case V_16 ->
                createV16(cp)
                        .setChargingProfileAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
        }
    }

    @Override
    public void getCompositeSchedule(ChargePointSelect cp, GetCompositeScheduleTask task) {
        OcppVersion ocppVersion = cp.getOcppProtocol().getVersion();
        switch (ocppVersion) {
            case V_12 -> task.failed(cp.getChargeBoxId(), EXCEPTION_V12);
            case V_15 -> task.failed(cp.getChargeBoxId(), EXCEPTION_V15);
            case V_16 ->
                createV16(cp)
                        .getCompositeScheduleAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
        }
    }

    @Override
    public void triggerMessage(ChargePointSelect cp, TriggerMessageTask task) {
        OcppVersion ocppVersion = cp.getOcppProtocol().getVersion();
        switch (ocppVersion) {
            case V_12 -> task.failed(cp.getChargeBoxId(), EXCEPTION_V12);
            case V_15 -> task.failed(cp.getChargeBoxId(), EXCEPTION_V15);
            case V_16 ->
                createV16(cp)
                        .triggerMessageAsync(
                                task.getRequest(ocppVersion),
                                cp.getChargeBoxId(),
                                task.createHandler(cp.getChargeBoxId()));
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private ocpp.cp._2010._08.ChargePointService createV12(ChargePointSelect cp) {
        return soapV12Helper.createClient(ocpp.cp._2010._08.ChargePointService.class, cp.getEndpointAddress());
    }

    private ocpp.cp._2012._06.ChargePointService createV15(ChargePointSelect cp) {
        return soapV15Helper.createClient(ocpp.cp._2012._06.ChargePointService.class, cp.getEndpointAddress());
    }

    private ocpp.cp._2015._10.ChargePointService createV16(ChargePointSelect cp) {
        return soapV16Helper.createClient(ocpp.cp._2015._10.ChargePointService.class, cp.getEndpointAddress());
    }
}
