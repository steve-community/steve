package net.parkl.ocpp.service.cs;

import java.io.Writer;
import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.parkl.ocpp.entities.*;
import net.parkl.ocpp.repositories.*;
import net.parkl.stevep.util.CalendarUtils;
import net.parkl.stevep.util.ListTransform;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.dto.TransactionDetails;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import de.rwth.idsg.steve.web.dto.TransactionQueryForm;

@Service
public class TransactionServiceImpl implements TransactionService {
	@Getter
	@Setter
	@EqualsAndHashCode
	static class MeterValueKey {
		private String value;
		private String readingContext;
		private String format;
		private String measurand;
		private String location;
		private String unit;
		
		public MeterValueKey(String value, String readingContext, String format, String measurand, String location,
				String unit) {
			super();
			this.value = value;
			this.readingContext = readingContext;
			this.format = format;
			this.measurand = measurand;
			this.location = location;
			this.unit = unit;
		}

		
	}
	
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private TransactionRepository transactionRepo;
	@Autowired
	private TransactionStartRepository transactionStartRepo;
	@Autowired
	private OcppChargeBoxRepository chargeBoxRepo;
	@Autowired
	private OcppTagRepository tagRepo;
	@Autowired
	private ConnectorMeterValueRepository connectorMeterValueRepo;
    
		
	@Override
	public List<Integer> getActiveTransactionIds(String chargeBoxId) {
		return transactionRepo.findActiveTransactionIds(chargeBoxId);
	}

	@Override
	public List<de.rwth.idsg.steve.repository.dto.Transaction> getTransactions(TransactionQueryForm form) {
		List<Transaction> list = getInternal(form);
		

		Map<String,OcppTag> tagMap = ListTransform.transformToMap(tagRepo.findAll(),
				t -> t.getIdTag());

		Map<String,OcppChargeBox> boxMap = ListTransform.transformToMap(chargeBoxRepo.findAll(),
				c -> c.getChargeBoxId());
		
		List<de.rwth.idsg.steve.repository.dto.Transaction> ret=new ArrayList<>();
		for (Transaction t:list) {
			OcppChargeBox box = boxMap.get(t.getConnector().getChargeBoxId());
			if (box==null) {
				throw new IllegalStateException("Invalid charge box id: "+t.getConnector().getChargeBoxId());
			}
			
			OcppTag tag = tagMap.get(t.getOcppTag());
			if (tag==null) {
				throw new IllegalStateException("Invalid id tag: "+t.getOcppTag());
			}
			ret.add(toTransactionDto(t,box, tag));
		}
		return ret;
	}

	private de.rwth.idsg.steve.repository.dto.Transaction toTransactionDto(Transaction t, OcppChargeBox box, OcppTag tag) {
		return de.rwth.idsg.steve.repository.dto.Transaction.builder()
                .id(t.getTransactionPk())
                .chargeBoxId(t.getConnector().getChargeBoxId())
                .connectorId(t.getConnector().getConnectorId())
                .ocppIdTag(t.getOcppTag())
                .startTimestampDT(t.getStartTimestamp()!=null?new DateTime(t.getStartTimestamp()):null)
                .startTimestamp(DateTimeUtils.humanize(t.getStartTimestamp()!=null?new DateTime(t.getStartTimestamp()):null))
                .startValue(t.getStartValue())
                .stopTimestampDT(t.getStopTimestamp()!=null?new DateTime(t.getStopTimestamp()):null)
                .stopTimestamp(DateTimeUtils.humanize(t.getStopTimestamp()!=null?new DateTime(t.getStopTimestamp()):null))
                .stopValue(t.getStopValue())
                .chargeBoxPk(box.getChargeBoxPk())
                .ocppTagPk(tag.getOcppTagPk())
				.stopEventActor(t.getStopEventActor())
                .build();
	}

