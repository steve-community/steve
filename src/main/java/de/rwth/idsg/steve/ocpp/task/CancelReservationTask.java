/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
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
import de.rwth.idsg.steve.repository.ReservationRepository;
import de.rwth.idsg.steve.web.dto.ocpp.CancelReservationParams;
import ocpp.cp._2012._06.CancelReservationRequest;
import ocpp.cp._2012._06.CancelReservationResponse;
import ocpp.cp._2015._10.CancelReservationStatus;

import jakarta.xml.ws.AsyncHandler;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 09.03.2018
 */
public class CancelReservationTask extends Ocpp15AndAboveTask<CancelReservationParams, CancelReservationStatus> {

    private final ReservationRepository reservationRepository;

    public CancelReservationTask(CancelReservationParams params,
                                 ReservationRepository reservationRepository) {
        super(params);
        this.reservationRepository = reservationRepository;
    }

    @Override
    public OcppCallback<CancelReservationStatus> defaultCallback() {
        return new DefaultOcppCallback<CancelReservationStatus>() {
            @Override
            public void success(String chargeBoxId, CancelReservationStatus status) {
                addNewResponse(chargeBoxId, status.value());

                if (CancelReservationStatus.ACCEPTED == status) {
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
                success(chargeBoxId, CancelReservationStatus.fromValue(res.get().getStatus().value()));
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    @Override
    public AsyncHandler<ocpp.cp._2015._10.CancelReservationResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }
}
