package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import de.rwth.idsg.steve.repository.dto.Heartbeat;

import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 19.08.2014
 */
public interface ChargePointRepository {
    List<ChargePointSelect> getChargePointsV12();
    List<ChargePointSelect> getChargePointsV15();
    List<String> getChargeBoxIds();
    ChargePoint getChargePointDetails(String chargeBoxId);
    List<Heartbeat> getChargePointHeartbeats();
    List<ConnectorStatus> getChargePointConnectorStatus();
    void addChargePoint(String chargeBoxId);
    void deleteChargePoint(String chargeBoxId);
    List<Integer> getConnectorIds(String chargeBoxId);
}