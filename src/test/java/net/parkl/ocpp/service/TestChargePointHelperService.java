package net.parkl.ocpp.service;

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import de.rwth.idsg.steve.service.ChargePointHelperService;
import de.rwth.idsg.steve.service.dto.UnidentifiedIncomingObject;
import de.rwth.idsg.steve.utils.ConnectorStatusCountFilter;
import de.rwth.idsg.steve.web.dto.OcppJsonStatus;
import de.rwth.idsg.steve.web.dto.Statistics;
import net.parkl.ocpp.service.cs.ChargePointService;
import net.parkl.ocpp.service.cs.GenericService;
import ocpp.cs._2015._10.RegistrationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TestChargePointHelperService implements ChargePointHelperService {
	 // SOAP-based charge points are stored in DB with an endpoint address
    @Autowired private ChargePointService chargePointService;
    @Autowired private GenericService genericService;
    
	@Override
	public Optional<RegistrationStatus> getRegistrationStatus(String chargeBoxId) {
		return Optional.empty();
	}

	@Override
	public Statistics getStats() {
		 Statistics stats = genericService.getStats();
	      
	     List<ConnectorStatus> latestList = chargePointService.getChargePointConnectorStatus();
	     stats.setStatusCountMap(ConnectorStatusCountFilter.getStatusCountMap(latestList));

	     return stats;
	}

	@Override
	public List<OcppJsonStatus> getOcppJsonStatus() {
		List<OcppJsonStatus> returnList = new ArrayList<>();
		return returnList;
	}

	@Override
	public List<ChargePointSelect> getChargePoints(OcppVersion version) {
		return chargePointService.getChargePointSelect(OcppProtocol.V_16_SOAP, Collections.singletonList(RegistrationStatus.ACCEPTED.value()));
	}

	@Override
	public List<ChargePointSelect> getChargePoints(OcppVersion version, List<RegistrationStatus> inStatusFilter) {
		return chargePointService.getChargePointSelect(OcppProtocol.V_16_SOAP,inStatusFilter.stream().map(Enum::name)
				.collect(Collectors.toList()));
	}

	/*@Override
	public List<ChargePointSelect> getChargePointsV12() {
		 return getChargePoints(OcppProtocol.V_12_SOAP);
	}

	@Override
	public List<ChargePointSelect> getChargePointsV15() {
		return getChargePoints(OcppProtocol.V_15_SOAP);
	}

	@Override
	public List<ChargePointSelect> getChargePointsV16() {
		return getChargePoints(OcppProtocol.V_16_SOAP);
	}*/

	@Override
	public List<UnidentifiedIncomingObject> getUnknownChargePoints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeUnknown(String chargeBoxId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeUnknown(List<String> chargeBoxIdList) {
		// TODO Auto-generated method stub
		
	}
 
}
