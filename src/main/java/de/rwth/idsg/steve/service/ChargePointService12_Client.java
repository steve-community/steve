/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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

import de.rwth.idsg.steve.ocpp.ChargePointService12_Invoker;
import de.rwth.idsg.steve.ocpp.ChargePointService12_InvokerImpl;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.task.ChangeAvailabilityTask;
import de.rwth.idsg.steve.ocpp.task.ChangeConfigurationTask;
import de.rwth.idsg.steve.ocpp.task.ClearCacheTask;
import de.rwth.idsg.steve.ocpp.task.GetDiagnosticsTask;
import de.rwth.idsg.steve.ocpp.task.RemoteStartTransactionTask;
import de.rwth.idsg.steve.ocpp.task.RemoteStopTransactionTask;
import de.rwth.idsg.steve.ocpp.task.ResetTask;
import de.rwth.idsg.steve.ocpp.task.UnlockConnectorTask;
import de.rwth.idsg.steve.ocpp.task.UpdateFirmwareTask;
import de.rwth.idsg.steve.repository.TaskStore;
import de.rwth.idsg.steve.web.dto.ocpp.ChangeAvailabilityParams;
import de.rwth.idsg.steve.web.dto.ocpp.ChangeConfigurationParams;
import de.rwth.idsg.steve.web.dto.ocpp.GetDiagnosticsParams;
import de.rwth.idsg.steve.web.dto.ocpp.MultipleChargePointSelect;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStartTransactionParams;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStopTransactionParams;
import de.rwth.idsg.steve.web.dto.ocpp.ResetParams;
import de.rwth.idsg.steve.web.dto.ocpp.UnlockConnectorParams;
import de.rwth.idsg.steve.web.dto.ocpp.UpdateFirmwareParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 */
@Slf4j
@Service
@Qualifier("ChargePointService12_Client")
public class ChargePointService12_Client {

    @Autowired protected ScheduledExecutorService executorService;
    @Autowired protected TaskStore taskStore;

    @Autowired private ChargePointService12_InvokerImpl invoker12;

    protected OcppVersion getVersion() {
        return OcppVersion.V_12;
    }

    protected ChargePointService12_Invoker getOcpp12Invoker() {
        return invoker12;
    }

    // -------------------------------------------------------------------------
    // Multiple Execution - since OCPP 1.2
    // -------------------------------------------------------------------------

    public int changeAvailability(ChangeAvailabilityParams params) {
        ChangeAvailabilityTask task = new ChangeAvailabilityTask(getVersion(), params);

        BackgroundService.with(executorService)
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp12Invoker().changeAvailability(c, task));

        return taskStore.add(task);
    }

    public int changeConfiguration(ChangeConfigurationParams params) {
        ChangeConfigurationTask task = new ChangeConfigurationTask(getVersion(), params);

        BackgroundService.with(executorService)
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp12Invoker().changeConfiguration(c, task));

        return taskStore.add(task);
    }

    public int clearCache(MultipleChargePointSelect params) {
        ClearCacheTask task = new ClearCacheTask(getVersion(), params);

        BackgroundService.with(executorService)
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp12Invoker().clearCache(c, task));

        return taskStore.add(task);
    }

    public int getDiagnostics(GetDiagnosticsParams params) {
        GetDiagnosticsTask task = new GetDiagnosticsTask(getVersion(), params);

        BackgroundService.with(executorService)
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp12Invoker().getDiagnostics(c, task));

        return taskStore.add(task);
    }

    public int reset(ResetParams params) {
        ResetTask task = new ResetTask(getVersion(), params);

        BackgroundService.with(executorService)
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp12Invoker().reset(c, task));

        return taskStore.add(task);
    }

    public int updateFirmware(UpdateFirmwareParams params) {
        UpdateFirmwareTask task = new UpdateFirmwareTask(getVersion(), params);

        BackgroundService.with(executorService)
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp12Invoker().updateFirmware(c, task));

        return taskStore.add(task);
    }

    // -------------------------------------------------------------------------
    // Single Execution - since OCPP 1.2
    // -------------------------------------------------------------------------

    public int remoteStartTransaction(RemoteStartTransactionParams params) {
        RemoteStartTransactionTask task = new RemoteStartTransactionTask(getVersion(), params);

        BackgroundService.with(executorService)
                         .forFirst(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp12Invoker().remoteStartTransaction(c, task));

        return taskStore.add(task);
    }

    public int remoteStopTransaction(RemoteStopTransactionParams params) {
        RemoteStopTransactionTask task = new RemoteStopTransactionTask(getVersion(), params);

        BackgroundService.with(executorService)
                         .forFirst(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp12Invoker().remoteStopTransaction(c, task));

        return taskStore.add(task);
    }

    public int unlockConnector(UnlockConnectorParams params) {
        UnlockConnectorTask task = new UnlockConnectorTask(getVersion(), params);

        BackgroundService.with(executorService)
                         .forFirst(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp12Invoker().unlockConnector(c, task));

        return taskStore.add(task);
    }

}
