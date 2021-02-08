package net.parkl.ocpp.service.cs;


import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.OcppTransport;
import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.repository.dto.ChargePoint.Overview;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import de.rwth.idsg.steve.repository.dto.UpdateChargeboxParams;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import de.rwth.idsg.steve.web.dto.ChargePointForm;
import de.rwth.idsg.steve.web.dto.ChargePointQueryForm;
import de.rwth.idsg.steve.web.dto.ConnectorStatusForm;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.OcppAddress;
import net.parkl.ocpp.entities.OcppChargeBox;
import net.parkl.ocpp.repositories.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 14.08.2014
 */
@Service
@Slf4j
public class ChargePointServiceImpl implements ChargePointService {

    private final OcppChargeBoxRepository chargeBoxRepository;
    private final OcppAddressRepository addressRepository;
    private final ConnectorRepository connectorRepository;
    private final ConnectorStatusRepository connectorStatusRepository;
    private final ChargePointCriteriaRepository chargePointCriteriaRepository;
	private final AddressService addressService;

	@Autowired
	public ChargePointServiceImpl(OcppChargeBoxRepository chargeBoxRepository, OcppAddressRepository addressRepository, ConnectorRepository connectorRepository, ConnectorStatusRepository connectorStatusRepository, ChargePointCriteriaRepository chargePointCriteriaRepository, AddressService addressService) {
		this.chargeBoxRepository = chargeBoxRepository;
		this.addressRepository = addressRepository;
		this.connectorRepository = connectorRepository;
		this.connectorStatusRepository = connectorStatusRepository;
		this.chargePointCriteriaRepository = chargePointCriteriaRepository;
		this.addressService = addressService;
	}

	@Override
    public Optional<String> getRegistrationStatus(String chargeBoxId) {
        String status = chargeBoxRepository.findChargeBoxRegistrationStatus(chargeBoxId);

        return Optional.ofNullable(status);
    }

    @Override
    public List<ChargePointSelect> getChargePointSelect(OcppProtocol protocol, List<String> inStatusFilter) {
        final OcppTransport transport = protocol.getTransport();
        
        List<OcppChargeBox> result=chargeBoxRepository.findByOcppProtocolAndRegistrationStatuses(protocol.getCompositeValue(),
				inStatusFilter);
        List<ChargePointSelect> ret=new ArrayList<>();
        for (OcppChargeBox r:result) {
        	ret.add(new ChargePointSelect(transport, r.getChargeBoxId(), r.getEndpointAddress()));
        }
        return ret;
    }

    @Override
    public List<String> getChargeBoxIds() {
    	return chargeBoxRepository.findAllChargeBoxIds();
    }

    @Override
    public Map<String, Integer> getChargeBoxIdPkPair(List<String> chargeBoxIdList) {
    	List<OcppChargeBox> result=chargeBoxRepository.findByChargeBoxIdIn(chargeBoxIdList);
    	Map<String,Integer> ret=new HashMap<>();
    	for (OcppChargeBox r:result) {
    		ret.put(r.getChargeBoxId(), r.getChargeBoxPk());
    	}
    	return ret;
    }

    @Override
    public List<ChargePoint.Overview> getOverview(ChargePointQueryForm form) {
        return toChargePointOverviewList(chargePointCriteriaRepository.getOverviewInternal(form));
    }

    private List<Overview> toChargePointOverviewList(List<OcppChargeBox> list) {
		List<Overview> ret=new ArrayList<>();
		for (OcppChargeBox r:list) {
			ret.add(ChargePoint.Overview.builder()
                                              .chargeBoxPk(r.getChargeBoxPk())
                                              .chargeBoxId(r.getChargeBoxId())
                                              .description(r.getDescription())
                                              .ocppProtocol(r.getOcppProtocol())
                                              .lastHeartbeatTimestampDT(r.getLastHeartbeatTimestamp()!=null?new DateTime(r.getLastHeartbeatTimestamp()):null)
                                              .lastHeartbeatTimestamp(DateTimeUtils.humanize(r.getLastHeartbeatTimestamp()!=null?new DateTime(r.getLastHeartbeatTimestamp()):null))
					                          .build());
		}
		return ret;
	}

