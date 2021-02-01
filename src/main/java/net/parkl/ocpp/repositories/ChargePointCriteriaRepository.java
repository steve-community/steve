package net.parkl.ocpp.repositories;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.web.dto.ChargePointQueryForm;
import net.parkl.ocpp.entities.OcppChargeBox;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Date;
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

    private Date getDayStart(DateTime now) {
        return now.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).toDate();
    }

    private Date getDayEnd(DateTime now) {
        return now.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).withMillisOfSecond(999).toDate();
    }
}
