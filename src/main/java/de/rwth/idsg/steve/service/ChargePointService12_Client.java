/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2024 SteVe Community Team
 * All Rights Reserved.
 *
 * Parkl Digital Technologies
 * Copyright (C) 2020-2021
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
import de.rwth.idsg.steve.ocpp.task.*;
import de.rwth.idsg.steve.repository.TaskStore;
import de.rwth.idsg.steve.web.dto.ocpp.*;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.service.cluster.PersistentTaskService;
import net.parkl.ocpp.service.cluster.PersistentTaskServiceImpl;
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
public class ChargePointService12_Client implements IChargePointService12_Client {

    @Autowired protected ScheduledExecutorService executorService;
    @Autowired protected TaskStore taskStore;
    @Autowired protected PersistentTaskService persistentTaskService;

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
        ChangeAvailabilityTask task = new ChangeAvailabilityTask(persistentTaskService, getVersion(), params);

        Integer taskId = taskStore.add(task);

        BackgroundService.with(executorService)
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp12Invoker().changeAvailability(c, task));

        return taskId;
    }

    public int changeConfiguration(ChangeConfigurationParams params) {
        ChangeConfigurationTask task = new ChangeConfigurationTask(persistentTaskService, getVersion(), params);

        Integer taskId = taskStore.add(task);

        BackgroundService.with(executorService)
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp12Invoker().changeConfiguration(c, task));

        return taskId;
    }

    public int clearCache(MultipleChargePointSelect params) {
        ClearCacheTask task = new ClearCacheTask(persistentTaskService, getVersion(), params);

        Integer taskId = taskStore.add(task);

        BackgroundService.with(executorService)
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp12Invoker().clearCache(c, task));

        return taskId;
    }

    public int getDiagnostics(GetDiagnosticsParams params) {
        GetDiagnosticsTask task = new GetDiagnosticsTask(persistentTaskService, getVersion(), params);

        Integer taskId = taskStore.add(task);

        BackgroundService.with(executorService)
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp12Invoker().getDiagnostics(c, task));

        return taskId;
    }

    public int reset(ResetParams params) {
        ResetTask task = new ResetTask(persistentTaskService, getVersion(), params);

        Integer taskId = taskStore.add(task);

        BackgroundService.with(executorService)
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp12Invoker().reset(c, task));

        return taskId;
    }

    public int updateFirmware(UpdateFirmwareParams params) {
        UpdateFirmwareTask task = new UpdateFirmwareTask(persistentTaskService, getVersion(), params);

        Integer taskId = taskStore.add(task);

        BackgroundService.with(executorService)
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp12Invoker().updateFirmware(c, task));

        return taskId;
    }

    // -------------------------------------------------------------------------
    // Single Execution - since OCPP 1.2
    // -------------------------------------------------------------------------

    public int remoteStartTransaction(RemoteStartTransactionParams params) {
        RemoteStartTransactionTask task = new RemoteStartTransactionTask(persistentTaskService, getVersion(), params);

        Integer taskId = taskStore.add(task);

        BackgroundService.with(executorService)
                         .forFirst(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp12Invoker().remoteStartTransaction(c, task));

        return taskId;
    }

    public int remoteStopTransaction(RemoteStopTransactionParams params) {
        RemoteStopTransactionTask task = new RemoteStopTransactionTask(persistentTaskService, getVersion(), params);

        Integer taskId = taskStore.add(task);

        BackgroundService.with(executorService)
                         .forFirst(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp12Invoker().remoteStopTransaction(c, task));

        return taskId;
    }

    public int unlockConnector(UnlockConnectorParams params) {
        UnlockConnectorTask task = new UnlockConnectorTask(persistentTaskService, getVersion(), params);

        Integer taskId = taskStore.add(task);

        BackgroundService.with(executorService)
                         .forFirst(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp12Invoker().unlockConnector(c, task));

        return taskId;
    }

}
