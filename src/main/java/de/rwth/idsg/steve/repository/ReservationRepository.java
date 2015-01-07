package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.repository.dto.Reservation;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 19.08.2014
 */
public interface ReservationRepository {
    List<Reservation> getReservations();
    List<Integer> getExistingReservationIds(String chargeBoxId);

    /**
     * Returns the id of the reservation, if the reservation is booked.
     *
     */
    int bookReservation(String idTag, String chargeBoxId,
                        Timestamp startTimestamp, Timestamp expiryTimestamp);

    void cancelReservation(int reservationId);
}