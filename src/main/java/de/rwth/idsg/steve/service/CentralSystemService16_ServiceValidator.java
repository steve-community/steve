/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
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
package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.SteveException;
import jooq.steve.db.enums.TransactionStopEventActor;
import jooq.steve.db.tables.records.TransactionRecord;
import lombok.RequiredArgsConstructor;
import ocpp._2022._02.security.SecurityEventNotification;
import ocpp.cs._2015._10.MeterValue;
import ocpp.cs._2015._10.MeterValuesRequest;
import ocpp.cs._2015._10.StartTransactionRequest;
import ocpp.cs._2015._10.StatusNotificationRequest;
import ocpp.cs._2015._10.StopTransactionRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Clock;
import java.time.Duration;
import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 17.02.2026
 */
@Service
@RequiredArgsConstructor
public class CentralSystemService16_ServiceValidator {

    private static final DateTime MIN = new DateTime(0);
    private static final DateTime MAX = new DateTime(Long.MAX_VALUE);

    private final Clock clock;
    private final Duration operationalDelta;

    @Autowired
    public CentralSystemService16_ServiceValidator(Clock clock) {
        this(clock, Duration.ofMinutes(5));
    }

    public SteveException validateStatusNotification(@NotNull StatusNotificationRequest params) {
        if (params.getConnectorId() < 0) {
            return new SteveException("StatusNotification.connectorId must not be negative");
        }

        if (params.isSetTimestamp()) {
            long deltaMillis = operationalDelta.toMillis();
            if (params.getTimestamp().getMillis() > clock.instant().toEpochMilli() + deltaMillis) {
                return new SteveException("StatusNotification.timestamp is in the future");
            }
        }

        return null;
    }

    public SteveException validateSecurityEvent(@NotNull SecurityEventNotification params) {
        long deltaMillis = operationalDelta.toMillis();
        if (params.getTimestamp().getMillis() > clock.instant().toEpochMilli() + deltaMillis) {
            return new SteveException("SecurityEventNotification.timestamp is in the future");
        }

        return null;
    }

    public SteveException validateStart(@NotNull StartTransactionRequest params) {
        if (params.getConnectorId() < 1) {
            return new SteveException("StartTransaction.connectorId must be positive");
        }

        if (params.getMeterStart() < 0) {
            return new SteveException("StartTransaction.meterStart must not be negative");
        }

        if (params.getTimestamp().getMillis() > clock.instant().plus(operationalDelta).toEpochMilli()) {
            return new SteveException("StartTransaction.timestamp is in the future");
        }

        return null;
    }

    public SteveException validateStop(TransactionRecord thisTx, @NotNull StopTransactionRequest stopParams) {
        if (thisTx == null) {
            return new SteveException("The transaction is not found in database");
        }

        boolean wasStopped = thisTx.getStopEventActor() == TransactionStopEventActor.station
            && thisTx.getStopValue() != null
            && thisTx.getStopTimestamp() != null;

        if (wasStopped) {
            return new SteveException("The transaction was already stopped by the station");
        }

        if (thisTx.getStartTimestamp().isAfter(stopParams.getTimestamp())) {
            return new SteveException("start.timestamp is after stop.timestamp");
        }

        if (stopParams.getTimestamp().getMillis() > clock.instant().plus(operationalDelta).toEpochMilli()) {
            return new SteveException("stop.timestamp is in the future");
        }

        if (Integer.parseInt(thisTx.getStartValue()) > stopParams.getMeterStop()) {
            return new SteveException("meterStart is greater than meterStop");
        }

        return this.validateMeterValuesInternal(stopParams.getTransactionData(), thisTx.getStartTimestamp(), stopParams.getTimestamp());
    }

    public SteveException validateMeterValues(@NotNull MeterValuesRequest params) {
        if (params.getConnectorId() < 0) {
            return new SteveException("MeterValues.connectorId must not be negative");
        }

        return this.validateMeterValuesInternal(params.getMeterValue(), null, null);
    }

    private SteveException validateMeterValuesInternal(List<MeterValue> meterValues,
                                                       @Nullable DateTime startTimestamp,
                                                       @Nullable DateTime stopTimestamp) {
        if (CollectionUtils.isEmpty(meterValues)) {
            return null;
        }

        DateTime earliest = MAX;
        DateTime latest = MIN;

        // single pass: track earliest and latest
        for (MeterValue mv : meterValues) {
            if (mv == null) {
                continue;
            }

            DateTime ts = mv.getTimestamp();

            // should not happen because of @NotNull
            if (ts == null) {
                return new SteveException("MeterValue.timestamp is empty");
            }

            if (ts.isBefore(earliest)) earliest = ts;
            if (ts.isAfter(latest))  latest = ts;
        }

        if (earliest == MAX || latest == MIN) {
            return new SteveException("MeterValue.timestamp is empty");
        }

        // allow operational delta tolerance for the following timestamp checks, since charge points
        // may have slight clock drift and meter values can be sampled a little bit later or before
        // our reference point.
        long deltaMillis = operationalDelta.toMillis();

        if (latest.getMillis() > clock.instant().toEpochMilli() + deltaMillis) {
            return new SteveException("at least one MeterValue.timestamp is in the future");
        }

        if (stopTimestamp != null) {
            if (latest.getMillis() > stopTimestamp.getMillis() + deltaMillis) {
                return new SteveException("at least one MeterValue.timestamp is after stop.timestamp");
            }
        }

        if (startTimestamp != null) {
            if (earliest.getMillis() < startTimestamp.getMillis() - deltaMillis) {
                return new SteveException("at least one MeterValue.timestamp is before start.timestamp");
            }
        }

        return null;
    }
}
