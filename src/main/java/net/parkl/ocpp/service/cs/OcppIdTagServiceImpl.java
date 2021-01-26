package net.parkl.ocpp.service.cs;

import java.sql.SQLIntegrityConstraintViolationException;
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

import net.parkl.ocpp.entities.OcppTag;
import net.parkl.ocpp.repositories.OcppTagRepository;
import net.parkl.ocpp.repositories.TransactionRepository;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.dto.OcppTag.Overview;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import de.rwth.idsg.steve.web.dto.OcppTagForm;
import de.rwth.idsg.steve.web.dto.OcppTagQueryForm;
import de.rwth.idsg.steve.web.dto.OcppTagQueryForm.BooleanType;

@Service
public class OcppIdTagServiceImpl implements OcppIdTagService {
	@Autowired
	private OcppTagRepository tagRepo;
	@PersistenceContext
	private EntityManager em;

	@Autowired
	private TransactionRepository transactionRepository;

	@Override
	public String getParentIdtag(String idTag) {
		return tagRepo.findParentIdTagByIdTag(idTag);
	}

	@Override
	public List<OcppTag> getRecords() {
		return tagRepo.findAllByOrderByOcppTagPkAsc();
	}

	@Override
	public List<OcppTag> getRecords(List<String> idTagList) {
		return tagRepo.findByIdTagInOrderByOcppTagPkAsc(idTagList);
	}

	@Override
	public OcppTag getRecord(String idTag) {
		return tagRepo.findByIdTag(idTag);
	}

	@Override
	public List<String> getActiveIdTags() {
	    Map<String,Long> tagsInTransaction = getTagsInTransaction();
		List<String> tags = tagRepo.findIdTagsActive(new Date());
		List<String> ret=new ArrayList<>();
		for (String tag:tags) {
		    if (!tagsInTransaction.containsKey(tag)) {
		        ret.add(tag);
            }
        }
		return ret;
	}

	@Override
	public List<String> getIdTags() {
		return tagRepo.findIdTagsAll();
	}

	@Override
	public OcppTag getRecord(int ocppTagPk) {
		return tagRepo.findById(ocppTagPk).orElse(null);
	}

	@Override
	@Transactional
	public void addOcppTag(OcppTagForm form) {
		try {
			OcppTag tag=new OcppTag();
			tag.setIdTag(form.getIdTag());
			tag.setParentIdTag(form.getParentIdTag());
			if (form.getExpiration()!=null) {
				tag.setExpiryDate(form.getExpiration().toDate());
			}
			tag.setNote(form.getNote());
			tag.setMaxActiveTransactionCount(form.getMaxActiveTransactionCount());
			tagRepo.save(tag);
		} catch (Exception e) {
            if (e.getCause() instanceof SQLIntegrityConstraintViolationException) {
                throw new SteveException("A user with idTag '%s' already exists.", form.getIdTag());
            } else {
                throw new SteveException("Execution of addOcppTag for idTag '%s' FAILED.", form.getIdTag(), e);
            }
        }
	}

	@Override
	@Transactional
	public void addOcppTagList(List<String> idList) {
		for (String idTag:idList) {
			OcppTag tag=new OcppTag();
			tag.setIdTag(idTag);
			tagRepo.save(tag);
		}
		
	}

	@Override
	public List<String> getParentIdTags() {
		return tagRepo.findParentIdTags();
	}

