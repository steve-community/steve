package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.service.dto.UnidentifiedIncomingObject;
import de.rwth.idsg.steve.web.dto.OcppJsonStatus;
import de.rwth.idsg.steve.web.dto.Statistics;
import ocpp.cs._2015._10.RegistrationStatus;

import java.util.List;
import java.util.Optional;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 24.03.2015
 */
public interface ChargePointHelperService {
    Optional<RegistrationStatus> getRegistrationStatus(String chargeBoxId);

    Statistics getStats();
    List<OcppJsonStatus> getOcppJsonStatus();
    List<ChargePointSelect> getChargePoints(OcppVersion version);
    List<ChargePointSelect> getChargePoints(OcppVersion version, List<RegistrationStatus> inStatusFilter);
    List<UnidentifiedIncomingObject> getUnknownChargePoints();
    void removeUnknown(String chargeBoxId);
    void removeUnknown(List<String> chargeBoxIdList);
}
