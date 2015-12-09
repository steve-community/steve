package de.rwth.idsg.steve.repository.impl;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.ReservationRepository;
import de.rwth.idsg.steve.repository.ReservationStatus;
import de.rwth.idsg.steve.repository.dto.Reservation;
import de.rwth.idsg.steve.utils.CustomDSL;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import de.rwth.idsg.steve.web.dto.ReservationQueryForm;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.jooq.Record9;
import org.jooq.RecordMapper;
import org.jooq.SelectQuery;
import org.jooq.exception.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static jooq.steve.db.tables.ChargeBox.CHARGE_BOX;
import static jooq.steve.db.tables.OcppTag.OCPP_TAG;
import static jooq.steve.db.tables.Reservation.RESERVATION;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 14.08.2014
 */
@Slf4j
@Repository
public class ReservationRepositoryImpl implements ReservationRepository {

    @Autowired private DSLContext ctx;

    @Override
    @SuppressWarnings("unchecked")
    public List<Reservation> getReservations(ReservationQueryForm form) {
        SelectQuery selectQuery = ctx.selectQuery();
        selectQuery.addFrom(RESERVATION);
        selectQuery.addJoin(OCPP_TAG, OCPP_TAG.ID_TAG.eq(RESERVATION.ID_TAG));
        selectQuery.addJoin(CHARGE_BOX, CHARGE_BOX.CHARGE_BOX_ID.eq(RESERVATION.CHARGE_BOX_ID));
        selectQuery.addSelect(
                RESERVATION.RESERVATION_PK,
                RESERVATION.TRANSACTION_PK,
                OCPP_TAG.OCPP_TAG_PK,
                CHARGE_BOX.CHARGE_BOX_PK,
                OCPP_TAG.ID_TAG,
                CHARGE_BOX.CHARGE_BOX_ID,
                RESERVATION.START_DATETIME,
                RESERVATION.EXPIRY_DATETIME,
                RESERVATION.STATUS
        );

        if (form.isChargeBoxIdSet()) {
            selectQuery.addConditions(RESERVATION.CHARGE_BOX_ID.eq(form.getChargeBoxId()));
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
                  .where(RESERVATION.CHARGE_BOX_ID.equal(chargeBoxId))
                        .and(RESERVATION.EXPIRY_DATETIME.greaterThan(CustomDSL.utcTimestamp()))
                        .and(RESERVATION.STATUS.equal(ReservationStatus.ACCEPTED.name()))
                  .fetch(RESERVATION.RESERVATION_PK);
    }

    @Override
    public int insert(String idTag, String chargeBoxId, DateTime startTimestamp, DateTime expiryTimestamp) {
        // Check overlapping
        //isOverlapping(startTimestamp, expiryTimestamp, chargeBoxId);

        int reservationId = ctx.insertInto(RESERVATION,
                                       RESERVATION.ID_TAG, RESERVATION.CHARGE_BOX_ID,
                                       RESERVATION.START_DATETIME, RESERVATION.EXPIRY_DATETIME,
                                       RESERVATION.STATUS)
                               .values(idTag, chargeBoxId,
                                       startTimestamp, expiryTimestamp,
                                       ReservationStatus.WAITING.name())
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
    public void used(int reservationId, int transactionId) {
        ctx.update(RESERVATION)
           .set(RESERVATION.STATUS, ReservationStatus.USED.name())
           .set(RESERVATION.TRANSACTION_PK, transactionId)
           .where(RESERVATION.RESERVATION_PK.equal(reservationId))
           .execute();
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private static class ReservationMapper implements
            RecordMapper<Record9<Integer, Integer, Integer, Integer, String, String, DateTime, DateTime, String>, Reservation> {
        @Override
        public Reservation map(Record9<Integer, Integer, Integer, Integer, String, String, DateTime, DateTime, String> r) {
            return Reservation.builder()
                              .id(r.value1())
                              .transactionId(r.value2())
                              .ocppTagPk(r.value3())
                              .chargeBoxPk(r.value4())
                              .ocppIdTag(r.value5())
                              .chargeBoxId(r.value6())
                              .startDatetime(DateTimeUtils.humanize(r.value7()))
                              .expiryDatetime(DateTimeUtils.humanize(r.value8()))
                              .status(r.value9())
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
                selectQuery.addConditions(RESERVATION.EXPIRY_DATETIME.greaterThan(CustomDSL.utcTimestamp()));
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
    private void isOverlapping(DateTime start, DateTime stop, String chargeBoxId) {
        try {
            int count = ctx.selectOne()
                           .from(RESERVATION)
                           .where(RESERVATION.EXPIRY_DATETIME.greaterOrEqual(start))
                             .and(RESERVATION.START_DATETIME.lessOrEqual(stop))
                             .and(RESERVATION.CHARGE_BOX_ID.equal(chargeBoxId))
                           .execute();

            if (count != 1) {
                throw new SteveException("The desired reservation overlaps with another reservation");
            }

        } catch (DataAccessException e) {
            log.error("Exception occurred", e);
        }
    }
}