	@Override
    public ChargePoint.Details getDetails(int chargeBoxPk) {
        OcppChargeBox cbr = chargeBoxRepository.findById(chargeBoxPk).
        		orElseThrow(() -> new SteveException("Charge point not found"));

       
        return new ChargePoint.Details(cbr, cbr.getAddress());
    }

    @Override
    public List<ConnectorStatus> getChargePointConnectorStatus(ConnectorStatusForm form) {
    	List<net.parkl.ocpp.entities.ConnectorStatus> result=connectorStatusRepository.findAllByOrderByStatusTimestampDesc();
       
    	
    	Map<String,OcppChargeBox> chargeBoxMap=new HashMap<>();
    	Iterable<OcppChargeBox> cbAll = chargeBoxRepository.findAll();
    	for (OcppChargeBox c:cbAll) {
    		chargeBoxMap.put(c.getChargeBoxId(), c);
    	}
    	
    	Map<Integer,ConnectorStatus> statusMap=new HashMap<>();
    	
    	for (net.parkl.ocpp.entities.ConnectorStatus r:result) {
    		OcppChargeBox cb = chargeBoxMap.get(r.getConnector().getChargeBoxId());
    		if (cb==null) {
    			throw new IllegalArgumentException("Invalid charge box id: "+r.getConnector().getChargeBoxId());
    		}

    		if (form == null || form.getStatus() == null ||
					form.getStatus().equals(r.getStatus())) {
				ConnectorStatus s = ConnectorStatus.builder()
						.chargeBoxPk(cb.getChargeBoxPk())
						.chargeBoxId(r.getConnector().getChargeBoxId())
						.connectorId(r.getConnector().getConnectorId())
						.timeStamp(r.getStatusTimestamp() != null ? DateTimeUtils.humanize(new DateTime(r.getStatusTimestamp())) : null)
						.statusTimestamp(r.getStatusTimestamp() != null ? new DateTime(r.getStatusTimestamp()) : null)
						.status(r.getStatus())
						.errorCode(r.getErrorCode())
						.build();
				if (!statusMap.containsKey(r.getConnector().getConnectorId()) ||
						statusMap.get(r.getConnector().getConnectorId()).getStatusTimestamp().isBefore(r.getStatusTimestamp().getTime())) {
					statusMap.put(r.getConnector().getConnectorId(), s);
					//ret.add(s);
				}
			}
    	}
    	
    	return new ArrayList<>(statusMap.values());
    }

    @Override
    public List<Integer> getNonZeroConnectorIds(String chargeBoxId) {
    	return connectorRepository.findNonZeroConnectorIdsByChargeBoxId(chargeBoxId);
    }

    @Override
    @Transactional
    public void addChargePointList(List<String> chargeBoxIdList) {
       	for (String cbId:chargeBoxIdList) {
    		OcppChargeBox cb=new OcppChargeBox();
    		cb.setChargeBoxId(cbId);
    		cb.setInsertConnectorStatusAfterTransactionMsg(false);
    		chargeBoxRepository.save(cb);
    	}
    }

    @Override
    @Transactional
    public int addChargePoint(ChargePointForm form) {
    	OcppAddress addr = addressService.saveAddress(form.getAddress());
    	
    	OcppChargeBox cb=new OcppChargeBox();
    	fillChargeBox(cb,form,addr);
    	cb=chargeBoxRepository.save(cb);
    	return cb.getChargeBoxPk();
       
    }

    @Override
    @Transactional
    public void updateChargePoint(ChargePointForm form) {
    	OcppChargeBox cb = chargeBoxRepository.findById(form.getChargeBoxPk()).
    			orElseThrow(() -> new IllegalArgumentException("Invalid charge box PK: "+form.getChargeBoxPk()));
    	
    	try {
	    	OcppAddress addr = addressService.saveAddress(form.getAddress());
	    	fillChargeBox(cb,form,addr);
	    	cb=chargeBoxRepository.save(cb);
    	} catch (Exception e) {
    		
            throw new SteveException("Failed to update the charge point with chargeBoxId '%s'",
                    form.getChargeBoxId(), e);
        }
    	
    	
    }

