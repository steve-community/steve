package net.parkl.ocpp.service;

import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import net.parkl.ocpp.entities.OcppChargingProcess;
import org.springframework.util.StringUtils;

@Component
public class OcppStartTimeoutManager {
	private static final Logger LOGGER=LoggerFactory.getLogger(OcppStartTimeoutManager.class);
	
	@Autowired
	private OcppProxyConfiguration config;
	@Autowired
	private OcppProxyService proxyService;
	
	@Autowired
	private EmobilityServiceProviderFacade facade;
	

	

	@Scheduled(fixedRate=10000)
	public void checkForStartTimeout() {
		
		if (!config.isStartTimeoutEnabled()) {
			return;
		}
		LOGGER.debug("Checking start timeout...");
		
		List<OcppChargingProcess> processes=proxyService.findOpenChargingProcessesWithoutTransaction();
		for (OcppChargingProcess cp:processes) {
			if (isStartTimeoutEnabledForChargeBox(cp.getConnector().getChargeBoxId())) {
				LOGGER.info("Checking for timeout of process: {}...",cp.getOcppChargingProcessId());
				
				try {
					checkForProcessStartTimeout(cp);	
				} catch (Exception ex) {
					LOGGER.error("Failed to process start timeout: "+cp.getOcppChargingProcessId(),ex);
				}
			}
			
		}
		
	}
	
	
	private void checkForProcessStartTimeout(OcppChargingProcess cp) throws Exception {
		if (cp.getStartDate().getTime()+config.getStartTimeoutSecs()*1000<System.currentTimeMillis()) {
			LOGGER.info("Charging process start timeout: {}",cp.getOcppChargingProcessId());
			
			
			LOGGER.info("Charging process stopped on timeout, connector reset: {}-{}",
					cp.getConnector().getChargeBoxId(),cp.getConnector().getConnectorId());
			facade.changeAvailability(cp.getConnector().getChargeBoxId(), String.valueOf(cp.getConnector().getConnectorId()), false);
			Thread.sleep(1000);
			facade.changeAvailability(cp.getConnector().getChargeBoxId(), String.valueOf(cp.getConnector().getConnectorId()), true);
			
		}
	}


	private boolean isStartTimeoutEnabledForChargeBox(String chargeBoxId) {
		String ids=config.getStartTimeoutChargeBoxIds();
		if (StringUtils.isEmpty(ids)) {
			return false;
		}
		String[] split = ids.split(",");
		for (String id:split) {
			if (id.equals(chargeBoxId)) {
				return true;
			}
		}
		return false;
	}
}
