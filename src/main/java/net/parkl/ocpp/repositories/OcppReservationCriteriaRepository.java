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
import de.rwth.idsg.steve.repository.dto.Reservation;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import de.rwth.idsg.steve.web.dto.ReservationQueryForm;
import net.parkl.ocpp.entities.OcppChargeBox;
import net.parkl.ocpp.entities.OcppReservation;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class OcppReservationCriteriaRepository {

    @PersistenceContext
    public EntityManager entityManager;

    public List<Reservation> getReservations(ReservationQueryForm form, Map<String, OcppChargeBox> boxMap) {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<OcppReservation> cq = cb.createQuery(OcppReservation.class);
            Root<OcppReservation> root = cq.from(OcppReservation.class);
            cq.select(root);
            if (form.isChargeBoxIdSet()) {
                cq = cq.where(cb.equal(root.get("connector").get("chargeBoxId"), form.getChargeBoxId()));
            }

            if (form.isOcppIdTagSet()) {
                cq = cq.where(cb.equal(root.get("ocppTag"), form.getOcppIdTag()));
            }

            if (form.isStatusSet()) {
                cq = cq.where(cb.equal(root.get("status"), form.getStatus().name()));
            }

            switch (form.getPeriodType()) {
                case ACTIVE:
                    cq = cq.where(cb.greaterThan(root.get("expiryDatetime"), new Date()));
                    break;

                case FROM_TO:
                    cq = cq.where(cb.and(cb.greaterThanOrEqualTo(root.get("startDatetime"), form.getFrom().toDate()),
                            cb.lessThanOrEqualTo(root.get("expiryDatetime"), form.getTo().toDate())
                    ));
                    break;

                default:
                    throw new SteveException("Unknown enum type");
            }


            cq = cq.orderBy(cb.asc(root.get("expiryDatetime")));
            TypedQuery<OcppReservation> q = entityManager.createQuery(cq);
            List<OcppReservation> result = q.getResultList();


            List<Reservation> ret = new ArrayList<>();
            for (OcppReservation r : result) {

                OcppChargeBox box = boxMap.get(r.getConnector().getChargeBoxId());
                if (box == null) {
                    throw new IllegalStateException("Invalid charge box id: " + r.getConnector().getChargeBoxId());
                }

                ret.add(Reservation.builder()
                        .id(r.getReservationPk())
                        .transactionId(r.getTransaction() != null ? r.getTransaction().getTransactionPk() : null)
                        .chargeBoxPk(box.getChargeBoxPk())
                        .ocppIdTag(r.getOcppTag())
                        .chargeBoxId(r.getConnector().getChargeBoxId())
                        .startDatetimeDT(r.getStartDatetime() != null ? new DateTime(r.getStartDatetime()) : null)
                        .startDatetime(DateTimeUtils.humanize(r.getStartDatetime() != null ? new DateTime(r.getStartDatetime()) : null))
                        .expiryDatetimeDT(r.getExpiryDatetime() != null ? new DateTime(r.getExpiryDatetime()) : null)
                        .expiryDatetime(DateTimeUtils.humanize(r.getExpiryDatetime() != null ? new DateTime(r.getExpiryDatetime()) : null))
                        .status(r.getStatus())
                        .connectorId(r.getConnector().getConnectorId())
                        .build());
            }
            return ret;
        } finally {
            entityManager.close();
        }
    }
}
