package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.repository.dto.Reservation;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 19.08.2014
 */
public interface ReservationRepository {
    List<Reservation> getAllReservations();
    List<Reservation> getActiveReservations();

    List<Integer> getActiveReservationIds(String chargeBoxId);

    /**
     * Returns the id of the reservation, if the reservation is inserted.
     */
    int insert(String idTag, String chargeBoxId, Timestamp startTimestamp, Timestamp expiryTimestamp);

    /**
     * Deletes the temporarily inserted reservation, when
     * 1) the charging station does not accept the reservation,
     * 2) there is a technical problem (communication failure etc.) with the charging station,
     */
    void delete(int reservationId);

    void accepted(int reservationId);
    void cancelled(int reservationId);
    void used(int reservationId, int transactionId);
}