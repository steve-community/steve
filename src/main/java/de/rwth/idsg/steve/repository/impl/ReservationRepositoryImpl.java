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
package de.rwth.idsg.steve.repository.impl;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.ReservationRepository;
import de.rwth.idsg.steve.repository.ReservationStatus;
import de.rwth.idsg.steve.repository.dto.InsertReservationParams;
import de.rwth.idsg.steve.repository.dto.Reservation;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import de.rwth.idsg.steve.web.dto.ReservationQueryForm;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record10;
import org.jooq.RecordMapper;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.SelectQuery;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static jooq.steve.db.tables.ChargeBox.CHARGE_BOX;
import static jooq.steve.db.tables.Connector.CONNECTOR;
import static jooq.steve.db.tables.OcppTag.OCPP_TAG;
import static jooq.steve.db.tables.Reservation.RESERVATION;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 14.08.2014
 */
@Slf4j
@Repository
public class ReservationRepositoryImpl implements ReservationRepository {

    private final DSLContext ctx;

    @Autowired
    public ReservationRepositoryImpl(DSLContext ctx) {
        this.ctx = ctx;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Reservation> getReservations(ReservationQueryForm form) {
        SelectQuery selectQuery = ctx.selectQuery();
        selectQuery.addFrom(RESERVATION);
        selectQuery.addJoin(OCPP_TAG, OCPP_TAG.ID_TAG.eq(RESERVATION.ID_TAG));
        selectQuery.addJoin(CONNECTOR, CONNECTOR.CONNECTOR_PK.eq(RESERVATION.CONNECTOR_PK));
        selectQuery.addJoin(CHARGE_BOX, CONNECTOR.CHARGE_BOX_ID.eq(CHARGE_BOX.CHARGE_BOX_ID));

        selectQuery.addSelect(
                RESERVATION.RESERVATION_PK,
                RESERVATION.TRANSACTION_PK,
                OCPP_TAG.OCPP_TAG_PK,
                CHARGE_BOX.CHARGE_BOX_PK,
                OCPP_TAG.ID_TAG,
                CHARGE_BOX.CHARGE_BOX_ID,
                RESERVATION.START_DATETIME,
                RESERVATION.EXPIRY_DATETIME,
                RESERVATION.STATUS,
                CONNECTOR.CONNECTOR_ID
        );

        if (form.isChargeBoxIdSet()) {
            selectQuery.addConditions(CHARGE_BOX.CHARGE_BOX_ID.eq(form.getChargeBoxId()));
        }

        if (form.isOcppIdTagSet()) {
            selectQuery.addConditions(RESERVATION.ID_TAG.eq(form.getOcppIdTag()));
        }

        if (form.isStatusSet()) {
            selectQuery.addConditions(RESERVATION.STATUS.eq(form.getStatus().name()));
        }

        processType(selectQuery, form);

        // Default order
        selectQuery.addOrderBy(RESERVATION.EXPIRY_DATETIME.asc());

        return selectQuery.fetch().map(new ReservationMapper());
    }

    @Override
    public List<Integer> getActiveReservationIds(String chargeBoxId) {
        return ctx.select(RESERVATION.RESERVATION_PK)
                  .from(RESERVATION)
                  .where(RESERVATION.CONNECTOR_PK.in(DSL.select(CONNECTOR.CONNECTOR_PK)
                                                        .from(CONNECTOR)
                                                        .where(CONNECTOR.CHARGE_BOX_ID.equal(chargeBoxId))))
                  .and(RESERVATION.EXPIRY_DATETIME.greaterThan(DateTime.now()))
                  .and(RESERVATION.STATUS.equal(ReservationStatus.ACCEPTED.name()))
                  .fetch(RESERVATION.RESERVATION_PK);
    }

    @Override
    public int insert(InsertReservationParams params) {
        // Check overlapping
        //isOverlapping(startTimestamp, expiryTimestamp, chargeBoxId);

        SelectConditionStep<Record1<Integer>> connectorPkQuery =
                DSL.select(CONNECTOR.CONNECTOR_PK)
                   .from(CONNECTOR)
                   .where(CONNECTOR.CHARGE_BOX_ID.equal(params.getChargeBoxId()))
                   .and(CONNECTOR.CONNECTOR_ID.equal(params.getConnectorId()));

        int reservationId = ctx.insertInto(RESERVATION)
                               .set(RESERVATION.CONNECTOR_PK, connectorPkQuery)
                               .set(RESERVATION.ID_TAG, params.getIdTag())
                               .set(RESERVATION.START_DATETIME, params.getStartTimestamp())
                               .set(RESERVATION.EXPIRY_DATETIME, params.getExpiryTimestamp())
                               .set(RESERVATION.STATUS, ReservationStatus.WAITING.name())
                               .returning(RESERVATION.RESERVATION_PK)
                               .fetchOne()
                               .getReservationPk();

        log.debug("A new reservation '{}' is inserted.", reservationId);
        return reservationId;
    }

    @Override
    public void delete(int reservationId) {
        ctx.delete(RESERVATION)
           .where(RESERVATION.RESERVATION_PK.equal(reservationId))
           .execute();

        log.debug("The reservation '{}' is deleted.", reservationId);
    }

    @Override
    public void accepted(int reservationId) {
        internalUpdateReservation(reservationId, ReservationStatus.ACCEPTED);
    }

    @Override
    public void cancelled(int reservationId) {
        internalUpdateReservation(reservationId, ReservationStatus.CANCELLED);
    }

    @Override
    public void used(Select<Record1<Integer>> connectorPkSelect, String ocppIdTag, int reservationId, int transactionId) {
        int count = ctx.update(RESERVATION)
                       .set(RESERVATION.STATUS, ReservationStatus.USED.name())
                       .set(RESERVATION.TRANSACTION_PK, transactionId)
                       .where(RESERVATION.RESERVATION_PK.equal(reservationId))
                       .and(RESERVATION.ID_TAG.equal(ocppIdTag))
                       .and(RESERVATION.CONNECTOR_PK.equal(connectorPkSelect))
                       .and(RESERVATION.STATUS.eq(ReservationStatus.ACCEPTED.name()))
                       .execute();

        if (count != 1) {
            log.warn("Could not mark the reservation '{}' as used: Problems occurred due to sent reservation id, " +
                    "charge box connector, user id tag or the reservation was used already.", reservationId);
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private static class ReservationMapper implements
            RecordMapper<Record10<Integer, Integer, Integer, Integer, String,
                                  String, DateTime, DateTime, String, Integer>, Reservation> {
        @Override
        public Reservation map(Record10<Integer, Integer, Integer, Integer, String,
                                        String, DateTime, DateTime, String, Integer> r) {
            return Reservation.builder()
                              .id(r.value1())
                              .transactionId(r.value2())
                              .ocppTagPk(r.value3())
                              .chargeBoxPk(r.value4())
                              .ocppIdTag(r.value5())
                              .chargeBoxId(r.value6())
                              .startDatetimeDT(r.value7())
                              .startDatetime(DateTimeUtils.humanize(r.value7()))
                              .expiryDatetimeDT(r.value8())
                              .expiryDatetime(DateTimeUtils.humanize(r.value8()))
                              .status(r.value9())
                              .connectorId(r.value10())
                              .build();
        }
    }

    private void internalUpdateReservation(int reservationId, ReservationStatus status) {
        try {
            ctx.update(RESERVATION)
               .set(RESERVATION.STATUS, status.name())
               .where(RESERVATION.RESERVATION_PK.equal(reservationId))
               .execute();
        } catch (DataAccessException e) {
            log.error("Updating of reservationId '{}' to status '{}' FAILED.", reservationId, status, e);
        }
    }

    private void processType(SelectQuery selectQuery, ReservationQueryForm form) {
        switch (form.getPeriodType()) {
            case ACTIVE:
                selectQuery.addConditions(RESERVATION.EXPIRY_DATETIME.greaterThan(DateTime.now()));
                break;

            case FROM_TO:
                selectQuery.addConditions(
                        RESERVATION.START_DATETIME.greaterOrEqual(form.getFrom().toDateTime()),
                        RESERVATION.EXPIRY_DATETIME.lessOrEqual(form.getTo().toDateTime())
                );
                break;

            default:
                throw new SteveException("Unknown enum type");
        }
    }

    /**
     * Throws exception, if there are rows whose date/time ranges overlap with the input
     */
//    private void isOverlapping(DateTime start, DateTime stop, String chargeBoxId) {
//        try {
//            int count = ctx.selectOne()
//                           .from(RESERVATION)
//                           .where(RESERVATION.EXPIRY_DATETIME.greaterOrEqual(start))
//                             .and(RESERVATION.START_DATETIME.lessOrEqual(stop))
//                             .and(RESERVATION.CHARGE_BOX_ID.equal(chargeBoxId))
//                           .execute();
//
//            if (count != 1) {
//                throw new SteveException("The desired reservation overlaps with another reservation");
//            }
//
//        } catch (DataAccessException e) {
//            log.error("Exception occurred", e);
//        }
//    }
}
