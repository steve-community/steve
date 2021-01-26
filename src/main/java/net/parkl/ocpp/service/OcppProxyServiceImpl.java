package net.parkl.ocpp.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;


import net.parkl.ocpp.repositories.TransactionStartRepository;
import net.parkl.ocpp.service.config.OcppSpecialConfiguration;
import net.parkl.ocpp.util.AsyncWaiter;
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
	private OcppSpecialConfiguration specialConfig;

	@Autowired
	private TransactionStartRepository transactionStartRepository;
	
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
	public OcppChargingProcess createChargingProcess(String chargeBoxId, int connectorId, String idTag, String licensePlate, Float limitKwh, Integer limitMinute) {
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
		p.setLimitMinute(limitMinute);
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
		return connectorMeterValueRepo.findByTransactionAndMeasurandAndPhaseIsNullOrderByValueTimestampDesc(transaction,measurand);
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
		List<ConnectorMeterValue> list = connectorMeterValueRepo.findByTransactionAndMeasurandAndPhaseIsNullOrderByValueTimestampDesc(transaction, measurand);
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
	return specialConfig.isWaitingForChargingProcessEnabled(chargeBoxId);

	}
	
	@Override
	public List<OcppChargingProcess> findOpenChargingProcessesWithLimitKwh() {
		return chargingProcessRepo.findAllByTransactionIsNotNullAndLimitKwhIsNotNullAndEndDateIsNull();
	}
	
@Override
	public List<OcppChargingProcess> findOpenChargingProcessesWithLimitMinute() {
		return chargingProcessRepo.findAllByTransactionIsNotNullAndLimitMinuteIsNotNullAndEndDateIsNull();
	}

  @Override
    public OcppChargingProcess findOpenProcessForRfidTag(String rfidTag, int connectorId, String chargeBoxId) {
        Connector connector = connectorRepo.findByChargeBoxIdAndConnectorId(chargeBoxId, connectorId);
        if (connector == null) {
            throw new IllegalStateException("Invalid charge box id/connector id: " + chargeBoxId + "/" + connectorId);
        }
        return chargingProcessRepo.findByOcppTagAndConnectorAndEndDateIsNull(rfidTag, connector);
    }

    @Override
    public OcppChargingProcess findByTransactionId(int transactionId) {
        TransactionStart transaction = transactionStartRepository.findById(transactionId).orElseThrow(() -> new IllegalStateException("Invalid transaction id"));
        return chargingProcessRepo.findByTransaction(transaction);
    }
}
