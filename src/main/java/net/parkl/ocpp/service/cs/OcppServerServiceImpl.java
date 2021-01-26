package net.parkl.ocpp.service.cs;

import java.util.Date;
import java.util.List;

import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.*;
import net.parkl.ocpp.repositories.*;
import net.parkl.ocpp.service.OcppConstants;
import net.parkl.ocpp.service.OcppErrorTranslator;
import net.parkl.ocpp.service.EmobilityServiceProviderFacade;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.rwth.idsg.steve.repository.ReservationStatus;
import de.rwth.idsg.steve.repository.dto.InsertConnectorStatusParams;
import de.rwth.idsg.steve.repository.dto.InsertTransactionParams;
import de.rwth.idsg.steve.repository.dto.UpdateChargeboxParams;
import de.rwth.idsg.steve.repository.dto.UpdateTransactionParams;
import ocpp.cs._2015._10.SampledValue;
import org.springframework.util.CollectionUtils;

@Service
@Slf4j
public class OcppServerServiceImpl implements OcppServerService {
	@Autowired
	private OcppChargeBoxRepository chargeBoxRepo;
	@Autowired
	private ConnectorRepository connectorRepo;
	@Autowired
	private ConnectorStatusRepository connectorStatusRepo;
	@Autowired
	private ConnectorMeterValueRepository connectorMeterValueRepo;
	@Autowired
	private TransactionRepository transactionRepo;
	@Autowired
	private TransactionStartRepository transactionStartRepo;
	@Autowired
	private TransactionStopRepository transactionStopRepo;
	@Autowired
	private TransactionStopFailedRepository transactionStopFailedRepo;
	@Autowired
	private OcppTagRepository tagRepo;
	
	@Autowired
	private OcppChargingProcessRepository chargingProcessRepo;
	
	@Autowired
	private OcppReservationRepository reservationRepo;
	
	@Autowired
	@Qualifier("taskExecutor")
	private TaskExecutor executor;
	
	@Autowired
	private EmobilityServiceProviderFacade proxyFacade;
	@Autowired
	private OcppErrorTranslator errorTranslator;

	
	@Override
	@Transactional
	public void updateChargeboxHeartbeat(String chargeBoxId, DateTime now) {
		OcppChargeBox cb = chargeBoxRepo.findByChargeBoxId(chargeBoxId);
		if (cb==null) {
			throw new IllegalArgumentException("Invalid charge box id: "+chargeBoxId);
		}
		cb.setLastHeartbeatTimestamp(now.toDate());
		chargeBoxRepo.save(cb);
	}

	@Override
	@Transactional
	public void updateEndpointAddress(String chargeBoxId, String endpointAddress) {
		OcppChargeBox cb = chargeBoxRepo.findByChargeBoxId(chargeBoxId);
		if (cb==null) {
			throw new IllegalArgumentException("Invalid charge box id: "+chargeBoxId);
		}
		cb.setEndpointAddress(endpointAddress);
		chargeBoxRepo.save(cb);
		
	}

	@Override
	@Transactional
	public boolean updateChargebox(UpdateChargeboxParams p) {
		OcppChargeBox cb = chargeBoxRepo.findByChargeBoxId(p.getChargeBoxId());
		if (cb==null) {
			 log.error("The chargebox '{}' is NOT registered and its boot NOT acknowledged.", p.getChargeBoxId());
			 return false;
		}
		cb.setOcppProtocol(p.getOcppProtocol().getCompositeValue());
		cb.setChargePointVendor(p.getVendor());
		cb.setChargePointModel(p.getModel());
		cb.setChargePointSerialNumber(p.getPointSerial());
		cb.setChargeBoxSerialNumber(p.getBoxSerial());
		cb.setFwVersion(p.getFwVersion());
		cb.setIccid(p.getIccid());
		cb.setImsi(p.getImsi());
		cb.setMeterType(p.getMeterType());
		cb.setMeterSerialNumber(p.getMeterSerial());
		if (p.getHeartbeatTimestamp()!=null) {
			cb.setLastHeartbeatTimestamp(p.getHeartbeatTimestamp().toDate());
		}
		
		chargeBoxRepo.save(cb);
		log.info("The chargebox '{}' is registered and its boot acknowledged.", p.getChargeBoxId());
		return true;
	}

