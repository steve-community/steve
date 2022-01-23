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
package de.rwth.idsg.steve.ocpp.task;

import de.rwth.idsg.steve.ocpp.Ocpp15AndAboveTask;
import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.repository.ReservationRepository;
import de.rwth.idsg.steve.web.dto.ocpp.CancelReservationParams;
import ocpp.cp._2012._06.CancelReservationRequest;
import ocpp.cp._2012._06.CancelReservationResponse;

import javax.xml.ws.AsyncHandler;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 09.03.2018
 */
public class CancelReservationTask extends Ocpp15AndAboveTask<CancelReservationParams, String> {

    private final ReservationRepository reservationRepository;

    public CancelReservationTask(OcppVersion ocppVersion, CancelReservationParams params,
                                 ReservationRepository reservationRepository) {
        super(ocppVersion, params);
        this.reservationRepository = reservationRepository;
    }

    @Override
    public OcppCallback<String> defaultCallback() {
        return new DefaultOcppCallback<String>() {
            @Override
            public void success(String chargeBoxId, String statusValue) {
                addNewResponse(chargeBoxId, statusValue);

                if ("Accepted".equalsIgnoreCase(statusValue)) {
                    reservationRepository.cancelled(params.getReservationId());
                }
            }
        };
    }

    @Override
    public CancelReservationRequest getOcpp15Request() {
        return new CancelReservationRequest()
                .withReservationId(params.getReservationId());
    }

    @Override
    public ocpp.cp._2015._10.CancelReservationRequest getOcpp16Request() {
        return new ocpp.cp._2015._10.CancelReservationRequest()
                .withReservationId(params.getReservationId());
    }

    @Override
    public AsyncHandler<CancelReservationResponse> getOcpp15Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    @Override
    public AsyncHandler<ocpp.cp._2015._10.CancelReservationResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }
}
