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
package net.parkl.ocpp.service;

import net.parkl.ocpp.entities.Connector;
import net.parkl.ocpp.entities.Transaction;
import net.parkl.ocpp.service.config.AdvancedChargeBoxConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;


@Component
public class OcppConsumptionHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(OcppConsumptionHelper.class);

    @Autowired
    private AdvancedChargeBoxConfiguration config;

    @PersistenceContext
    private EntityManager em;

    public float getTotalPower(Transaction t) {
       return getTotalPower(t.getStartValue(), t.getStopValue());
    }

    public float getTotalPower(String startValue, String stopValue) {
        float ret = 0f;
        if (stopValue != null) {
            float stopVal = Float.parseFloat(stopValue);
            if (startValue != null) {
                float startVal = Float.parseFloat(startValue);
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
                float sum = sumStopValueByConnectorBefore(t.getConnector(), t.getStartTimestamp() != null ? t.getStartTimestamp() : LocalDateTime.now());
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
                float sum = sumStopValueByConnectorBefore(t.getConnector(), t.getStopTimestamp() != null ? t.getStopTimestamp() : LocalDateTime.now());
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

    private float sumStopValueByConnectorBefore(Connector connector, LocalDateTime dateTime) {
        BigDecimal res = (BigDecimal) em.createNativeQuery(
                        "select sum(cast(stop_value as decimal(10,2))) from ocpp_transaction_stop as stop inner join ocpp_transaction_start as start on stop.transaction_pk=start.transaction_pk where connector_pk=?1 and stop_timestamp<?2")
                .setParameter(1, connector.getConnectorPk())
                .setParameter(2, dateTime)
                .getSingleResult();
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