	@Override
	@Transactional
	public void updateChargeboxFirmwareStatus(String chargeBoxId, String status) {
		OcppChargeBox cb = chargeBoxRepo.findByChargeBoxId(chargeBoxId);
		if (cb==null) {
			throw new IllegalArgumentException("Invalid charge box id: "+chargeBoxId);
		}
		cb.setFwUpdateStatus(status);
		cb.setFwUpdateTimestamp(new Date());
		chargeBoxRepo.save(cb);
		
	}

	@Override
	@Transactional
	public void insertConnectorStatus(InsertConnectorStatusParams p) {
		Connector c=connectorRepo.findByChargeBoxIdAndConnectorId(p.getChargeBoxId(),p.getConnectorId());
		if (c==null) {
			c=new Connector();
			c.setChargeBoxId(p.getChargeBoxId());
			c.setConnectorId(p.getConnectorId());
			c=connectorRepo.save(c);
		}
		
		ConnectorStatus s=new ConnectorStatus();
		s.setConnector(c);
		if (p.getTimestamp()!=null) {
			s.setStatusTimestamp(p.getTimestamp().toDate());
		}
		s.setStatus(p.getStatus());
		s.setErrorCode(p.getErrorCode());
		s.setErrorInfo(p.getErrorInfo());
		s.setVendorId(p.getVendorId());
		s.setVendorErrorCode(p.getVendorErrorCode());
		
		connectorStatusRepo.save(s);
		
		OcppChargingProcess savedProcess=null;
		if (s.getStatus().equals("Available")) {
			OcppChargingProcess process = chargingProcessRepo.findByConnectorAndTransactionIsNullAndEndDateIsNull(c);
			if (process!=null) {
				log.info("Ending charging process on available connector status: {}",process.getOcppChargingProcessId());
				process.setEndDate(new Date());
				savedProcess=chargingProcessRepo.save(process);
			}
		} else if (s.getStatus().equals("Faulted") || s.getStatus().equals("Unavailable")) {
			OcppChargingProcess process = chargingProcessRepo.findByConnectorAndTransactionIsNullAndEndDateIsNull(c);
			if (process!=null) {
				log.info("Saving connector status error to charging process: {} [error={}]...",process.getOcppChargingProcessId(),
						s.getErrorCode());
				String error = errorTranslator.translateError(s.getErrorCode());
				if (error!=null) {
					process.setErrorCode(error);
					savedProcess = chargingProcessRepo.save(process);
		}
			}
		}
		log.debug("Stored a new connector status for {}/{}.", p.getChargeBoxId(), p.getConnectorId());
		
		if (savedProcess!=null) {
			final OcppChargingProcess pr=savedProcess;
			executor.execute(new Runnable() {
				
				@Override
				public void run() {
					log.info("Notifying Parkl about closing charging process: {}...",pr.getOcppChargingProcessId());
					proxyFacade.stopChargingExternal(pr, pr.getErrorCode()!=null?pr.getErrorCode(): OcppConstants.REASON_VEHICLE_NOT_CONNECTED);
				}
			});
		}
	}

	@Override
	@Transactional
	public void updateChargeboxDiagnosticsStatus(String chargeBoxId, String status) {
		OcppChargeBox cb = chargeBoxRepo.findByChargeBoxId(chargeBoxId);
		if (cb==null) {
			throw new IllegalArgumentException("Invalid charge box id: "+chargeBoxId);
		}
		cb.setDiagnosticsStatus(status);
		cb.setDiagnosticsTimestamp(new Date());
		chargeBoxRepo.save(cb);
	}





