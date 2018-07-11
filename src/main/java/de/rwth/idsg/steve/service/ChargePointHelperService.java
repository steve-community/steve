package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.service.dto.UnidentifiedIncomingObject;
import de.rwth.idsg.steve.web.dto.OcppJsonStatus;
import de.rwth.idsg.steve.web.dto.Statistics;

import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 24.03.2015
 */
public interface ChargePointHelperService {
    Statistics getStats();
    List<OcppJsonStatus> getOcppJsonStatus();
    List<ChargePointSelect> getChargePointsV12();
    List<ChargePointSelect> getChargePointsV15();
    List<ChargePointSelect> getChargePointsV16();

    void rememberNewUnknown(String chargeBoxId);
    List<UnidentifiedIncomingObject> getUnknownChargePoints();
    void removeUnknown(String chargeBoxId);
    void removeUnknown(List<String> chargeBoxIdList);
}
