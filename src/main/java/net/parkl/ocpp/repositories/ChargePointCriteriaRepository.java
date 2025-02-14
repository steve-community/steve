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
import de.rwth.idsg.steve.web.dto.ChargePointQueryForm;
import net.parkl.ocpp.entities.OcppChargeBox;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Repository
public class ChargePointCriteriaRepository {
    @PersistenceContext
    public EntityManager entityManager;

    public List<OcppChargeBox> getOverviewInternal(ChargePointQueryForm form) {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<OcppChargeBox> cq = cb.createQuery(OcppChargeBox.class);
            Root<OcppChargeBox> root = cq.from(OcppChargeBox.class);
            cq.select(root);
            if (form.isSetOcppVersion()) {
                cq = cq.where(cb.like(root.get("ocppProtocol"), form.getOcppVersion().getValue() + "%"));
            }

            if (form.isSetDescription()) {
                cq = cq.where(cb.like(root.get("description"), "%" + form.getDescription() + "%"));
            }

            if (form.isSetChargeBoxId()) {
                cq = cq.where(cb.like(root.get("chargeBoxId"), "%" + form.getChargeBoxId() + "%"));
            }

            DateTime now = DateTime.now();
            switch (form.getHeartbeatPeriod()) {
                case ALL:
                    break;

                case TODAY:
                    cq = cq.where(cb.between(root.get("lastHeartbeatTimestamp"), getDayStart(now), getDayEnd(now)));
                    break;

                case YESTERDAY:
                    cq = cq.where(cb.between(root.get("lastHeartbeatTimestamp"), getDayStart(now.minusDays(1)), getDayEnd(now.minusDays(1))));
                    break;

                case EARLIER:
                    cq = cq.where(cb.lessThan(root.get("lastHeartbeatTimestamp"), getDayStart(now.minusDays(1))));

                    break;

                default:
                    throw new SteveException("Unknown enum type");
            }

            cq = cq.orderBy(cb.asc(root.get("chargeBoxPk")));
            TypedQuery<OcppChargeBox> q = entityManager.createQuery(cq);
            return q.getResultList();
        } finally {
            entityManager.close();
        }
    }

    private LocalDateTime getDayStart(DateTime now) {
        LocalDateTime ldt = LocalDateTime.ofInstant(now.toDate().toInstant(), ZoneId.systemDefault());
        return ldt.withHour(0).withMinute(0).withSecond(0).withNano(0);
    }
    private LocalDateTime getDayEnd(DateTime now) {
        LocalDateTime ldt = LocalDateTime.ofInstant(now.toDate().toInstant(), ZoneId.systemDefault());
        return ldt.withHour(23).withMinute(59).withSecond(59).withNano(999_000_000);
    }
}