	@Override
	@Transactional
	public Integer insertTransaction(InsertTransactionParams p) {
		log.info("Starting transaction: chargeBoxId={},connectorId={},idTag={}...",
				p.getChargeBoxId(),p.getConnectorId(),p.getIdTag());
		Connector c=connectorRepo.findByChargeBoxIdAndConnectorId(p.getChargeBoxId(),p.getConnectorId());
		// -------------------------------------------------------------------------
		// Step 1: Insert connector and idTag, if they are new to us
		// -------------------------------------------------------------------------
		if (c==null) {
			c=new Connector();
			c.setChargeBoxId(p.getChargeBoxId());
			c.setConnectorId(p.getConnectorId());
			c=connectorRepo.save(c);
		}

		OcppTag tag = tagRepo.findByIdTag(p.getIdTag());
		boolean unknownTagInserted = false;
		if (tag==null) {
			tag = new OcppTag();
			tag.setIdTag(p.getIdTag());
			String note = "This unknown idTag was used in a transaction that started @ " + p.getStartTimestamp()
					+ ". It was reported @ " + DateTime.now() + ".";
			tag.setMaxActiveTransactionCount(0);
			tag = tagRepo.save(tag);

			unknownTagInserted = true;
		}

		// -------------------------------------------------------------------------
		// Step 2: Insert transaction if it does not exist already
		// ---------------------------------------------------------------------------
		TransactionStart existing = transactionStartRepo.findByConnectorAndIdTagAndStartValues(
				c, p.getIdTag(),
				p.getStartTimestamp()!=null?p.getStartTimestamp().toDate():null,
				p.getStartMeterValue());

		if (existing!=null) {
			log.warn("Transaction already exists: {}", existing.getTransactionPk());
			return existing.getTransactionPk();
		}

		TransactionStart t=new TransactionStart();
		t.setConnector(c);
		t.setOcppTag(p.getIdTag());
		if (p.getStartTimestamp()!=null) {
			t.setStartTimestamp(p.getStartTimestamp().toDate());
		}
		t.setStartValue(p.getStartMeterValue());
		if (p.getEventTimestamp()!=null) {
			t.setEventTimestamp(p.getEventTimestamp().toDate());
		}


		t=transactionStartRepo.save(t);


		if (unknownTagInserted) {
			log.warn("The transaction '{}' contains an unknown idTag '{}' which was inserted into DB "
					+ "to prevent information loss and has been blocked", t.getTransactionPk(), p.getIdTag());
		}


		log.info("Transaction saved id={}, querying charing suitable process for connector: {}", t.getTransactionPk(), c.getConnectorId());
		OcppChargingProcess proc= chargingProcessRepo.findByConnectorAndTransactionIsNullAndEndDateIsNull(c);
		if (proc!=null) {
			log.info("Setting transaction on connector {} to process: {}...",c.getConnectorId(),proc.getOcppChargingProcessId());
			proc.setTransaction(t);
			chargingProcessRepo.save(proc);
		} else {
			log.warn("No active charging process found without transaction for connector: {}",c.getConnectorId());
		}

		// -------------------------------------------------------------------------
		// Step 3 for OCPP >= 1.5: A startTransaction may be related to a reservation
		// -------------------------------------------------------------------------
		if (p.isSetReservationId() && p.getReservationId().intValue()!=-1) {
			OcppReservation r = reservationRepo.findById(p.getReservationId()).
					orElseThrow(() -> new IllegalArgumentException("Invalid reservation: "+p.getReservationId()));
			
            r.setStatus(ReservationStatus.USED.name());
            r.setTransaction(t);
            reservationRepo.save(r);
        }

		// -------------------------------------------------------------------------
		// Step 4: Set connector status
		// -------------------------------------------------------------------------

		if (shouldInsertConnectorStatusAfterTransactionMsg(p.getChargeBoxId())) {
			ConnectorStatus s=new ConnectorStatus();
			s.setConnector(t.getConnector());
			if (p.getStartTimestamp()!=null) {
				s.setStatusTimestamp(p.getStartTimestamp().toDate());
			}
			s.setStatus(p.getStatusUpdate().getStatus());
			s.setErrorCode(p.getStatusUpdate().getErrorCode());

			connectorStatusRepo.save(s);
		}

		return t.getTransactionPk();
	}

