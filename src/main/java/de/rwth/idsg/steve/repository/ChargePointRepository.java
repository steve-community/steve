package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import de.rwth.idsg.steve.web.dto.ChargePointForm;
import de.rwth.idsg.steve.web.dto.ChargePointQueryForm;

import java.util.List;
import java.util.Map;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 19.08.2014
 */
public interface ChargePointRepository {
    boolean isRegistered(String chargeBoxId);
    List<ChargePointSelect> getChargePointSelect(OcppProtocol protocol);
    List<String> getChargeBoxIds();
    Map<String, Integer> getChargeBoxIdPkPair(List<String> chargeBoxIdList);

    List<ChargePoint.Overview> getOverview(ChargePointQueryForm form);
    ChargePoint.Details getDetails(int chargeBoxPk);

    List<ConnectorStatus> getChargePointConnectorStatus();
    List<Integer> getConnectorIds(String chargeBoxId);

    void addChargePoint(List<String> chargeBoxIdList);
    int addChargePoint(ChargePointForm form);
    void updateChargePoint(ChargePointForm form);
    void deleteChargePoint(int chargeBoxPk);
}