	@Override
	public TransactionDetails getDetails(int transactionPk, boolean firstArrivingMeterValueIfMultiple) {
		// -------------------------------------------------------------------------
        // Step 1: Collect general data about transaction
        // -------------------------------------------------------------------------

        TransactionQueryForm form = new TransactionQueryForm();
        form.setTransactionPk(transactionPk);
        form.setType(TransactionQueryForm.QueryType.ALL);
        form.setPeriodType(TransactionQueryForm.QueryPeriodType.ALL);

        List<Transaction>
                transactions = getInternal(form);

        if (transactions == null || transactions.isEmpty()) {
            throw new SteveException("There is no transaction with id '%s'", transactionPk);
        }
        
        Transaction transaction = transactions.get(0);

        Date startTimestamp = transaction.getStartTimestamp();
        Date stopTimestamp = transaction.getStopTimestamp();
        String stopValue = transaction.getStopValue();
        String chargeBoxId = transaction.getConnector().getChargeBoxId();
        int connectorId = transaction.getConnector().getConnectorId();
        
        OcppTag tag = tagRepo.findByIdTag(transaction.getOcppTag());
        if (tag==null) {
        	throw new IllegalStateException("Invalid id tag: "+transaction.getOcppTag());
        }
        
        OcppChargeBox box = chargeBoxRepo.findByChargeBoxId(transaction.getConnector().getChargeBoxId());
        if (box==null) {
        	throw new IllegalStateException("Invalid charge box id: "+transaction.getConnector().getChargeBoxId());
        }

        // -------------------------------------------------------------------------
        // Step 2: Collect intermediate meter values
        // -------------------------------------------------------------------------

		TransactionStart nextTx = null;

        // Case 1: Ideal and most accurate case. Station sends meter values with transaction id set.
        //
        List<ConnectorMeterValue> cmv1=connectorMeterValueRepo.findByTransactionPk(transaction.getTransactionPk());

        // Case 2: Fall back to filtering according to time windows
        //
        List<ConnectorMeterValue> cmv2=null;
        if (stopTimestamp == null && stopValue == null) {
			// https://github.com/RWTH-i5-IDSG/steve/issues/97
			//
			// handle "zombie" transaction, for which we did not receive any StopTransaction. if we do not handle it,
			// meter values for all subsequent transactions at this chargebox and connector will be falsely attributed
			// to this zombie transaction.
			//
			// "what is the subsequent transaction at the same chargebox and connector?"

			nextTx = transactionStartRepo.findNextTransactions(chargeBoxId, connectorId, startTimestamp,
					PageRequest.of(0, 1)).stream().findFirst().orElse(null);
			if (nextTx == null) {
				// the last active transaction
				cmv2 = connectorMeterValueRepo.findByChargeBoxIdAndConnectorIdAfter(chargeBoxId, connectorId, startTimestamp);
			} else {
				cmv2 = connectorMeterValueRepo.findByChargeBoxIdAndConnectorIdBetween(chargeBoxId,connectorId,startTimestamp,nextTx.getStartTimestamp());
			}
        } else {
            // finished transaction
            cmv2=connectorMeterValueRepo.findByChargeBoxIdAndConnectorIdBetween(chargeBoxId,connectorId,startTimestamp,stopTimestamp);
        }


        // Actually, either case 1 applies or 2. If we retrieved values using 1, case 2 is should not be
        // executed (best case). In worst case (1 returns empty list and we fall back to case 2) though,
        // we make two db calls. Alternatively, we can pass both queries in one go, and make the db work.
        //
        // UNION removes all duplicate records
        //
		List<ConnectorMeterValue> union=new ArrayList<>();
		union.addAll(cmv1);
		union.addAll(cmv2);

        // -------------------------------------------------------------------------
        // Step 3: Charging station might send meter vales at fixed intervals (e.g.
        // every 15 min) regardless of the fact that connector's meter value did not
        // change (e.g. vehicle is fully charged, but cable is still connected). This
        // yields multiple entries in db with the same value but different timestamp.
        // We are only interested in the first (or last) arriving entry.
        // -------------------------------------------------------------------------
        

        
        Collections.sort(union,new Comparator<ConnectorMeterValue>() {
			@Override
			public int compare(ConnectorMeterValue o1, ConnectorMeterValue o2) {
				if (firstArrivingMeterValueIfMultiple) {
					return o1.getValueTimestamp().compareTo(o2.getValueTimestamp());
				} else {
					return o2.getValueTimestamp().compareTo(o1.getValueTimestamp());
				}

			}
        });


        List<TransactionDetails.MeterValues> values=new ArrayList<>();
        Set<MeterValueKey> keys=new HashSet<>();
        for (ConnectorMeterValue v:union) {
        	MeterValueKey key=new MeterValueKey(v.getValue(), v.getReadingContext(), 
        			v.getFormat(), v.getMeasurand(), v.getLocation(), v.getUnit());
        	if (!keys.contains(key)) {
        		values.add(TransactionDetails.MeterValues.builder()
                        .valueTimestamp(new DateTime(v.getValueTimestamp()))
                        .value(v.getValue())
                        .readingContext(v.getReadingContext())
                        .format(v.getFormat())
                        .measurand(v.getMeasurand())
                        .location(v.getLocation())
                        .unit(v.getUnit())
						.phase(v.getPhase())
                        .build());
        	}
        }

        return new TransactionDetails(toTransactionDto(transaction, box, tag), values, nextTx);
	}

