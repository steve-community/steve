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
package de.rwth.idsg.steve.ocpp.task;

import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.task.impl.OcppVersionHandler;
import de.rwth.idsg.steve.ocpp.task.impl.TaskDefinition;
import de.rwth.idsg.steve.repository.ReservationRepository;
import de.rwth.idsg.steve.web.dto.ocpp.CancelReservationParams;

public class CancelReservationTask extends CommunicationTask<CancelReservationParams, String> {

    private static final TaskDefinition<CancelReservationParams, String> TASK_DEFINITION =
            TaskDefinition.<CancelReservationParams, String>builder()
                    .versionHandler(
                            OcppVersion.V_15,
                            new OcppVersionHandler<>(
                                    task -> new ocpp.cp._2012._06.CancelReservationRequest()
                                            .withReservationId(task.getParams().getReservationId()),
                                    (ocpp.cp._2012._06.CancelReservationResponse r) ->
                                            r.getStatus().value()))
                    .versionHandler(
                            OcppVersion.V_16,
                            new OcppVersionHandler<>(
                                    task -> new ocpp.cp._2015._10.CancelReservationRequest()
                                            .withReservationId(task.getParams().getReservationId()),
                                    (ocpp.cp._2015._10.CancelReservationResponse r) ->
                                            r.getStatus().value()))
                    .build();

    private final ReservationRepository reservationRepository;

    public CancelReservationTask(
            CancelReservationParams params, ReservationRepository reservationRepository, String caller) {
        super(TASK_DEFINITION, params, caller);
        this.reservationRepository = reservationRepository;
    }

    public CancelReservationTask(CancelReservationParams params, ReservationRepository reservationRepository) {
        this(params, reservationRepository, "SteVe");
    }

    @Override
    protected OcppCallback<String> createDefaultCallback() {
        return new OcppCallback<>() {
            @Override
            public void success(String chargeBoxId, String statusValue) {
                addNewResponse(chargeBoxId, statusValue);

                if ("Accepted".equalsIgnoreCase(statusValue)) {
                    reservationRepository.cancelled(params.getReservationId());
                }
            }

            @Override
            public void successError(String chargeBoxId, Object error) {
                addNewError(chargeBoxId, error.toString());
            }

            @Override
            public void failed(String chargeBoxId, Exception e) {
                addNewError(chargeBoxId, e.getMessage());
            }
        };
    }
}
