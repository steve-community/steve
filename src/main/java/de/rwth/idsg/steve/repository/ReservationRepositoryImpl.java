package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.dto.Reservation;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import de.rwth.idsg.steve.web.ExceptionMessage;
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
     * SELECT reservation_pk, idTag, chargeBoxId, startDatetime, expiryDatetime
     * FROM reservation
     * WHERE expiryDatetime >= NOW()
     * ORDER BY expiryDatetime
     */
    @Override
    public List<Reservation> getReservations() {
        return DSL.using(config)
                  .selectFrom(RESERVATION)
                  .where(RESERVATION.EXPIRYDATETIME.greaterOrEqual(DSL.currentTimestamp()))
                  .orderBy(RESERVATION.EXPIRYDATETIME)
                  .fetch()
                  .map(new ReservationMapper());
    }

    /**
     * SELECT reservation_pk
     * FROM reservation
     * WHERE chargeBoxId = ? AND expiryDatetime >= NOW()
     */
    @Override
    public List<Integer> getExistingReservationIds(String chargeBoxId) {
        return DSL.using(config)
                  .select()
                  .from(RESERVATION)
                  .where(RESERVATION.CHARGEBOXID.equal(chargeBoxId))
                    .and(RESERVATION.EXPIRYDATETIME.greaterOrEqual(DSL.currentTimestamp()))
                  .fetch(RESERVATION.RESERVATION_PK);
    }

    /**
     * INSERT INTO reservation (idTag, chargeBoxId, startDatetime, expiryDatetime) VALUES (?,?,?,?)
     */
    @Override
    public int bookReservation(String idTag, String chargeBoxId,
                               Timestamp startTimestamp, Timestamp expiryTimestamp) {
        // Check overlapping
        //isOverlapping(startTimestamp, expiryTimestamp, chargeBoxId);

        int reservationId = DSL.using(config)
                               .insertInto(RESERVATION,
                                       RESERVATION.IDTAG, RESERVATION.CHARGEBOXID,
                                       RESERVATION.STARTDATETIME, RESERVATION.EXPIRYDATETIME)
                               .values(idTag, chargeBoxId, startTimestamp, expiryTimestamp)
                               .returning(RESERVATION.RESERVATION_PK)
                               .execute();

        log.info("A new reservation '{}' is booked.", reservationId);
        return reservationId;
    }

    /**
     * DELETE FROM reservation
     * WHERE reservation_pk = ?
     */
    @Override
    public void cancelReservation(int reservationId) throws SteveException {
        try {
            DSL.using(config)
               .delete(RESERVATION)
               .where(RESERVATION.RESERVATION_PK.equal(reservationId))
               .execute();
        } catch (DataAccessException e) {
            throw new SteveException("Deletion of reservationId '" + reservationId + "' FAILED.", e);
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
                    .idTag(r.getIdtag())
                    .chargeBoxId(r.getChargeboxid())
                    .startDatetime(DateTimeUtils.humanize(r.getStartdatetime()))
                    .expiryDatetime(DateTimeUtils.humanize(r.getExpirydatetime()))
                    .build();
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
                throw new SteveException(ExceptionMessage.OVERLAPPING_RESERVATION);
            }

        } catch (DataAccessException e) {
            log.error("Exception occurred", e);
        }
    }
}