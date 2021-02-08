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
package net.parkl.ocpp.repositories;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.web.dto.TransactionQueryForm;
import net.parkl.ocpp.entities.Transaction;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;

import static net.parkl.ocpp.util.CalendarUtils.*;

@Repository
public class TransactionCriteriaRepository {

    @PersistenceContext
    public EntityManager entityManager;

    public List<Transaction> getInternal(TransactionQueryForm form) {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Transaction> cq = cb.createQuery(Transaction.class);
            Root<Transaction> root = cq.from(Transaction.class);
            cq.select(root);

            if (form.isTransactionPkSet()) {
                cq = cq.where(cb.equal(root.get("transactionPk"), form.getTransactionPk()));
            }

            if (form.isChargeBoxIdSet()) {
                cq = cq.where(cb.equal(root.get("connector").get("chargeBoxId"), form.getChargeBoxId()));
            }

            if (form.isOcppIdTagSet()) {
                cq = cq.where(cb.equal(root.get("ocppTag"), form.getOcppIdTag()));
            }

            if (form.getType() == TransactionQueryForm.QueryType.ACTIVE) {
                cq = cq.where(cb.isNull(root.get("stopTimestamp")));
            }

            Predicate typePredicate = getTypePredicate(cb, root, form);
            if (typePredicate != null) {
                cq = cq.where(typePredicate);
            }

            cq = cq.orderBy(cb.desc(root.get("transactionPk")));
            TypedQuery<Transaction> q = entityManager.createQuery(cq);
            return q.getResultList();
        } finally {
            entityManager.close();
        }
    }

    private Predicate getTypePredicate(CriteriaBuilder cb, Root<Transaction> root, TransactionQueryForm form) {
        Date now = new Date();

        switch (form.getPeriodType()) {
            case TODAY:
                return cb.between(root.get("startTimestamp"), getFirstMomentOfDay(now), getLastMomentOfDay(now));
            case LAST_10:
            case LAST_30:
            case LAST_90:
                return cb.between(root.get("startTimestamp"), createDaysBeforeNow(form.getPeriodType().getInterval()),
                        now);
            case ALL:
                return null;
            case FROM_TO:
                return cb.between(root.get("startTimestamp"), form.getFrom().toDate(),
                        form.getTo().toDate());
            default:
                throw new SteveException("Unknown enum type: " + form.getPeriodType());
        }
    }
}
