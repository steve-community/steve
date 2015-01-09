package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.dto.Reservation;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import jooq.steve.db.tables.records.ReservationRecord;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Configuration;
import org.jooq.RecordMapper;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

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

    /**
     * SELECT *
     * FROM reservation
     * ORDER BY expiryDatetime
     */
    @Override
    public List<Reservation> getAllReservations() {
        return DSL.using(config)
                .selectFrom(RESERVATION)
                .orderBy(RESERVATION.EXPIRYDATETIME)
                .fetch()
                .map(new ReservationMapper());
    }

    /**
     * SELECT *
     * FROM reservation
     * WHERE expiryDatetime > CURRENT_TIMESTAMP AND status = ?
     * ORDER BY expiryDatetime
     */
    @Override
    public List<Reservation> getActiveReservations() {
        return DSL.using(config)
                  .selectFrom(RESERVATION)
                  .where(RESERVATION.EXPIRYDATETIME.greaterThan(DSL.currentTimestamp()))
                        .and(RESERVATION.STATUS.equal(Status.ACCEPTED.name()))
                  .orderBy(RESERVATION.EXPIRYDATETIME)
                  .fetch()
                  .map(new ReservationMapper());
    }

    /**
     * SELECT reservation_pk
     * FROM reservation
     * WHERE chargeBoxId = ?
     * AND expiryDatetime > CURRENT_TIMESTAMP AND status = ?
     */
    @Override
    public List<Integer> getActiveReservationIds(String chargeBoxId) {
        return DSL.using(config)
                  .select(RESERVATION.RESERVATION_PK)
                  .from(RESERVATION)
                  .where(RESERVATION.CHARGEBOXID.equal(chargeBoxId))
                        .and(RESERVATION.EXPIRYDATETIME.greaterThan(DSL.currentTimestamp()))
                        .and(RESERVATION.STATUS.equal(Status.ACCEPTED.name()))
                  .fetch(RESERVATION.RESERVATION_PK);
    }

    /**
     * INSERT INTO reservation (idTag, chargeBoxId, startDatetime, expiryDatetime, status) VALUES (?,?,?,?,?)
     */
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
                                       Status.WAITING.name())
                               .returning(RESERVATION.RESERVATION_PK)
                               .fetchOne()
                               .getReservationPk();

        log.debug("A new reservation '{}' is inserted.", reservationId);
        return reservationId;
    }

    /**
     * DELETE FROM reservation
     * WHERE reservation_pk = ?
     */
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
        internalUpdateReservation(reservationId, Status.ACCEPTED);
    }

    @Override
    public void cancelled(int reservationId) {
        internalUpdateReservation(reservationId, Status.CANCELLED);
    }

    /**
     * UPDATE reservation
     * SET status = ?,
     * SET transaction_pk = ?
     * WHERE reservation_pk = ?
     */
    @Override
    public void used(int reservationId, int transactionId) {
        DSL.using(config)
           .update(RESERVATION)
           .set(RESERVATION.STATUS, Status.USED.name())
           .set(RESERVATION.TRANSACTION_PK, transactionId)
           .where(RESERVATION.RESERVATION_PK.equal(reservationId))
           .execute();
    }

    /**
     * UPDATE reservation
     * SET status = ?
     * WHERE reservation_pk = ?
     */
    private void internalUpdateReservation(int reservationId, Status status) {
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

    private class ReservationMapper implements RecordMapper<ReservationRecord, Reservation> {
        @Override
        public Reservation map(ReservationRecord r) {
            return Reservation.builder()
                    .id(r.getReservationPk())
                    .transactionId(r.getTransactionPk())
                    .idTag(r.getIdtag())
                    .chargeBoxId(r.getChargeboxid())
                    .startDatetime(DateTimeUtils.humanize(r.getStartdatetime()))
                    .expiryDatetime(DateTimeUtils.humanize(r.getExpirydatetime()))
                    .status(r.getStatus())
                    .build();
        }
    }

    /**
     * To be used in the DB table "reservation" for the column "status".
     */
    private enum Status {

        WAITING,    // Waiting for charge point to respond to a reservation request
        ACCEPTED,   // Charge point accepted. The only status for active, usable reservations (if expiryDatetime is in future)
        USED,       // Reservation used by the user for a transaction
        CANCELLED;  // Reservation cancelled by the user

        @Override
        public String toString() {
            return this.name();
        }
    }

    /**
     * Throws exception, if there are rows whose date/time ranges overlap with the input
     *
     * This WHERE clause covers all three cases:
     *
     * SELECT 1
     * FROM reservation
     * WHERE ? <= expiryDatetime AND ? >= startDatetime AND chargeBoxId = ?
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