package net.parkl.ocpp.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.parkl.ocpp.entities.Connector;
import net.parkl.ocpp.entities.ConnectorMeterValue;
import net.parkl.ocpp.entities.OcppChargingProcess;
import net.parkl.ocpp.entities.TransactionStart;
import net.parkl.ocpp.repositories.ConnectorMeterValueRepository;
import net.parkl.ocpp.repositories.ConnectorRepository;
import net.parkl.ocpp.repositories.OcppChargingProcessRepository;
import net.parkl.stevep.util.AsyncWaiter;
import org.springframework.util.StringUtils;

@Service
public class OcppProxyServiceImpl implements OcppProxyService {
	private static final Logger LOGGER=LoggerFactory.getLogger(OcppProxyServiceImpl.class);

	@Autowired
	private OcppChargingProcessRepository chargingProcessRepo;
	
	@Autowired
	private ConnectorRepository connectorRepo;
	@Autowired
	private ConnectorMeterValueRepository connectorMeterValueRepo;
	
	@Autowired
	private OcppProxyConfiguration proxyConfig;
	
	public OcppChargingProcess findOpenChargingProcessWithoutTransaction(String chargeBoxId, int connectorId) {
		Connector c = connectorRepo.findByChargeBoxIdAndConnectorId(chargeBoxId, connectorId);
		if (c==null) {
			throw new IllegalStateException("Invalid charge box id/connector id: "+chargeBoxId+"/"+connectorId);
		}
		return chargingProcessRepo.findByConnectorAndTransactionIsNullAndEndDateIsNull(c);
	}
	public OcppChargingProcess findOpenChargingProcess(String chargeBoxId, int connectorId) {
		Connector c = connectorRepo.findByChargeBoxIdAndConnectorId(chargeBoxId, connectorId);
		if (c==null) {
			throw new IllegalStateException("Invalid charge box id/connector id: "+chargeBoxId+"/"+connectorId);
		}
		return chargingProcessRepo.findByConnectorAndEndDateIsNull(c);
	}
 	@Override
	@Transactional
	public OcppChargingProcess createChargingProcess(String chargeBoxId, int connectorId, String idTag, String licensePlate, Float limitKwh) {
		Connector c = connectorRepo.findByChargeBoxIdAndConnectorId(chargeBoxId, connectorId);
		if (c==null) {
			throw new IllegalStateException("Invalid charge box id/connector id: "+chargeBoxId+"/"+connectorId);
		}
		LOGGER.info("Creating OcppChargingProcess on {}/{} with id tag {} for: {}...",chargeBoxId, connectorId,
				idTag, licensePlate);
		OcppChargingProcess existing = chargingProcessRepo.findByConnectorAndTransactionIsNullAndEndDateIsNull(c);
		if (existing!=null) {
			throw new IllegalStateException("Connector occupied: "+c.getConnectorId());
		}
		
		OcppChargingProcess p=new OcppChargingProcess();
		p.setOcppChargingProcessId(UUID.randomUUID().toString());
		p.setConnector(c);
		p.setLicensePlate(licensePlate);
		p.setOcppTag(idTag);
		p.setLimitKwh(limitKwh);
		return chargingProcessRepo.save(p);
	}

	@Override
	public OcppChargingProcess findOcppChargingProcess(String processId) {
		return chargingProcessRepo.findById(processId).orElse(null);
	}

	@Override
	@Transactional
	public OcppChargingProcess stopChargingProcess(String processId) {
		OcppChargingProcess cp = chargingProcessRepo.findById(processId).
				orElseThrow(() ->  new IllegalStateException("Invalid OcppChargingProcess id: "+processId));
		
		if (cp.getEndDate()!=null) {
			throw new IllegalStateException("OcppChargingProcess already ended: "+processId);
		}
		
		cp.setEndDate(new Date());
		return chargingProcessRepo.save(cp);
	}

	@Override
	public List<ConnectorMeterValue> getConnectorMeterValueByTransactionAndMeasurand(TransactionStart transaction, String measurand) {
		return connectorMeterValueRepo.findByTransactionAndMeasurandOrderByValueTimestampDesc(transaction,measurand);
	}

	@Override
	@Transactional
	public OcppChargingProcess stopRequested(String processId) {
		OcppChargingProcess cp = chargingProcessRepo.findById(processId).
				orElseThrow(() ->  new IllegalStateException("Invalid OcppChargingProcess id: "+processId));
		
		if (cp.getStopRequestDate()!=null) {
			LOGGER.warn("OcppChargingProcess stop already requested: "+processId);
			return cp;
		}
		
		cp.setStopRequestDate(new Date());
		return chargingProcessRepo.save(cp);
	}
	@Override
	public ConnectorMeterValue getLastConnectorMeterValueByTransactionAndMeasurand(TransactionStart transaction,
                                                                                   String measurand) {
		List<ConnectorMeterValue> list = connectorMeterValueRepo.findByTransactionAndMeasurandOrderByValueTimestampDesc(transaction, measurand);
		if (list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}
	@Override
	@Transactional
	public OcppChargingProcess stopRequestCancelled(String processId) {
		OcppChargingProcess cp = chargingProcessRepo.findById(processId).
				orElseThrow(() ->  new IllegalStateException("Invalid OcppChargingProcess id: "+processId));
		
		cp.setStopRequestDate(null);
		return chargingProcessRepo.save(cp);
	}
	@Override
	public List<OcppChargingProcess> getActiveProcessesByChargeBox(String chargeBoxId) {
		return chargingProcessRepo.findActiveByChargeBoxId(chargeBoxId);
	}
	@Override
	public List<OcppChargingProcess> findOpenChargingProcessesWithoutTransaction() {
		return chargingProcessRepo.findAllByTransactionIsNullAndEndDateIsNull();
	}
	@Override
	public OcppChargingProcess checkForChargingProcessWithoutTransaction(String chargeBoxId, int connectorId) {
		Connector conn = connectorRepo.findByChargeBoxIdAndConnectorId(chargeBoxId, connectorId);
		if (conn == null) {
			throw new IllegalArgumentException("Connector not found: " + chargeBoxId + "/" + connectorId);
		}
		AsyncWaiter<OcppChargingProcess> waiter = new AsyncWaiter<>(2000);
		waiter.setDelayMs(0);
		waiter.setIntervalMs(200);
		return waiter.waitFor(new Callable<OcppChargingProcess>() {

			@Override
			public OcppChargingProcess call() throws Exception {
				return chargingProcessRepo.findByConnectorAndTransactionIsNullAndEndDateIsNull(conn);
			}
		});
	}
	@Override
	public boolean isWaitingForChargingProcess(String chargeBoxId) {
		String ids=proxyConfig.getWaitingForChargingProcessChargeBoxIds();
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
	
	@Override
	public List<OcppChargingProcess> findOpenChargingProcessesWithLimit() {
		return chargingProcessRepo.findAllByTransactionIsNotNullAndLimitKwhIsNotNullAndEndDateIsNull();
	}
	
}
