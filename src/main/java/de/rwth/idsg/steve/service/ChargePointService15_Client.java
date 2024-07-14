/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2024 SteVe Community Team
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
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.task.CancelReservationTask;
import de.rwth.idsg.steve.ocpp.task.DataTransferTask;
import de.rwth.idsg.steve.ocpp.task.GetConfigurationTask;
import de.rwth.idsg.steve.ocpp.task.GetLocalListVersionTask;
import de.rwth.idsg.steve.ocpp.task.ReserveNowTask;
import de.rwth.idsg.steve.ocpp.task.SendLocalListTask;
import de.rwth.idsg.steve.repository.ReservationRepository;
import de.rwth.idsg.steve.repository.TaskStore;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.repository.dto.InsertReservationParams;
import de.rwth.idsg.steve.service.dto.EnhancedReserveNowParams;
import de.rwth.idsg.steve.web.dto.ocpp.CancelReservationParams;
import de.rwth.idsg.steve.web.dto.ocpp.DataTransferParams;
import de.rwth.idsg.steve.web.dto.ocpp.GetConfigurationParams;
import de.rwth.idsg.steve.web.dto.ocpp.MultipleChargePointSelect;
import de.rwth.idsg.steve.web.dto.ocpp.ReserveNowParams;
import de.rwth.idsg.steve.web.dto.ocpp.SendLocalListParams;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 */
@Slf4j
@Service
@Qualifier("ChargePointService15_Client")
public class ChargePointService15_Client extends ChargePointService12_Client {

    private final OcppTagService ocppTagService;
    private final ReservationRepository reservationRepository;

    private final ChargePointService15_Invoker invoker15;

    public ChargePointService15_Client(
            ScheduledExecutorService executorService,
            TaskStore taskStore,
            @Qualifier("ChargePointService15_Invoker") ChargePointService15_Invoker invoker15,
            OcppTagService ocppTagService,
            ReservationRepository reservationRepository
    ) {
        super(executorService, taskStore, invoker15);
        this.ocppTagService = ocppTagService;
        this.reservationRepository = reservationRepository;
        this.invoker15 = invoker15;
    }

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
        DataTransferTask task = new DataTransferTask(getVersion(), params);

        BackgroundService.with(getExecutorService())
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp15Invoker().dataTransfer(c, task));

        return getTaskStore().add(task);
    }

    public int getConfiguration(GetConfigurationParams params) {
        GetConfigurationTask task = new GetConfigurationTask(getVersion(), params);

        BackgroundService.with(getExecutorService())
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp15Invoker().getConfiguration(c, task));

        return getTaskStore().add(task);
    }

    public int getLocalListVersion(MultipleChargePointSelect params) {
        GetLocalListVersionTask task = new GetLocalListVersionTask(getVersion(), params);

        BackgroundService.with(getExecutorService())
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp15Invoker().getLocalListVersion(c, task));

        return getTaskStore().add(task);
    }

    public int sendLocalList(SendLocalListParams params) {
        SendLocalListTask task = new SendLocalListTask(getVersion(), params, ocppTagService);

        BackgroundService.with(getExecutorService())
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp15Invoker().sendLocalList(c, task));

        return getTaskStore().add(task);
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

        int reservationId = reservationRepository.insert(res);
        String parentIdTag = ocppTagService.getParentIdtag(params.getIdTag());

        EnhancedReserveNowParams enhancedParams = new EnhancedReserveNowParams(params, reservationId, parentIdTag);
        ReserveNowTask task = new ReserveNowTask(getVersion(), enhancedParams, reservationRepository);

        BackgroundService.with(getExecutorService())
                         .forFirst(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp15Invoker().reserveNow(c, task));

        return getTaskStore().add(task);
    }

    public int cancelReservation(CancelReservationParams params) {
        CancelReservationTask task = new CancelReservationTask(getVersion(), params, reservationRepository);

        BackgroundService.with(getExecutorService())
                         .forFirst(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp15Invoker().cancelReservation(c, task));

        return getTaskStore().add(task);
    }
}
