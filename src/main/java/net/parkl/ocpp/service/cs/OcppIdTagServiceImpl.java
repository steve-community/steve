/*
 * Parkl Digital Technologies
 * Copyright (C) 2020-2021
 * All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.parkl.ocpp.service.cs;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.dto.OcppTag.Overview;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import de.rwth.idsg.steve.web.dto.OcppTagForm;
import de.rwth.idsg.steve.web.dto.OcppTagQueryForm;
import de.rwth.idsg.steve.web.dto.OcppTagQueryForm.BooleanType;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.OcppTag;
import net.parkl.ocpp.repositories.OcppTagRepository;
import net.parkl.ocpp.repositories.TransactionRepository;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;

@Service
@Slf4j
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
        Map<String, Long> tagsInTransaction = getTagsInTransaction();
        List<String> tags = tagRepo.findIdTagsActive(new Date());
        List<String> ret = new ArrayList<>();
        for (String tag : tags) {
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
            OcppTag tag = new OcppTag();
            tag.setIdTag(form.getIdTag());
            tag.setParentIdTag(form.getParentIdTag());
            if (form.getExpiration() != null) {
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
        for (String idTag : idList) {
            OcppTag tag = new OcppTag();
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

        Map<String, OcppTag> parentMap = getParentIdTagMap();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<OcppTag> cq = cb.createQuery(OcppTag.class);


            Root<OcppTag> root = cq.from(OcppTag.class);
            cq.select(root);
            if (form.isIdTagSet()) {
                cq = cq.where(cb.equal(root.get("idTag"), form.getIdTag()));
            }

            if (form.isParentIdTagSet()) {
                cq = cq.where(cb.equal(root.get("parentIdTag"), form.getParentIdTag()));
            }


            switch (form.getExpired()) {
                case ALL:
                    break;

                case TRUE:
                    cq = cq.where(cb.lessThanOrEqualTo(root.get("expiryDate"), new Date()));
                    break;

                case FALSE:
                    cq = cq.where(cb.or(cb.isNull(root.get("expiryDate")), cb.greaterThan(root.get("expiryDate"), new Date())));
                    break;

                default:
                    throw new SteveException("Unknown enum type");
            }

            if (form.getBlocked() != BooleanType.ALL) {
                if (form.getBlocked().getBoolValue()) {
                    cq = cq.where(cb.equal(root.get("maxActiveTransactionCount"), 0));
                } else {
                    cq = cq.where(cb.gt(root.get("maxActiveTransactionCount"), 0));
                }

            }

            cq = cq.orderBy(cb.asc(root.get("ocppTagPk")));
            TypedQuery<OcppTag> q = em.createQuery(cq);
            List<OcppTag> result = q.getResultList();

            List<Overview> ret = new ArrayList<>();
            for (OcppTag r : result) {
                OcppTag parent = null;
                if (r.getParentIdTag() != null) {
                    parent = parentMap.get(r.getParentIdTag());
                    if (parent == null) {
                        throw new IllegalStateException("Invalid parent ID tag: " + r.getParentIdTag());
                    }
                }

                if (form.getInTransaction() == BooleanType.ALL
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
        for (Object[] row : tagsInTransactionResult) {
            tagsInTransaction.put((String) row[0], (Long) row[1]);
        }
        return tagsInTransaction;
    }


    private Map<String, OcppTag> getParentIdTagMap() {
        Map<String, OcppTag> parentMap = new HashMap<>();
        Iterable<OcppTag> all = tagRepo.findAll();
        for (OcppTag tag : all) {
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
                orElseThrow(() -> new IllegalArgumentException("Invalid OCPP tag PK: " + form.getOcppTagPk()));

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

    @Override
    @Transactional
    public void addRfidTagIfNotExists(String rfidTag) {
        log.info("Finding OcppTag in database wiht rfid = {}", rfidTag);
        OcppTag tag = tagRepo.findByIdTag(rfidTag);
        if (tag == null) {
            OcppTag newTag = new OcppTag();
            newTag.setIdTag(rfidTag);
            tagRepo.save(newTag);
            log.info("Successfully added new Occp Tag with rfid {}", rfidTag);
        }
    }

    @Override
    @Transactional
    public void createTagWithoutActiveTransactionIfNotExists(String idTag) {
        OcppTag tag = tagRepo.findByIdTag(idTag);
        if (tag == null) {
            tag = new OcppTag();
            tag.setIdTag(idTag);
            tag.setMaxActiveTransactionCount(0);
            tagRepo.save(tag);
            log.warn("An unknown idTag '{}' was inserted into DB to prevent information loss and has been blocked",
                    idTag);
        }
    }

    @Override
    public List<OcppTag> findTags() {
        return tagRepo.findTags();
    }

    @Override
    public Map<String, OcppTag> getRfidTagOcppTagMap() {
        Iterable<OcppTag> tagsAll = tagRepo.findTags();
        Map<String, OcppTag> tagMap = new HashMap<>();
        for (OcppTag tag : tagsAll) {
            tagMap.put(tag.getIdTag(), tag);
        }
        return tagMap;
    }
}
