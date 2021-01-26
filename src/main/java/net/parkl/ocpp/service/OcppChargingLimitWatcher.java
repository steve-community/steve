package net.parkl.ocpp.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import net.parkl.ocpp.entities.OcppChargingProcess;

@Component
public class OcppChargingLimitWatcher {
	private static final Logger LOGGER=LoggerFactory.getLogger(OcppChargingLimitWatcher.class);
	
	@Autowired
	private OcppProxyService proxyService;
	@Autowired
	private EmobilityServiceProviderFacade facade;
	
	@Autowired
	private TaskExecutor taskExecutor;
	
	
	
	@Scheduled(fixedRate=10000)
	public void checkChargingLimit() {
		LOGGER.debug("Checking charging limit...");
		
		List<OcppChargingProcess> processes=proxyService.findOpenChargingProcessesWithLimit();
		for (OcppChargingProcess cp:processes) {
			if (cp.getTransaction()!=null) {
				LOGGER.info("Checking charging process with limit: {}...", cp.getOcppChargingProcessId());
				PowerValue pw = facade.getPowerValue(cp.getTransaction());
				float kWh = OcppConsumptionHelper.getKwhValue(pw.getValue(),pw.getUnit());
				if (kWh>=cp.getLimitKwh()) {
					LOGGER.info("Limit {} exceeded ({}) for charing process, stopping: {}...",cp.getLimitKwh(), kWh, cp.getOcppChargingProcessId());
					
					taskExecutor.execute(new Runnable() {
						
						@Override
						public void run() {
							facade.stopChargingWithLimit(cp.getOcppChargingProcessId());
						}
					});
					
				}
			}
			
		}
		
	}
}