	@Override
	@Transactional
	public void updateTransaction(UpdateTransactionParams p) {
		// -------------------------------------------------------------------------
		// Step 1: insert transaction stop data
		// -------------------------------------------------------------------------
		boolean stopValueNull= p.getStopMeterValue()==null;
		OcppChargingProcess savedProcess=null;
		boolean notifyStop=false;

		try {
			TransactionStop stop = new TransactionStop();

			TransactionStopId id = new TransactionStopId();
			id.setTransactionPk(p.getTransactionId());
			id.setEventTimestamp(p.getEventTimestamp().toDate());
			stop.setTransactionStopId(id);

			stop.setEventActor(p.getEventActor());
			if (p.getStopTimestamp()!=null) {
				stop.setStopTimestamp(p.getStopTimestamp().toDate());
			}
			stop.setStopValue(p.getStopMeterValue());
			stop.setStopReason(p.getStopReason());

			stop = transactionStopRepo.save(stop);

			//update charging process
			OcppChargingProcess process = chargingProcessRepo.findByTransactionId(p.getTransactionId());

			if (p.getStopTimestamp()!=null) {
				log.info("Transaction update: {} with end date: {}",
						p.getTransactionId(),
						p.getStopTimestamp());
				if (process!=null) {
					log.info("Ending charging process on transaction update: {} with end date: {}",
							process.getOcppChargingProcessId(),
							p.getStopTimestamp());
					process.setEndDate(p.getStopTimestamp().toDate());
					if (process.getStopRequestDate()==null) {
						notifyStop=true;
					}
					savedProcess=chargingProcessRepo.save(process);
				}
			}
		} catch (Exception e) {
			log.error("Transaction save failed", e);
			try {
				TransactionStopFailed fail=new TransactionStopFailed();

				TransactionStopId id = new TransactionStopId();
				id.setTransactionPk(p.getTransactionId());
				id.setEventTimestamp(p.getEventTimestamp().toDate());
				fail.setTransactionStopId(id);

				fail.setEventActor(p.getEventActor());
				if (p.getStopTimestamp()!=null) {
					fail.setStopTimestamp(p.getStopTimestamp().toDate());
				}
				fail.setStopValue(p.getStopMeterValue());
				fail.setStopReason(p.getStopReason());
				fail.setFailReason(Throwables.getStackTraceAsString(e));

				transactionStopFailedRepo.save(fail);
			} catch (Exception ex) {
				// This is where we give up and just log
				log.error("Transaction stop failure save error", e);
			}
		}

		// -------------------------------------------------------------------------
		// Step 2: Set connector status back. We do this even in cases where step 1
		// fails. It probably and hopefully makes sense.
		// -------------------------------------------------------------------------

		if (shouldInsertConnectorStatusAfterTransactionMsg(p.getChargeBoxId())) {
			TransactionStart transactionStart = transactionStartRepo.findById(p.getTransactionId()).orElseThrow(() -> new IllegalArgumentException("Invalid transaction id: "+p.getTransactionId()));

			ConnectorStatus s=new ConnectorStatus();
			s.setConnector(transactionStart.getConnector());
			if (p.getStopTimestamp()!=null) {
				s.setStatusTimestamp(p.getStopTimestamp().toDate());
			}
			s.setStatus(p.getStatusUpdate().getStatus());
			s.setErrorCode(p.getStatusUpdate().getErrorCode());

			connectorStatusRepo.save(s);

		}

		//ESP notification
		if (stopValueNull || notifyStop) {
			boolean stopped=notifyStop;
			final OcppChargingProcess pr=savedProcess;
			executor.execute(new Runnable() {
				
				@Override
				public void run() {
					if (stopped) {
						// if stop charging was not initiated by the ESP
						log.info("Notifying ESP about stop transaction from charger: {}...",p.getTransactionId());
						proxyFacade.stopChargingExternal(pr,OcppConstants.REASON_VEHICLE_CHARGED);
					} else {
						log.info("Notifying ESP about consumption of transaction: {}...",p.getTransactionId());
						Transaction t = transactionRepo.findById(p.getTransactionId()).orElseThrow(() -> new IllegalArgumentException("Invalid transaction id: "+p.getTransactionId()));

						proxyFacade.updateConsumption(pr,t.getStartValue(),t.getStopValue());
					
					}
				}
			});
		}
	}




