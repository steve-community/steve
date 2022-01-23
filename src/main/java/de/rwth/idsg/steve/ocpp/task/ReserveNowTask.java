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
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonError;
import de.rwth.idsg.steve.repository.ReservationRepository;
import de.rwth.idsg.steve.service.dto.EnhancedReserveNowParams;

import javax.xml.ws.AsyncHandler;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 09.03.2018
 */
public class ReserveNowTask extends Ocpp15AndAboveTask<EnhancedReserveNowParams, String> {

    private final ReservationRepository reservationRepository;

    public ReserveNowTask(OcppVersion ocppVersion, EnhancedReserveNowParams params,
                          ReservationRepository reservationRepository) {
        super(ocppVersion, params);
        this.reservationRepository = reservationRepository;
    }

    @Override
    public OcppCallback<String> defaultCallback() {
        return new StringOcppCallback() {
            @Override
            public void success(String chargeBoxId, String responseStatus) {
                addNewResponse(chargeBoxId, responseStatus);

                if ("Accepted".equalsIgnoreCase(responseStatus)) {
                    reservationRepository.accepted(params.getReservationId());
                } else {
                    delete();
                }
            }

            @Override
            public void success(String chargeBoxId, OcppJsonError error) {
                addNewError(chargeBoxId, error.toString());
                delete();
            }

            @Override
            public void failed(String chargeBoxId, Exception e) {
                addNewError(chargeBoxId, e.getMessage());
                delete();
            }
        };
    }

    @Override
    public ocpp.cp._2012._06.ReserveNowRequest getOcpp15Request() {
        return new ocpp.cp._2012._06.ReserveNowRequest()
                .withConnectorId(params.getReserveNowParams().getConnectorId())
                .withExpiryDate(params.getReserveNowParams().getExpiry().toDateTime())
                .withIdTag(params.getReserveNowParams().getIdTag())
                .withReservationId(params.getReservationId())
                .withParentIdTag(params.getParentIdTag());
    }

    @Override
    public ocpp.cp._2015._10.ReserveNowRequest getOcpp16Request() {
        return new ocpp.cp._2015._10.ReserveNowRequest()
                .withConnectorId(params.getReserveNowParams().getConnectorId())
                .withExpiryDate(params.getReserveNowParams().getExpiry().toDateTime())
                .withIdTag(params.getReserveNowParams().getIdTag())
                .withReservationId(params.getReservationId())
                .withParentIdTag(params.getParentIdTag());
    }

    @Override
    public AsyncHandler<ocpp.cp._2012._06.ReserveNowResponse> getOcpp15Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    @Override
    public AsyncHandler<ocpp.cp._2015._10.ReserveNowResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    private void delete() {
        reservationRepository.delete(params.getReservationId());
    }

}
