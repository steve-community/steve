package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import de.rwth.idsg.steve.web.dto.ConnectorStatusForm;
import de.rwth.idsg.steve.web.dto.OcppJsonStatus;
import de.rwth.idsg.steve.web.dto.Statistics;
import ocpp.cs._2015._10.RegistrationStatus;

import java.util.List;

public interface ChargePointHelperService {
    Statistics getStats();

    List<ConnectorStatus> getChargePointConnectorStatus(ConnectorStatusForm params);

    List<OcppJsonStatus> getOcppJsonStatus();

    List<ChargePointSelect> getChargePoints(OcppVersion version);
    List<ChargePointSelect> getChargePoints(OcppVersion version, List<RegistrationStatus> inStatusFilter);
}
