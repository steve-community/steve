package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import de.rwth.idsg.steve.repository.dto.Heartbeat;
import de.rwth.idsg.steve.web.dto.ChargeBoxForm;

import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 19.08.2014
 */
public interface ChargePointRepository {
    boolean isRegistered(String chargeBoxId);
    List<ChargePointSelect> getChargePointSelect(OcppProtocol protocol);
    List<String> getChargeBoxIds();
    ChargePoint getDetails(String chargeBoxId);
    ChargePoint getDetailsForUpdate(String chargeBoxId);
    List<Heartbeat> getChargePointHeartbeats();
    List<ConnectorStatus> getChargePointConnectorStatus();
    List<Integer> getConnectorIds(String chargeBoxId);

    void addChargePoint(ChargeBoxForm form);
    void updateChargePoint(ChargeBoxForm form);
    void deleteChargePoint(String chargeBoxId);
}