	@Override
	public List<Overview> getOverview(OcppTagQueryForm form) {
		Map<String, Long> tagsInTransaction = getTagsInTransaction();

		Map<String,OcppTag> parentMap=getParentIdTagMap();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<OcppTag> cq = cb.createQuery(OcppTag.class);


			Root<OcppTag> root = cq.from(OcppTag.class);
			cq.select(root);
			if (form.isIdTagSet()) {
				 cq=cq.where(cb.equal(root.get("idTag"), form.getIdTag()));
			}
			
			if (form.isParentIdTagSet()) {
				 cq=cq.where(cb.equal(root.get("parentIdTag"), form.getParentIdTag()));
			}
			

	        switch (form.getExpired()) {
	            case ALL:
	                break;

	            case TRUE:
	            	cq=cq.where(cb.lessThanOrEqualTo(root.get("expiryDate"), new Date()));
	                break;

	            case FALSE:
	            	cq=cq.where(cb.or(cb.isNull(root.get("expiryDate")),cb.greaterThan(root.get("expiryDate"), new Date())));
	                break;

	            default:
	                throw new SteveException("Unknown enum type");
	        }

	       /* if (form.getInTransaction()!=BooleanType.ALL) {
	        	cq=cq.where(cb.equal(root.get("inTransaction"), form.getInTransaction().getBoolValue()));
	        }*/
	        if (form.getBlocked()!=BooleanType.ALL) {
	        	if (form.getBlocked().getBoolValue()) {
					cq=cq.where(cb.equal(root.get("maxActiveTransactionCount"), 0));
				} else {
					cq=cq.where(cb.gt(root.get("maxActiveTransactionCount"), 0));
				}

	        }
	     
			
			
			cq=cq.orderBy(cb.asc(root.get("ocppTagPk")));
			TypedQuery<OcppTag> q = em.createQuery(cq);
			List<OcppTag> result = q.getResultList();
			

			List<Overview> ret=new ArrayList<>();
			for (OcppTag r:result) {
				OcppTag parent=null;
				if (r.getParentIdTag()!=null) {
					parent=parentMap.get(r.getParentIdTag());
					if (parent==null) {
						throw new IllegalStateException("Invalid parent ID tag: "+r.getParentIdTag());
					}
				}

				if (form.getInTransaction()==BooleanType.ALL
					|| filterInTransaction(tagsInTransaction, r.getIdTag(), form.getInTransaction().getBoolValue())) {
					ret.add(Overview.builder()
							.ocppTagPk(r.getOcppTagPk())
							.parentOcppTagPk(parent != null ? parent.getOcppTagPk() : null)
							.idTag(r.getIdTag())
							.parentIdTag(r.getParentIdTag())
							.expiryDateDT(r.getExpiryDate() != null ? new DateTime(r.getExpiryDate()) : null)
							.expiryDate(DateTimeUtils.humanize(r.getExpiryDate() != null ? new DateTime(r.getExpiryDate()) : null))
							.inTransaction(tagsInTransaction.containsKey(r.getIdTag()) &&
									tagsInTransaction.get(r.getIdTag()) > 0)
							.blocked(r.getMaxActiveTransactionCount() == 0)
							.build());
				}
			}
			return ret;
		} finally { 
			em.close();
		}
		
	}

	private boolean filterInTransaction(Map<String, Long> tagsInTransaction, String idTag, boolean inTransaction) {
		if (inTransaction) {
			return tagsInTransaction.containsKey(idTag) &&
					tagsInTransaction.get(idTag) > 0;
		} else {
			return !tagsInTransaction.containsKey(idTag) ||
					tagsInTransaction.get(idTag) == 0;
		}
	}

	private Map<String, Long> getTagsInTransaction() {
		List<Object[]> tagsInTransactionResult = transactionRepository.findIdTagsInTransaction();
		Map<String, Long> tagsInTransaction = new HashMap<>();
		for (Object[] row:tagsInTransactionResult) {
			tagsInTransaction.put((String)row[0], (Long)row[1]);
		}
		return tagsInTransaction;
	}


	private Map<String, OcppTag> getParentIdTagMap() {
		Map<String,OcppTag> parentMap=new HashMap<>();
		Iterable<OcppTag> all=tagRepo.findAll();
		for (OcppTag tag:all) {
			parentMap.put(tag.getIdTag(), tag);
		}
		return parentMap;
	}

	@Override
	@Transactional
	public void deleteOcppTag(int ocppTagPk) {
		try {
			tagRepo.deleteById(ocppTagPk);
		} catch (Exception e) {
            throw new SteveException("Execution of deleteOcppTag for idTag FAILED.", e);
        }
	}

	@Override
	@Transactional
	public void updateOcppTag(OcppTagForm form) {
		OcppTag tag = tagRepo.findById(form.getOcppTagPk()).
				orElseThrow(() -> new IllegalArgumentException("Invalid OCPP tag PK: "+form.getOcppTagPk()));
		
		try {
			tag.setParentIdTag(form.getParentIdTag());
			tag.setExpiryDate(form.getExpiration().toDate());
			tag.setNote(form.getNote());
			tag.setMaxActiveTransactionCount(form.getMaxActiveTransactionCount());
			tagRepo.save(tag);
		} catch (Exception e) {
            throw new SteveException("Execution of updateOcppTag for idTag '%s' FAILED.", form.getIdTag(), e);
        }
	}

}
