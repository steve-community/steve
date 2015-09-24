package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.dto.Reservation;
import de.rwth.idsg.steve.utils.CustomDSL;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import de.rwth.idsg.steve.web.dto.ReservationQueryForm;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Configuration;
import org.jooq.Record7;
import org.jooq.RecordMapper;
import org.jooq.SelectQuery;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

import static de.rwth.idsg.steve.utils.DateTimeUtils.toTimestamp;
import static jooq.steve.db.tables.Reservation.RESERVATION;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 14.08.2014
 */
@Slf4j
@Repository
public class ReservationRepositoryImpl implements ReservationRepository {

    @Autowired
    @Qualifier("jooqConfig")
    private Configuration config;

    @Override
    @SuppressWarnings("unchecked")
    public List<Reservation> getReservations(ReservationQueryForm form) {
        SelectQuery selectQuery = DSL.using(config).selectQuery();
        selectQuery.addFrom(RESERVATION);
        selectQuery.addSelect(
                RESERVATION.RESERVATION_PK,
                RESERVATION.TRANSACTION_PK,
                RESERVATION.IDTAG,
                RESERVATION.CHARGEBOXID,
                RESERVATION.STARTDATETIME,
                RESERVATION.EXPIRYDATETIME,
                RESERVATION.STATUS
        );

        if (form.isChargeBoxIdSet()) {
            selectQuery.addConditions(RESERVATION.CHARGEBOXID.eq(form.getChargeBoxId()));
        }

        if (form.isUserIdSet()) {
            selectQuery.addConditions(RESERVATION.IDTAG.eq(form.getUserId()));
        }

        if (form.isStatusSet()) {
            selectQuery.addConditions(RESERVATION.STATUS.eq(form.getStatus().name()));
        }

        processType(selectQuery, form);

        // Default order
        selectQuery.addOrderBy(RESERVATION.EXPIRYDATETIME.asc());

        return selectQuery.fetch().map(new ReservationMapper());
    }

    @Override
    public List<Integer> getActiveReservationIds(String chargeBoxId) {
        return DSL.using(config)
                  .select(RESERVATION.RESERVATION_PK)
                  .from(RESERVATION)
                  .where(RESERVATION.CHARGEBOXID.equal(chargeBoxId))
                        .and(RESERVATION.EXPIRYDATETIME.greaterThan(CustomDSL.utcTimestamp()))
                        .and(RESERVATION.STATUS.equal(ReservationStatus.ACCEPTED.name()))
                  .fetch(RESERVATION.RESERVATION_PK);
    }

    @Override
    public int insert(String idTag, String chargeBoxId, Timestamp startTimestamp, Timestamp expiryTimestamp) {
        // Check overlapping
        //isOverlapping(startTimestamp, expiryTimestamp, chargeBoxId);

        int reservationId = DSL.using(config)
                               .insertInto(RESERVATION,
                                       RESERVATION.IDTAG, RESERVATION.CHARGEBOXID,
                                       RESERVATION.STARTDATETIME, RESERVATION.EXPIRYDATETIME,
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
        DSL.using(config)
           .delete(RESERVATION)
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
        DSL.using(config)
           .update(RESERVATION)
           .set(RESERVATION.STATUS, ReservationStatus.USED.name())
           .set(RESERVATION.TRANSACTION_PK, transactionId)
           .where(RESERVATION.RESERVATION_PK.equal(reservationId))
           .execute();
    }

    private void internalUpdateReservation(int reservationId, ReservationStatus status) {
        try {
            DSL.using(config)
               .update(RESERVATION)
               .set(RESERVATION.STATUS, status.name())
               .where(RESERVATION.RESERVATION_PK.equal(reservationId))
               .execute();
        } catch (DataAccessException e) {
            log.error("Updating of reservationId '{}' to status '{}' FAILED.", reservationId, status, e);
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private class ReservationMapper implements
            RecordMapper<Record7<Integer, Integer, String, String, Timestamp, Timestamp, String>, Reservation> {
        @Override
        public Reservation map(Record7<Integer, Integer, String, String, Timestamp, Timestamp, String> r) {
            return Reservation.builder()
                              .id(r.value1())
                              .transactionId(r.value2())
                              .idTag(r.value3())
                              .chargeBoxId(r.value4())
                              .startDatetime(DateTimeUtils.humanize(r.value5()))
                              .expiryDatetime(DateTimeUtils.humanize(r.value6()))
                              .status(r.value7())
                              .build();
        }
    }

    private void processType(SelectQuery selectQuery, ReservationQueryForm form) {
        switch (form.getPeriodType()) {
            case ACTIVE:
                selectQuery.addConditions(RESERVATION.EXPIRYDATETIME.greaterThan(CustomDSL.utcTimestamp()));
                break;

            case FROM_TO:
                selectQuery.addConditions(
                        RESERVATION.STARTDATETIME.greaterOrEqual(toTimestamp(form.getFrom())),
                        RESERVATION.EXPIRYDATETIME.lessOrEqual(toTimestamp(form.getTo()))
                );
                break;
        }
    }

    /**
     * Throws exception, if there are rows whose date/time ranges overlap with the input
     */
    private void isOverlapping(Timestamp start, Timestamp stop, String chargeBoxId) {
        try {
            int count = DSL.using(config)
                           .selectOne()
                           .from(RESERVATION)
                           .where(RESERVATION.EXPIRYDATETIME.greaterOrEqual(start))
                             .and(RESERVATION.STARTDATETIME.lessOrEqual(stop))
                             .and(RESERVATION.CHARGEBOXID.equal(chargeBoxId))
                           .execute();

            if (count != 1) {
                throw new SteveException("The desired reservation overlaps with another reservation");
            }

        } catch (DataAccessException e) {
            log.error("Exception occurred", e);
        }
    }
}