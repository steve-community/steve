/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2019 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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
import de.rwth.idsg.steve.ocpp.ChargePointService15_Invoker;
import de.rwth.idsg.steve.ocpp.ChargePointService15_InvokerImpl;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.task.*;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.repository.dto.InsertReservationParams;
import de.rwth.idsg.steve.service.dto.EnhancedReserveNowParams;
import de.rwth.idsg.steve.web.dto.ocpp.*;
import net.parkl.ocpp.service.cs.ReservationService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 */
@Service
@Qualifier("ChargePointService15_Client")
public class ChargePointService15_Client extends ChargePointService12_Client implements IChargePointService15_Client {

    @Autowired protected OcppTagService ocppTagService;
    @Autowired protected ReservationService reservationService;

    @Autowired private ChargePointService15_InvokerImpl invoker15;

    @Override
    protected OcppVersion getVersion() {
        return OcppVersion.V_15;
    }

    @Override
    protected ChargePointService12_Invoker getOcpp12Invoker() {
        return invoker15;
    }

    protected ChargePointService15_Invoker getOcpp15Invoker() {
        return invoker15;
    }

    // -------------------------------------------------------------------------
    // Multiple Execution - since OCPP 1.5
    // -------------------------------------------------------------------------

    public int dataTransfer(DataTransferParams params) {
        DataTransferTask task = new DataTransferTask(persistentTaskService, getVersion(), params);

        Integer taskId = taskStore.add(task);

        BackgroundService.with(executorService)
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp15Invoker().dataTransfer(c, task));

        return taskId;
    }

    public int getConfiguration(GetConfigurationParams params) {
        GetConfigurationTask task = new GetConfigurationTask(persistentTaskService, getVersion(), params);

        Integer taskId = taskStore.add(task);

        BackgroundService.with(executorService)
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp15Invoker().getConfiguration(c, task));

        return taskId;
    }

    public int getLocalListVersion(MultipleChargePointSelect params) {
        GetLocalListVersionTask task = new GetLocalListVersionTask(persistentTaskService, getVersion(), params);

        Integer taskId = taskStore.add(task);

        BackgroundService.with(executorService)
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp15Invoker().getLocalListVersion(c, task));

        return taskId;
    }

    public int sendLocalList(SendLocalListParams params) {
        SendLocalListTask task = new SendLocalListTask(persistentTaskService, getVersion(), params, ocppTagService);

        Integer taskId = taskStore.add(task);

        BackgroundService.with(executorService)
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp15Invoker().sendLocalList(c, task));

        return taskId;
    }


    // -------------------------------------------------------------------------
    // Single Execution - since OCPP 1.5
    // -------------------------------------------------------------------------

    public int reserveNow(ReserveNowParams params) {
        List<ChargePointSelect> list = params.getChargePointSelectList();
        InsertReservationParams res = InsertReservationParams.builder()
                                                             .idTag(params.getIdTag())
                                                             .chargeBoxId(list.get(0).getChargeBoxId())
                                                             .connectorId(params.getConnectorId())
                                                             .startTimestamp(DateTime.now())
                                                             .expiryTimestamp(params.getExpiry().toDateTime())
                                                             .build();

        int reservationId = reservationService.insert(res);

        EnhancedReserveNowParams enhancedParams = new EnhancedReserveNowParams(params, reservationId);
        ReserveNowTask task = new ReserveNowTask(persistentTaskService, getVersion(), enhancedParams, reservationService);

        Integer taskId = taskStore.add(task);

        BackgroundService.with(executorService)
                         .forFirst(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp15Invoker().reserveNow(c, task));

        return taskId;
    }

    public int cancelReservation(CancelReservationParams params) {
        CancelReservationTask task = new CancelReservationTask(persistentTaskService,
                getVersion(), params, reservationService);

        Integer taskId = taskStore.add(task);

        BackgroundService.with(executorService)
                         .forFirst(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp15Invoker().cancelReservation(c, task));

        return taskId;
    }


}
