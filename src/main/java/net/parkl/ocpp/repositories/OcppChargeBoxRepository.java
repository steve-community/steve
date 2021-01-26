package net.parkl.ocpp.repositories;

import net.parkl.ocpp.entities.OcppChargeBox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface OcppChargeBoxRepository extends JpaRepository<OcppChargeBox, Integer> {

	OcppChargeBox findByChargeBoxId(String chargeBoxId);

	@Query("SELECT OBJECT(c) FROM OcppChargeBox AS c WHERE c.ocppProtocol=?1 AND c.endpointAddress IS NOT NULL AND c.registrationStatus IN ?2")
	List<OcppChargeBox> findByOcppProtocolAndRegistrationStatuses(String protocol, List<String> statuses);

	List<OcppChargeBox> findByChargeBoxIdIn(List<String> chargeBoxIdList);

	@Query("SELECT c.chargeBoxId FROM OcppChargeBox AS c")
	List<String> findAllChargeBoxIds();

	@Query("SELECT COUNT(c.lastHeartbeatTimestamp) FROM OcppChargeBox AS c WHERE c.lastHeartbeatTimestamp>=?1")
	long countLastHeartBeatAfter(Date date);
	@Query("SELECT COUNT(c.lastHeartbeatTimestamp) FROM OcppChargeBox AS c WHERE c.lastHeartbeatTimestamp>=?1 AND c.lastHeartbeatTimestamp<?2")
	long countLastHeartBeatBetween(Date date1,Date date2);

	@Query("SELECT COUNT(c.lastHeartbeatTimestamp) FROM OcppChargeBox AS c WHERE c.lastHeartbeatTimestamp<?1")
	long countLastHeartBeatBefore(Date date);
	
@Query("SELECT c.registrationStatus FROM OcppChargeBox AS c WHERE c.chargeBoxId=?1")
    String findChargeBoxRegistrationStatus(String chargeBoxId);
}