	private List<Transaction> getInternal(TransactionQueryForm form) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Transaction> cq = cb.createQuery(Transaction.class);
			Root<Transaction> root = cq.from(Transaction.class);
			cq.select(root);
			
			if (form.isTransactionPkSet()) {
				cq=cq.where(cb.equal(root.get("transactionPk"), form.getTransactionPk()));
	        }

	        if (form.isChargeBoxIdSet()) {
	        	cq=cq.where(cb.equal(root.get("connector").get("chargeBoxId"), form.getChargeBoxId()));
	        }

	        if (form.isOcppIdTagSet()) {
	        	cq=cq.where(cb.equal(root.get("ocppTag"), form.getOcppIdTag()));
	        }

	        if (form.getType() == TransactionQueryForm.QueryType.ACTIVE) {
	        	cq=cq.where(cb.isNull(root.get("stopTimestamp")));
	        }

	        Predicate typePredicate = getTypePredicate(cb, root, form);
			if (typePredicate!=null) {
				cq=cq.where(typePredicate);
			}

	        cq=cq.orderBy(cb.desc(root.get("transactionPk")));
			TypedQuery<Transaction> q = em.createQuery(cq);
			return q.getResultList();
			
		} finally {
			em.close();
		}
		
	}



	private Predicate getTypePredicate(CriteriaBuilder cb, Root<Transaction> root, TransactionQueryForm form) {
		Date now=new Date();

		switch (form.getPeriodType()) {
			case TODAY:

				return cb.between(root.get("startTimestamp"), CalendarUtils.getFirstMomentOfDay(now),
						CalendarUtils.getLastMomentOfDay(now));


			case LAST_10:
			case LAST_30:
			case LAST_90:
				return cb.between(root.get("startTimestamp"), CalendarUtils.createDaysBeforeNow(form.getPeriodType().getInterval()),
						now);

			case ALL:
				return null;

			case FROM_TO:
				return cb.between(root.get("startTimestamp"), form.getFrom().toDate(),
						form.getTo().toDate());


			default:
				throw new SteveException("Unknown enum type: "+form.getPeriodType());
		}
	}

	@Override
	public void writeTransactionsCSV(TransactionQueryForm form, Writer writer) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<String> getChargeBoxIdsOfActiveTransactions(String idTag) {
		return transactionRepo.findActiveChargeBoxIdsByOcppTag(idTag);
	}

	@Override
	public Optional<Transaction> findTransaction(int transactionPk) {
		return transactionRepo.findById(transactionPk);
	}

	@Override
	public long getActiveTransactionCountByIdTag(String idTag) {
		return transactionRepo.countActiveTransactionsByIdTag(idTag);
	}

}
