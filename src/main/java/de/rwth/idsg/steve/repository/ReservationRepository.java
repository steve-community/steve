package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.repository.dto.InsertReservationParams;
import de.rwth.idsg.steve.repository.dto.Reservation;
import de.rwth.idsg.steve.web.dto.ReservationQueryForm;
import org.joda.time.DateTime;

import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 19.08.2014
 */
public interface ReservationRepository {
    List<Reservation> getReservations(ReservationQueryForm form);

    List<Integer> getActiveReservationIds(String chargeBoxId);

    /**
     * Returns the id of the reservation, if the reservation is inserted.
     */
    int insert(InsertReservationParams params);

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
