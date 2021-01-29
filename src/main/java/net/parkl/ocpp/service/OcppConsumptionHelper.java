package net.parkl.ocpp.service;

import net.parkl.ocpp.entities.Connector;
import net.parkl.ocpp.entities.Transaction;
import net.parkl.ocpp.repositories.TransactionRepository;
import net.parkl.ocpp.service.config.AdvancedChargeBoxConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.Date;


@Component
public class OcppConsumptionHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(OcppConsumptionHelper.class);

    @Autowired
    private AdvancedChargeBoxConfiguration config;

    @Autowired
    private TransactionRepository transactionRepo;

    @PersistenceContext
    private EntityManager em;

    public float getTotalPower(Transaction t) {
        float ret = 0f;
        if (t != null && t.getStopValue() != null) {
            float stopVal = Float.parseFloat(t.getStopValue());
            if (t.getStartValue() != null) {
                float startVal = Float.parseFloat(t.getStartValue());
                ret = (stopVal - startVal) / 1000f;
            } else {
                ret = stopVal / 1000f;
            }
            LOGGER.info("Total power at the end of charging: {}", ret);
        } else {
            LOGGER.warn("Stop value not found at the end of charging");
        }
        return ret;
    }

    public Float getStartValue(Transaction t) {
        if (t == null) {
            return null;
        }

        if (isTransactionPartialForChargeBox(t.getConnector().getChargeBoxId())) {
            if (t.getStartValue() != null) {
                float sum = sumStopValueByConnectorBefore(t.getConnector(), t.getStartTimestamp() != null ? t.getStartTimestamp() : new Date());
                return sum / 1000f;
            }
            return null;
        } else {
            if (t.getStartValue() != null) {
                return Float.parseFloat(t.getStartValue()) / 1000f;
            }
            return null;
        }
    }

    public Float getStopValue(Transaction t) {
        if (t == null) {
            return null;
        }

        if (isTransactionPartialForChargeBox(t.getConnector().getChargeBoxId())) {
            if (t.getStopValue() != null) {
                float sum = sumStopValueByConnectorBefore(t.getConnector(), t.getStopTimestamp() != null ? t.getStopTimestamp() : new Date());
                return (sum + Float.parseFloat(t.getStopValue())) / 1000f;
            }
            return null;
        } else {
            if (t.getStopValue() != null) {
                return Float.parseFloat(t.getStopValue()) / 1000f;
            }
            return null;
        }
    }

    private float sumStopValueByConnectorBefore(Connector connector, Date date) {
        BigDecimal res = (BigDecimal) em.createNativeQuery(
                "select sum(cast(stop_value as decimal(10,2))) from ocpp_transaction_stop as stop inner join ocpp_transaction_start as start on stop.transaction_pk=start.transaction_pk where connector_pk=? and stop_timestamp<?").
                setParameter(1, connector.getConnectorPk()).setParameter(2, date).getSingleResult();
        if (res == null) {
            return 0f;
        }
        return res.floatValue();
    }

    private boolean isTransactionPartialForChargeBox(String chargeBoxId) {
        return config.isTransactionPartialEnabled(chargeBoxId);

    }


    public static float getKwValue(float val, String unit) {
        if (unit != null && unit.equals(OcppConstants.UNIT_W)) {
            return val / 1000f;
        }
        return val;
    }

    public static float getKwhValue(float val, String unit) {
        if (unit != null && unit.equals(OcppConstants.UNIT_WH)) {
            return val / 1000f;
        }
        return val;

    }

}
