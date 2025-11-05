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
import de.rwth.idsg.steve.ocpp.task.ExtendedTriggerMessageTask;
import de.rwth.idsg.steve.ocpp.task.GetCompositeScheduleTask;
import de.rwth.idsg.steve.ocpp.task.GetConfigurationTask;
import de.rwth.idsg.steve.ocpp.task.GetDiagnosticsTask;
import de.rwth.idsg.steve.ocpp.task.GetLocalListVersionTask;
import de.rwth.idsg.steve.ocpp.task.GetLogTask;
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
import org.springframework.stereotype.Service;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 05.01.2025
 */
@RequiredArgsConstructor
@Service
public class ChargePointServiceInvokerImpl implements ChargePointServiceInvoker {

    private final ChargePointServiceSoapInvoker chargePointServiceSoapInvoker;
    private final ChargePointServiceJsonInvoker chargePointServiceJsonInvoker;

    // -------------------------------------------------------------------------
    // since Ocpp 1.2
    // -------------------------------------------------------------------------

    @Override
    public void reset(ChargePointSelect cp, ResetTask task) {
        if (cp.isSoap()) {
            chargePointServiceSoapInvoker.reset(cp, task);
        } else {
            chargePointServiceJsonInvoker.runPipeline(cp, task);
        }
    }

    @Override
    public void clearCache(ChargePointSelect cp, ClearCacheTask task) {
        if (cp.isSoap()) {
            chargePointServiceSoapInvoker.clearCache(cp, task);
        } else {
            chargePointServiceJsonInvoker.runPipeline(cp, task);
        }
    }

    @Override
    public void getDiagnostics(ChargePointSelect cp, GetDiagnosticsTask task) {
        if (cp.isSoap()) {
            chargePointServiceSoapInvoker.getDiagnostics(cp, task);
        } else {
            chargePointServiceJsonInvoker.runPipeline(cp, task);
        }
    }

    @Override
    public void updateFirmware(ChargePointSelect cp, UpdateFirmwareTask task) {
        if (cp.isSoap()) {
            chargePointServiceSoapInvoker.updateFirmware(cp, task);
        } else {
            chargePointServiceJsonInvoker.runPipeline(cp, task);
        }
    }

    @Override
    public void unlockConnector(ChargePointSelect cp, UnlockConnectorTask task) {
        if (cp.isSoap()) {
            chargePointServiceSoapInvoker.unlockConnector(cp, task);
        } else {
            chargePointServiceJsonInvoker.runPipeline(cp, task);
        }
    }

    public void changeAvailability(ChargePointSelect cp, ChangeAvailabilityTask task) {
        if (cp.isSoap()) {
            chargePointServiceSoapInvoker.changeAvailability(cp, task);
        } else {
            chargePointServiceJsonInvoker.runPipeline(cp, task);
        }
    }

    @Override
    public void changeConfiguration(ChargePointSelect cp, ChangeConfigurationTask task) {
        if (cp.isSoap()) {
            chargePointServiceSoapInvoker.changeConfiguration(cp, task);
        } else {
            chargePointServiceJsonInvoker.runPipeline(cp, task);
        }
    }

    @Override
    public void remoteStartTransaction(ChargePointSelect cp, RemoteStartTransactionTask task) {
        if (cp.isSoap()) {
            chargePointServiceSoapInvoker.remoteStartTransaction(cp, task);
        } else {
            chargePointServiceJsonInvoker.runPipeline(cp, task);
        }
    }

    @Override
    public void remoteStopTransaction(ChargePointSelect cp, RemoteStopTransactionTask task) {
        if (cp.isSoap()) {
            chargePointServiceSoapInvoker.remoteStopTransaction(cp, task);
        } else {
            chargePointServiceJsonInvoker.runPipeline(cp, task);
        }
    }

    // -------------------------------------------------------------------------
    // since Ocpp 1.5
    // -------------------------------------------------------------------------

    @Override
    public void dataTransfer(ChargePointSelect cp, DataTransferTask task) {
        if (cp.isSoap()) {
            chargePointServiceSoapInvoker.dataTransfer(cp, task);
        } else {
            chargePointServiceJsonInvoker.runPipeline(cp, task);
        }
    }

    @Override
    public void getConfiguration(ChargePointSelect cp, GetConfigurationTask task) {
        if (cp.isSoap()) {
            chargePointServiceSoapInvoker.getConfiguration(cp, task);
        } else {
            chargePointServiceJsonInvoker.runPipeline(cp, task);
        }
    }

    @Override
    public void getLocalListVersion(ChargePointSelect cp, GetLocalListVersionTask task) {
        if (cp.isSoap()) {
            chargePointServiceSoapInvoker.getLocalListVersion(cp, task);
        } else {
            chargePointServiceJsonInvoker.runPipeline(cp, task);
        }
    }

    @Override
    public void sendLocalList(ChargePointSelect cp, SendLocalListTask task) {
        if (cp.isSoap()) {
            chargePointServiceSoapInvoker.sendLocalList(cp, task);
        } else {
            chargePointServiceJsonInvoker.runPipeline(cp, task);
        }
    }

    @Override
    public void reserveNow(ChargePointSelect cp, ReserveNowTask task) {
        if (cp.isSoap()) {
            chargePointServiceSoapInvoker.reserveNow(cp, task);
        } else {
            chargePointServiceJsonInvoker.runPipeline(cp, task);
        }
    }

    @Override
    public void cancelReservation(ChargePointSelect cp, CancelReservationTask task) {
        if (cp.isSoap()) {
            chargePointServiceSoapInvoker.cancelReservation(cp, task);
        } else {
            chargePointServiceJsonInvoker.runPipeline(cp, task);
        }
    }

    // -------------------------------------------------------------------------
    // since Ocpp 1.6
    // -------------------------------------------------------------------------

    @Override
    public void clearChargingProfile(ChargePointSelect cp, ClearChargingProfileTask task) {
        if (cp.isSoap()) {
            chargePointServiceSoapInvoker.clearChargingProfile(cp, task);
        } else {
            chargePointServiceJsonInvoker.runPipeline(cp, task);
        }
    }

    @Override
    public void setChargingProfile(ChargePointSelect cp, SetChargingProfileTask task) {
        if (cp.isSoap()) {
            chargePointServiceSoapInvoker.setChargingProfile(cp, task);
        } else {
            chargePointServiceJsonInvoker.runPipeline(cp, task);
        }
    }

    @Override
    public void getCompositeSchedule(ChargePointSelect cp, GetCompositeScheduleTask task) {
        if (cp.isSoap()) {
            chargePointServiceSoapInvoker.getCompositeSchedule(cp, task);
        } else {
            chargePointServiceJsonInvoker.runPipeline(cp, task);
        }
    }

    @Override
    public void triggerMessage(ChargePointSelect cp, TriggerMessageTask task) {
        if (cp.isSoap()) {
            chargePointServiceSoapInvoker.triggerMessage(cp, task);
        } else {
            chargePointServiceJsonInvoker.runPipeline(cp, task);
        }
    }

    // -------------------------------------------------------------------------
    // "Improved security for OCPP 1.6-J" additions. Only for JSON
    // -------------------------------------------------------------------------

    public void extendedTriggerMessage(ChargePointSelect cp, ExtendedTriggerMessageTask task) {
        if (cp.isJson()) {
            chargePointServiceJsonInvoker.runPipeline(cp, task);
        }
    }

    public void getLog(ChargePointSelect cp, GetLogTask task) {
        if (cp.isJson()) {
            chargePointServiceJsonInvoker.runPipeline(cp, task);
        }
    }
}
