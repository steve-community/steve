package net.parkl.ocpp.service.cs;

import java.util.List;

import de.rwth.idsg.steve.repository.dto.InsertReservationParams;
import de.rwth.idsg.steve.web.dto.ReservationQueryForm;

public interface ReservationService {

	void accepted(int reservationId);

	void delete(int reservationId);

	void cancelled(int reservationId);

	int insert(InsertReservationParams res);

	List<Integer> getActiveReservationIds(String chargeBoxId);

	List<de.rwth.idsg.steve.repository.dto.Reservation> getReservations(ReservationQueryForm form);
}
