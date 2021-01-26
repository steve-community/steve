package net.parkl.ocpp.service.cs;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import net.parkl.ocpp.entities.Connector;
import net.parkl.ocpp.entities.OcppChargeBox;
import net.parkl.ocpp.entities.OcppReservation;
import net.parkl.ocpp.entities.OcppTag;
import net.parkl.ocpp.repositories.ConnectorRepository;
import net.parkl.ocpp.repositories.OcppChargeBoxRepository;
import net.parkl.ocpp.repositories.OcppReservationRepository;
import net.parkl.ocpp.repositories.OcppTagRepository;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.ReservationStatus;
import de.rwth.idsg.steve.repository.dto.InsertReservationParams;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import de.rwth.idsg.steve.web.dto.ReservationQueryForm;

@Service
public class ReservationServiceImpl implements ReservationService {
	private static Logger LOGGER=LoggerFactory.getLogger(ReservationServiceImpl.class);
	@Autowired
	private OcppReservationRepository reservationRepo;
	@Autowired
	private ConnectorRepository connectorRepo;
	@Autowired
	private OcppChargeBoxRepository chargeBoxRepo;
	@Autowired
	private OcppTagRepository tagRepo;
	
	@PersistenceContext
	private EntityManager em;
	
	@Override
	@Transactional
	public void accepted(int reservationId) {
		OcppReservation r = reservationRepo.findById(reservationId).orElseThrow(() ->
			new IllegalArgumentException("Invalid reservation id: "+reservationId));
		
		r.setStatus(ReservationStatus.ACCEPTED.name());
		reservationRepo.save(r);
	}

	@Override
	@Transactional
	public void delete(int reservationId) {
		reservationRepo.deleteById(reservationId);
		LOGGER.debug("The reservation '{}' is deleted.", reservationId);
	}

	@Override
	@Transactional
	public void cancelled(int reservationId) {
		OcppReservation r = reservationRepo.findById(reservationId).orElseThrow(() ->
			new IllegalArgumentException("Invalid reservation id: "+reservationId));
		
		r.setStatus(ReservationStatus.CANCELLED.name());
		reservationRepo.save(r);
	}

	@Override
	@Transactional
	public int insert(InsertReservationParams p) {
		Connector conn = connectorRepo.findByChargeBoxIdAndConnectorId(p.getChargeBoxId(), p.getConnectorId());
		
		OcppReservation r=new OcppReservation();
		r.setConnector(conn);
		r.setOcppTag(p.getIdTag());
		if (p.getStartTimestamp()!=null) {
			r.setStartDatetime(p.getStartTimestamp().toDate());
		}
		if (p.getExpiryTimestamp()!=null) {
			r.setExpiryDatetime(p.getExpiryTimestamp().toDate());
		}
		r.setStatus(ReservationStatus.WAITING.name());
		r=reservationRepo.save(r);
		LOGGER.debug("A new reservation '{}' is inserted.", r.getReservationPk());
        return r.getReservationPk();
	}

	@Override
	public List<Integer> getActiveReservationIds(String chargeBoxId) {
		return reservationRepo.findActiveReservationIds(chargeBoxId,new Date());
	}

	@Override
	public List<de.rwth.idsg.steve.repository.dto.Reservation> getReservations(ReservationQueryForm form) {
		Iterable<OcppTag> tagsAll = tagRepo.findAll();
		Map<String,OcppTag> tagMap=new HashMap<>();
		for (OcppTag tag:tagsAll) {
			tagMap.put(tag.getIdTag(), tag);
		}
		
		Iterable<OcppChargeBox> boxesAll = chargeBoxRepo.findAll();
		Map<String,OcppChargeBox> boxMap=new HashMap<>();
		for (OcppChargeBox box:boxesAll) {
			boxMap.put(box.getChargeBoxId(), box);
		}
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<OcppReservation> cq = cb.createQuery(OcppReservation.class);
			Root<OcppReservation> root = cq.from(OcppReservation.class);
			cq.select(root);
			if (form.isChargeBoxIdSet()) {
				cq=cq.where(cb.equal(root.get("connector").get("chargeBoxId"), form.getChargeBoxId()));
	            //selectQuery.addConditions(CHARGE_BOX.CHARGE_BOX_ID.eq(form.getChargeBoxId()));
	        }

	        if (form.isOcppIdTagSet()) {
	        	cq=cq.where(cb.equal(root.get("ocppTag"), form.getOcppIdTag()));
	        }

	        if (form.isStatusSet()) {
	        	cq=cq.where(cb.equal(root.get("status"), form.getStatus().name()));
	        }

	        switch (form.getPeriodType()) {
	            case ACTIVE:
	            	cq=cq.where(cb.greaterThan(root.get("expiryDatetime"), new Date()));
	                break;
	
	            case FROM_TO:
	            	cq=cq.where(cb.and(cb.greaterThanOrEqualTo(root.get("startDatetime"), form.getFrom().toDate()),
	            			cb.lessThanOrEqualTo(root.get("expiryDatetime"), form.getTo().toDate())
	            			));
	                break;
	
	            default:
	                throw new SteveException("Unknown enum type");
	        }

	       	
			cq=cq.orderBy(cb.asc(root.get("expiryDatetime")));
			TypedQuery<OcppReservation> q = em.createQuery(cq);
			List<OcppReservation> result = q.getResultList();
			

			List<de.rwth.idsg.steve.repository.dto.Reservation> ret=new ArrayList<>();
			for (OcppReservation r:result) {
				
				OcppTag tag = tagMap.get(r.getOcppTag());
				if (tag==null) {
					throw new IllegalStateException("Invalid id tag: "+r.getOcppTag());
				}
				
				OcppChargeBox box=boxMap.get(r.getConnector().getChargeBoxId());
				if (box==null) {
					throw new IllegalStateException("Invalid charge box id: "+r.getConnector().getChargeBoxId());
				}
				
				ret.add(de.rwth.idsg.steve.repository.dto.Reservation.builder()
                        .id(r.getReservationPk())
                        .transactionId(r.getTransaction()!=null?r.getTransaction().getTransactionPk():null)
                        .ocppTagPk(tag.getOcppTagPk())
                        .chargeBoxPk(box.getChargeBoxPk())
                        .ocppIdTag(r.getOcppTag())
                        .chargeBoxId(r.getConnector().getChargeBoxId())
                        .startDatetimeDT(r.getStartDatetime()!=null?new DateTime(r.getStartDatetime()):null)
                        .startDatetime(DateTimeUtils.humanize(r.getStartDatetime()!=null?new DateTime(r.getStartDatetime()):null))
                        .expiryDatetimeDT(r.getExpiryDatetime()!=null?new DateTime(r.getExpiryDatetime()):null)
                        .expiryDatetime(DateTimeUtils.humanize(r.getExpiryDatetime()!=null?new DateTime(r.getExpiryDatetime()):null))
                        .status(r.getStatus())
                        .connectorId(r.getConnector().getConnectorId())
                        .build());
			}
			return ret;
		} finally { 
			em.close();
		}
	}

}
