/*
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
package net.parkl.ocpp.service.cs;

import de.rwth.idsg.steve.repository.ReservationStatus;
import de.rwth.idsg.steve.repository.dto.InsertReservationParams;
import de.rwth.idsg.steve.web.dto.ReservationQueryForm;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.Connector;
import net.parkl.ocpp.entities.OcppChargeBox;
import net.parkl.ocpp.entities.OcppReservation;
import net.parkl.ocpp.entities.TransactionStart;
import net.parkl.ocpp.repositories.OcppReservationCriteriaRepository;
import net.parkl.ocpp.repositories.OcppReservationRepository;
import net.parkl.ocpp.service.config.AdvancedChargeBoxConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static de.rwth.idsg.steve.repository.ReservationStatus.USED;

@Service
@Slf4j
public class ReservationServiceImpl implements ReservationService {
    private final OcppReservationRepository reservationRepo;
    private final ConnectorService connectorService;
    private final ChargePointService chargePointService;
    private final AdvancedChargeBoxConfiguration config;
    private final OcppReservationCriteriaRepository reservationCriteriaRepository;

    @Autowired
    public ReservationServiceImpl(OcppReservationRepository reservationRepo,
                                  ConnectorService connectorService,
                                  ChargePointService chargePointService,
                                  AdvancedChargeBoxConfiguration config,
                                  OcppReservationCriteriaRepository reservationCriteriaRepository) {
        this.reservationRepo = reservationRepo;
        this.connectorService = connectorService;
        this.chargePointService = chargePointService;
        this.config = config;
        this.reservationCriteriaRepository = reservationCriteriaRepository;
    }

    @Override
    @Transactional
    public void accepted(int reservationId) {
        OcppReservation r = reservationRepo.findById(reservationId).orElseThrow(() ->
                new IllegalArgumentException("Invalid reservation id: " + reservationId));

        r.setStatus(ReservationStatus.ACCEPTED.name());
        reservationRepo.save(r);
    }

    @Override
    @Transactional
    public void delete(int reservationId) {
        reservationRepo.deleteById(reservationId);
        log.debug("The reservation '{}' is deleted.", reservationId);
    }

    @Override
    @Transactional
    public void cancelled(int reservationId) {
        OcppReservation r = reservationRepo.findById(reservationId).orElseThrow(() ->
                new IllegalArgumentException("Invalid reservation id: " + reservationId));

        r.setStatus(ReservationStatus.CANCELLED.name());
        reservationRepo.save(r);
    }

    @Override
    @Transactional
    public int insert(InsertReservationParams params) {
        Connector conn = connectorService.createConnectorIfNotExists(params.getChargeBoxId(), params.getConnectorId());

        OcppReservation r = new OcppReservation();
        r.setConnector(conn);
        r.setOcppTag(params.getIdTag());
        if (params.getStartTimestamp() != null) {
            r.setStartDatetime(params.getStartTimestamp().toDate());
        }
        if (params.getExpiryTimestamp() != null) {
            r.setExpiryDatetime(params.getExpiryTimestamp().toDate());
        }
        r.setStatus(ReservationStatus.WAITING.name());
        r = reservationRepo.save(r);
        log.debug("A new reservation '{}' is inserted.", r.getReservationPk());
        return r.getReservationPk();
    }

    @Override
    public List<Integer> getActiveReservationIds(String chargeBoxId) {
        return reservationRepo.findActiveReservationIds(chargeBoxId, new Date());
    }

    @Override
    public List<de.rwth.idsg.steve.repository.dto.Reservation> getReservations(ReservationQueryForm form) {
        Map<String, OcppChargeBox> boxMap = chargePointService.getIdChargeBoxMap();
        return reservationCriteriaRepository.getReservations(form, boxMap);
    }


    @Override
    public void markReservationAsUsed(TransactionStart transactionStart, int reservationId, String chargeBoxId) {
        if (reservationId != -1 && config.checkReservationId(chargeBoxId)) {
            OcppReservation reservation = reservationRepo.findById(reservationId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid reservation: " + reservationId));
            reservation.setStatus(USED.name());
            reservation.setTransaction(transactionStart);
            reservationRepo.save(reservation);
        }
    }
}