    private void fillChargeBox(OcppChargeBox cb, ChargePointForm form, OcppAddress addr) {
		cb.setAddress(addr);
		cb.setChargeBoxId(form.getChargeBoxId());
		cb.setDescription(form.getDescription());
		cb.setLocationLatitude(form.getLocationLatitude());
		cb.setLocationLongitude(form.getLocationLongitude());
		cb.setNote(form.getNote());
		cb.setRegistrationStatus(form.getRegistrationStatus());
	}

	@Override
	@Transactional
    public void deleteChargePoint(int chargeBoxPk) {
    	OcppChargeBox cb = chargeBoxRepository.findById(chargeBoxPk).
    			orElseThrow(() -> new IllegalArgumentException("Invalid charge box PK: "+chargeBoxPk));
    	
    	
    	try {
	    	OcppAddress addr = cb.getAddress();
			
	    	chargeBoxRepository.delete(cb);
	    	addressRepository.delete(addr);
		} catch (Exception e) {
			throw new SteveException("Failed to delete the charge point", e);
		}
       
    }

	@Override
	@Transactional
	public void updateChargeboxHeartbeat(String chargeBoxId, DateTime now) {
		OcppChargeBox cb = chargeBoxRepository.findByChargeBoxId(chargeBoxId);
		if (cb==null) {
			throw new IllegalArgumentException("Invalid charge box id: "+chargeBoxId);
		}
		cb.setLastHeartbeatTimestamp(now.toDate());
		chargeBoxRepository.save(cb);
	}

	@Override
	@Transactional
	public void updateEndpointAddress(String chargeBoxId, String endpointAddress) {
		OcppChargeBox cb = chargeBoxRepository.findByChargeBoxId(chargeBoxId);
		if (cb==null) {
			throw new IllegalArgumentException("Invalid charge box id: "+chargeBoxId);
		}
		cb.setEndpointAddress(endpointAddress);
		chargeBoxRepository.save(cb);

	}

	@Override
	@Transactional
	public boolean updateChargebox(UpdateChargeboxParams p) {
		OcppChargeBox cb = chargeBoxRepository.findByChargeBoxId(p.getChargeBoxId());
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

		chargeBoxRepository.save(cb);
		log.info("The chargebox '{}' is registered and its boot acknowledged.", p.getChargeBoxId());
		return true;
	}

	@Override
	@Transactional
	public void updateChargeboxFirmwareStatus(String chargeBoxId, String status) {
		OcppChargeBox cb = chargeBoxRepository.findByChargeBoxId(chargeBoxId);
		if (cb==null) {
			throw new IllegalArgumentException("Invalid charge box id: "+chargeBoxId);
		}
		cb.setFwUpdateStatus(status);
		cb.setFwUpdateTimestamp(new Date());
		chargeBoxRepository.save(cb);

	}



	@Override
	@Transactional
	public void updateChargeboxDiagnosticsStatus(String chargeBoxId, String status) {
		OcppChargeBox cb = chargeBoxRepository.findByChargeBoxId(chargeBoxId);
		if (cb==null) {
			throw new IllegalArgumentException("Invalid charge box id: "+chargeBoxId);
		}
		cb.setDiagnosticsStatus(status);
		cb.setDiagnosticsTimestamp(new Date());
		chargeBoxRepository.save(cb);
	}

	@Override
	public List<OcppChargeBox> findAllChargePoints() {
		return chargeBoxRepository.findAll();
	}

	@Override
	public OcppChargeBox findByChargeBoxId(String chargeBoxId) {
		return chargeBoxRepository.findByChargeBoxId(chargeBoxId);
	}

	public boolean shouldInsertConnectorStatusAfterTransactionMsg(String chargeBoxId) {
		OcppChargeBox cb = chargeBoxRepository.findByChargeBoxId(chargeBoxId);
		return cb != null && cb.insertConnectorStatusAfterTransactionMsg();
	}

	@Override
	public Map<String, OcppChargeBox> getIdChargeBoxMap() {
		Iterable<OcppChargeBox> boxesAll = chargeBoxRepository.findAll();
		Map<String, OcppChargeBox> boxMap = new HashMap<>();
		for (OcppChargeBox box : boxesAll) {
			boxMap.put(box.getChargeBoxId(), box);
		}
		return boxMap;
	}
}
