package net.parkl.ocpp.service.cs;

import de.rwth.idsg.steve.repository.dto.InsertReservationParams;
import de.rwth.idsg.steve.web.dto.ReservationQueryForm;
import net.parkl.ocpp.entities.TransactionStart;

import java.util.List;

public interface ReservationService {
	void accepted(int reservationId);

	void delete(int reservationId);

	void cancelled(int reservationId);

	int insert(InsertReservationParams res);

	List<Integer> getActiveReservationIds(String chargeBoxId);

	List<de.rwth.idsg.steve.repository.dto.Reservation> getReservations(ReservationQueryForm form);

	void markReservationAsUsed(TransactionStart t, int reservationId, String chargeBoxId);
}
