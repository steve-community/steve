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
package de.rwth.idsg.steve.ocpp;

import de.rwth.idsg.steve.ocpp.soap.ChargePointServiceSoapInvoker;
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
import de.rwth.idsg.steve.ocpp.ws.ChargePointServiceJsonInvoker;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.function.BiConsumer;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 05.01.2025
 */
@RequiredArgsConstructor
@Service
@Primary
public class ChargePointServiceInvokerImpl implements ChargePointServiceInvoker {

    private final ChargePointServiceSoapInvoker chargePointServiceSoapInvoker;
    private final ChargePointServiceJsonInvoker chargePointServiceJsonInvoker;

    private <T extends CommunicationTask<?, ?>> void invoke(
            ChargePointSelect cp,
            T task,
            BiConsumer<ChargePointSelect, T> soapAction,
            BiConsumer<ChargePointSelect, T> jsonAction) {
        if (cp.isSoap()) {
            soapAction.accept(cp, task);
        } else {
            jsonAction.accept(cp, task);
        }
    }

    @Override
    public void reset(ChargePointSelect cp, ResetTask task) {
        invoke(cp, task, chargePointServiceSoapInvoker::reset, chargePointServiceJsonInvoker::reset);
    }

    @Override
    public void clearCache(ChargePointSelect cp, ClearCacheTask task) {
        invoke(cp, task, chargePointServiceSoapInvoker::clearCache, chargePointServiceJsonInvoker::clearCache);
    }

    @Override
    public void getDiagnostics(ChargePointSelect cp, GetDiagnosticsTask task) {
        invoke(cp, task, chargePointServiceSoapInvoker::getDiagnostics, chargePointServiceJsonInvoker::getDiagnostics);
    }

    @Override
    public void updateFirmware(ChargePointSelect cp, UpdateFirmwareTask task) {
        invoke(cp, task, chargePointServiceSoapInvoker::updateFirmware, chargePointServiceJsonInvoker::updateFirmware);
    }

    @Override
    public void unlockConnector(ChargePointSelect cp, UnlockConnectorTask task) {
        invoke(
                cp,
                task,
                chargePointServiceSoapInvoker::unlockConnector,
                chargePointServiceJsonInvoker::unlockConnector);
    }

    public void changeAvailability(ChargePointSelect cp, ChangeAvailabilityTask task) {
        invoke(
                cp,
                task,
                chargePointServiceSoapInvoker::changeAvailability,
                chargePointServiceJsonInvoker::changeAvailability);
    }

    @Override
    public void changeConfiguration(ChargePointSelect cp, ChangeConfigurationTask task) {
        invoke(
                cp,
                task,
                chargePointServiceSoapInvoker::changeConfiguration,
                chargePointServiceJsonInvoker::changeConfiguration);
    }

    @Override
    public void remoteStartTransaction(ChargePointSelect cp, RemoteStartTransactionTask task) {
        invoke(
                cp,
                task,
                chargePointServiceSoapInvoker::remoteStartTransaction,
                chargePointServiceJsonInvoker::remoteStartTransaction);
    }

    @Override
    public void remoteStopTransaction(ChargePointSelect cp, RemoteStopTransactionTask task) {
        invoke(
                cp,
                task,
                chargePointServiceSoapInvoker::remoteStopTransaction,
                chargePointServiceJsonInvoker::remoteStopTransaction);
    }

    @Override
    public void dataTransfer(ChargePointSelect cp, DataTransferTask task) {
        invoke(cp, task, chargePointServiceSoapInvoker::dataTransfer, chargePointServiceJsonInvoker::dataTransfer);
    }

    @Override
    public void getConfiguration(ChargePointSelect cp, GetConfigurationTask task) {
        invoke(
                cp,
                task,
                chargePointServiceSoapInvoker::getConfiguration,
                chargePointServiceJsonInvoker::getConfiguration);
    }

    @Override
    public void getLocalListVersion(ChargePointSelect cp, GetLocalListVersionTask task) {
        invoke(
                cp,
                task,
                chargePointServiceSoapInvoker::getLocalListVersion,
                chargePointServiceJsonInvoker::getLocalListVersion);
    }

    @Override
    public void sendLocalList(ChargePointSelect cp, SendLocalListTask task) {
        invoke(cp, task, chargePointServiceSoapInvoker::sendLocalList, chargePointServiceJsonInvoker::sendLocalList);
    }

    @Override
    public void reserveNow(ChargePointSelect cp, ReserveNowTask task) {
        invoke(cp, task, chargePointServiceSoapInvoker::reserveNow, chargePointServiceJsonInvoker::reserveNow);
    }

    @Override
    public void cancelReservation(ChargePointSelect cp, CancelReservationTask task) {
        invoke(
                cp,
                task,
                chargePointServiceSoapInvoker::cancelReservation,
                chargePointServiceJsonInvoker::cancelReservation);
    }

    @Override
    public void clearChargingProfile(ChargePointSelect cp, ClearChargingProfileTask task) {
        invoke(
                cp,
                task,
                chargePointServiceSoapInvoker::clearChargingProfile,
                chargePointServiceJsonInvoker::clearChargingProfile);
    }

    @Override
    public void setChargingProfile(ChargePointSelect cp, SetChargingProfileTask task) {
        invoke(
                cp,
                task,
                chargePointServiceSoapInvoker::setChargingProfile,
                chargePointServiceJsonInvoker::setChargingProfile);
    }

    @Override
    public void getCompositeSchedule(ChargePointSelect cp, GetCompositeScheduleTask task) {
        invoke(
                cp,
                task,
                chargePointServiceSoapInvoker::getCompositeSchedule,
                chargePointServiceJsonInvoker::getCompositeSchedule);
    }

    @Override
    public void triggerMessage(ChargePointSelect cp, TriggerMessageTask task) {
        invoke(cp, task, chargePointServiceSoapInvoker::triggerMessage, chargePointServiceJsonInvoker::triggerMessage);
    }
}
