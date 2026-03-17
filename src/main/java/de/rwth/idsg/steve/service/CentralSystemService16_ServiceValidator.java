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
import ocpp.cs._2015._10.MeterValue;
import ocpp.cs._2015._10.MeterValuesRequest;
import ocpp.cs._2015._10.StartTransactionRequest;
import ocpp.cs._2015._10.StopTransactionRequest;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Clock;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 17.02.2026
 */
@Service
@RequiredArgsConstructor
public class CentralSystemService16_ServiceValidator {

    private final Clock clock;
    private final Duration operationalDeltaForNow;

    @Autowired
    public CentralSystemService16_ServiceValidator(Clock clock) {
        this(clock, Duration.ofMinutes(5));
    }

    public SteveException validateStart(StartTransactionRequest params) {
        if (params.getConnectorId() < 1) {
            return new SteveException("StartTransaction.connectorId must be positive");
        }

        if (params.getMeterStart() < 0) {
            return new SteveException("StartTransaction.meterStart must not be negative");
        }

        if (params.getTimestamp().getMillis() > clock.instant().plus(operationalDeltaForNow).toEpochMilli()) {
            return new SteveException("StartTransaction.timestamp is in the future");
        }

        return null;
    }

    public SteveException validateStop(TransactionRecord thisTx, StopTransactionRequest stopParams) {
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

        if (stopParams.getTimestamp().getMillis() > clock.instant().plus(operationalDeltaForNow).toEpochMilli()) {
            return new SteveException("stop.timestamp is in the future");
        }

        if (Integer.parseInt(thisTx.getStartValue()) > stopParams.getMeterStop()) {
            return new SteveException("meterStart is greater than meterStop");
        }

        return this.validateMeterValuesInternal(stopParams.getTransactionData(), thisTx.getStartTimestamp(), stopParams.getTimestamp());
    }

    public SteveException validateMeterValues(MeterValuesRequest params) {
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

        List<DateTime> timestamps = meterValues.stream()
            .map(MeterValue::getTimestamp)
            .filter(java.util.Objects::nonNull)
            .toList();

        // should not happen because of @NotNull
        if (timestamps.isEmpty()) {
            return new SteveException("MeterValue.timestamp is empty");
        }

        DateTime latest = timestamps.stream().max(Comparator.naturalOrder()).get();

        if (latest.getMillis() > clock.instant().plus(operationalDeltaForNow).toEpochMilli()) {
            return new SteveException("at least one MeterValue.timestamp is in the future");
        }

        if (stopTimestamp != null && latest.isAfter(stopTimestamp)) {
            return new SteveException("at least one MeterValue.timestamp is after stop.timestamp");
        }

        if (startTimestamp != null) {
            DateTime earliest = timestamps.stream().min(Comparator.naturalOrder()).get();
            if (earliest.isBefore(startTimestamp)) {
                return new SteveException("at least one MeterValue.timestamp is before start.timestamp");
            }
        }

        // check timestamp monotonicity: timestamps should be non-decreasing
        for (int i = 1; i < timestamps.size(); i++) {
            if (timestamps.get(i).isBefore(timestamps.get(i - 1))) {
                return new SteveException("MeterValue timestamps are not in chronological order");
            }
        }

        return null;
    }
}
