package net.parkl.ocpp.repositories;

import net.parkl.ocpp.entities.Connector;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ConnectorRepository extends CrudRepository<Connector, Integer>{

	@Query("SELECT c.connectorId FROM Connector AS c WHERE c.chargeBoxId=?1 AND c.connectorId<>0")
	List<Integer> findNonZeroConnectorIdsByChargeBoxId(String chargeBoxId);

	Connector findByChargeBoxIdAndConnectorId(String chargeBoxId, int connectorId);
	List<Connector> findAllByOrderByConnectorPkAsc();
    List<Connector> findByChargeBoxId(String chargeBoxId);
}