	@Override
	@Transactional
	public void insertMeterValues(String chargeBoxIdentity, List<ocpp.cs._2015._10.MeterValue> meterValue,
			int connectorId, Integer transactionId) {
		TransactionStart t=transactionStartRepo.findById(transactionId).orElseThrow(() -> new IllegalArgumentException("Invalid transaction id: "+transactionId));;
		
		Connector connector = connectorRepo.findById(connectorId).orElseThrow(() -> new IllegalArgumentException("Invalid connector id: "+connectorId));
		
		for (ocpp.cs._2015._10.MeterValue mv:meterValue) {
			for (SampledValue v:mv.getSampledValue()) {
				ConnectorMeterValue cmv=new ConnectorMeterValue();
				cmv.setConnector(connector);
				cmv.setTransaction(t);
				
				cmv.setValue(v.getValue());
				if (mv.getTimestamp()!=null) {
					cmv.setValueTimestamp(mv.getTimestamp().toDate());
				}
				cmv.setReadingContext(v.isSetContext() ? v.getContext().value() : null);
				cmv.setFormat(v.isSetFormat() ? v.getFormat().value() : null);
				cmv.setMeasurand(v.isSetMeasurand() ? v.getMeasurand().value() : null);
				cmv.setLocation(v.isSetLocation() ? v.getLocation().value() : null);
				cmv.setUnit(v.isSetUnit() ? v.getUnit().value() : null);
				cmv.setPhase(v.isSetPhase() ? v.getPhase().value() : null);
				connectorMeterValueRepo.save(cmv);
			}
			
		}
	}

	@Override
	@Transactional
	public void insertMeterValues(String chargeBoxIdentity, List<ocpp.cs._2015._10.MeterValue> meterValue,
			int transactionId) {
		if (CollectionUtils.isEmpty(meterValue)) {
			return;
		}

		TransactionStart t=transactionStartRepo.findById(transactionId).orElseThrow(() -> new IllegalArgumentException("Invalid transaction id: "+transactionId));
		if (t==null) {
			throw new IllegalArgumentException("Invalid transaction id: "+transactionId);
		}
		
		
		for (ocpp.cs._2015._10.MeterValue mv:meterValue) {
			for (SampledValue v:mv.getSampledValue()) {
				ConnectorMeterValue cmv=new ConnectorMeterValue();
				cmv.setConnector(t.getConnector());
				cmv.setTransaction(t);
				
				cmv.setValue(v.getValue());
				if (mv.getTimestamp()!=null) {
					cmv.setValueTimestamp(mv.getTimestamp().toDate());
				}
				cmv.setReadingContext(v.isSetContext() ? v.getContext().value() : null);
				cmv.setFormat(v.isSetFormat() ? v.getFormat().value() : null);
				cmv.setMeasurand(v.isSetMeasurand() ? v.getMeasurand().value() : null);
				cmv.setLocation(v.isSetLocation() ? v.getLocation().value() : null);
				cmv.setUnit(v.isSetUnit() ? v.getUnit().value() : null);
				cmv.setPhase(v.isSetPhase() ? v.getPhase().value() : null);
				connectorMeterValueRepo.save(cmv);
			}
			
		}
	}

	private boolean shouldInsertConnectorStatusAfterTransactionMsg(String chargeBoxId) {
		OcppChargeBox cb = chargeBoxRepo.findByChargeBoxId(chargeBoxId);
		return cb!=null && cb.getInsertConnectorStatusAfterTransactionMsg()!=null &&
				cb.getInsertConnectorStatusAfterTransactionMsg().booleanValue();

	}

}
