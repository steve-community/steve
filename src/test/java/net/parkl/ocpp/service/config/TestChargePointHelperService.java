package net.parkl.ocpp.service.config;

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import de.rwth.idsg.steve.service.ChargePointHelperService;
import de.rwth.idsg.steve.service.dto.UnidentifiedIncomingObject;
import de.rwth.idsg.steve.utils.ConnectorStatusCountFilter;
import de.rwth.idsg.steve.web.dto.OcppJsonStatus;
import de.rwth.idsg.steve.web.dto.Statistics;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class TestChargePointHelperService implements ChargePointHelperService {

	private final ChargePointService chargePointService;
	private final GenericService genericService;

	@Autowired
	public TestChargePointHelperService(ChargePointService chargePointService, GenericService genericService) {
		this.chargePointService = chargePointService;
		this.genericService = genericService;
	}

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
		return new ArrayList<>();
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

	@Override
	public List<UnidentifiedIncomingObject> getUnknownChargePoints() {
		log.info("test getUnknownChargePoints called");
		return null;
	}

	@Override
	public void removeUnknown(String chargeBoxId) {
		log.info("test removeUnknown called with chargeBoxId: {}", chargeBoxId);
	}

	@Override
	public void removeUnknown(List<String> chargeBoxIdList) {
		log.info("test removeUnknown called with chargeBoxIdList: {}", chargeBoxIdList);
	}
 
}
