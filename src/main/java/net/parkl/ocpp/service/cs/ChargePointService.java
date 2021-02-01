package net.parkl.ocpp.service.cs;


import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import de.rwth.idsg.steve.repository.dto.UpdateChargeboxParams;
import de.rwth.idsg.steve.web.dto.ChargePointForm;
import de.rwth.idsg.steve.web.dto.ChargePointQueryForm;
import de.rwth.idsg.steve.web.dto.ConnectorStatusForm;
import net.parkl.ocpp.entities.OcppChargeBox;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;
import java.util.Optional;


public interface ChargePointService {
    Optional<String> getRegistrationStatus(String chargeBoxId);
    List<ChargePointSelect> getChargePointSelect(OcppProtocol protocol, List<String> statusFilter);
    List<String> getChargeBoxIds();
    Map<String, Integer> getChargeBoxIdPkPair(List<String> chargeBoxIdList);

    List<ChargePoint.Overview> getOverview(ChargePointQueryForm form);
    ChargePoint.Details getDetails(int chargeBoxPk);

    default List<ConnectorStatus> getChargePointConnectorStatus() {
        return getChargePointConnectorStatus(null);
    }

    List<ConnectorStatus> getChargePointConnectorStatus(@Nullable ConnectorStatusForm form);

    List<Integer> getNonZeroConnectorIds(String chargeBoxId);

    int addChargePoint(ChargePointForm form);
    void updateChargePoint(ChargePointForm form);
    void deleteChargePoint(int chargeBoxPk);
	void addChargePointList(List<String> chargeBoxIdList);


    void updateChargeboxHeartbeat(String chargeBoxId, DateTime now);

    void updateEndpointAddress(String chargeBoxId, String endpointAddress);

    boolean updateChargebox(UpdateChargeboxParams params);

    void updateChargeboxFirmwareStatus(String chargeBoxIdentity, String status);
    void updateChargeboxDiagnosticsStatus(String chargeBoxIdentity, String status);

    List<OcppChargeBox> findAllChargePoints();

    OcppChargeBox findByChargeBoxId(String chargeBoxId);

    boolean shouldInsertConnectorStatusAfterTransactionMsg(String chargeBoxId);
}
