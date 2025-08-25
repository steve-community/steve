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
package de.rwth.idsg.steve.ocpp.ws;

import de.rwth.idsg.steve.ocpp.ChargePointServiceInvoker;
import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppTransport;
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
import de.rwth.idsg.steve.ocpp.ws.pipeline.OutgoingCallPipeline;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 20.03.2015
 */
@Slf4j
@Service
public class ChargePointServiceJsonInvoker implements ChargePointServiceInvoker {

    private final OutgoingCallPipeline outgoingCallPipeline;
    private final Map<OcppVersion, InvocationContext> invocationContexts;

    public ChargePointServiceJsonInvoker(OutgoingCallPipeline outgoingCallPipeline,
                                         @Autowired
                                         @Qualifier("invocationContexts")
                                         Map<OcppVersion, InvocationContext> invocationContexts) {
        this.outgoingCallPipeline = outgoingCallPipeline;
        this.invocationContexts = invocationContexts;
    }

    /**
     * Just a wrapper to make try-catch block and exception handling stand out
     */
    private void runPipeline(ChargePointSelect cps, CommunicationTask task) {
        try {
            run(cps, task);
        } catch (Exception e) {
            log.error("Exception occurred", e);
            // Outgoing call failed due to technical problems. Pass the exception to handler to inform the user
            task.failed(cps.getChargeBoxId(), e);
        }
    }

    /**
     * Actual processing
     */
    private void run(ChargePointSelect cps, CommunicationTask<?, ?> task) {
        var protocol = cps.getOcppProtocol();
        if (protocol.getTransport() != OcppTransport.JSON) {
            throw new IllegalArgumentException("Only JSON transport is supported here");
        }
        if (!invocationContexts.containsKey(protocol.getVersion())) {
            throw new IllegalArgumentException("Unsupported OCPP version: " + protocol.getVersion());
        }
        var context = invocationContexts.get(protocol.getVersion())
                .toCommunicationContext(cps.getChargeBoxId(), task);
        outgoingCallPipeline.accept(context);
    }

    @Override
    public void reset(ChargePointSelect cp, ResetTask task) {
        runPipeline(cp, task);
    }

    @Override
    public void clearCache(ChargePointSelect cp, ClearCacheTask task) {
        runPipeline(cp, task);
    }

    @Override
    public void getDiagnostics(ChargePointSelect cp, GetDiagnosticsTask task) {
        runPipeline(cp, task);
    }

    @Override
    public void updateFirmware(ChargePointSelect cp, UpdateFirmwareTask task) {
        runPipeline(cp, task);
    }

    @Override
    public void unlockConnector(ChargePointSelect cp, UnlockConnectorTask task) {
        runPipeline(cp, task);
    }

    @Override
    public void changeAvailability(ChargePointSelect cp, ChangeAvailabilityTask task) {
        runPipeline(cp, task);
    }

    @Override
    public void changeConfiguration(ChargePointSelect cp, ChangeConfigurationTask task) {
        runPipeline(cp, task);
    }

    @Override
    public void remoteStartTransaction(ChargePointSelect cp, RemoteStartTransactionTask task) {
        runPipeline(cp, task);
    }

    @Override
    public void remoteStopTransaction(ChargePointSelect cp, RemoteStopTransactionTask task) {
        runPipeline(cp, task);
    }

    @Override
    public void dataTransfer(ChargePointSelect cp, DataTransferTask task) {
        runPipeline(cp, task);
    }

    @Override
    public void getConfiguration(ChargePointSelect cp, GetConfigurationTask task) {
        runPipeline(cp, task);
    }

    @Override
    public void getLocalListVersion(ChargePointSelect cp, GetLocalListVersionTask task) {
        runPipeline(cp, task);
    }

    @Override
    public void sendLocalList(ChargePointSelect cp, SendLocalListTask task) {
        runPipeline(cp, task);
    }

    @Override
    public void reserveNow(ChargePointSelect cp, ReserveNowTask task) {
        runPipeline(cp, task);
    }

    @Override
    public void cancelReservation(ChargePointSelect cp, CancelReservationTask task) {
        runPipeline(cp, task);
    }

    @Override
    public void clearChargingProfile(ChargePointSelect cp, ClearChargingProfileTask task) {
        runPipeline(cp, task);
    }

    @Override
    public void setChargingProfile(ChargePointSelect cp, SetChargingProfileTask task) {
        runPipeline(cp, task);
    }

    @Override
    public void getCompositeSchedule(ChargePointSelect cp, GetCompositeScheduleTask task) {
        runPipeline(cp, task);
    }

    @Override
    public void triggerMessage(ChargePointSelect cp, TriggerMessageTask task) {
        runPipeline(cp, task);
    }
}
