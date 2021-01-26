package net.parkl.ocpp.repositories;

import net.parkl.ocpp.entities.OcppReservation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface OcppReservationRepository extends CrudRepository<OcppReservation, Integer>{

	@Query("SELECT r.reservationPk FROM OcppReservation AS r WHERE r.connector.chargeBoxId=?1 AND r.expiryDatetime>?2 AND r.status='ACCEPTED'")
	List<Integer> findActiveReservationIds(String chargeBoxId,Date now);

	@Query("SELECT COUNT(r.reservationPk) FROM OcppReservation AS r WHERE r.expiryDatetime>?1 AND r.status='Accepted'")
	long countByExpiryDateGreater(Date now);

}
