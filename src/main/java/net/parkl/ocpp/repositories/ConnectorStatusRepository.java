package net.parkl.ocpp.repositories;

import net.parkl.ocpp.entities.Connector;
import net.parkl.ocpp.entities.ConnectorStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ConnectorStatusRepository extends CrudRepository<ConnectorStatus, Integer>{

	List<ConnectorStatus> findAllByOrderByStatusTimestampDesc();

	ConnectorStatus findFirstByConnectorOrderByStatusTimestampDesc(Connector connector);

}
