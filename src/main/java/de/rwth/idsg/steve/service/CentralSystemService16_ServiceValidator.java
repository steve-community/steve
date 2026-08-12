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

import jooq.steve.db.enums.TransactionStopEventActor;
import jooq.steve.db.tables.records.TransactionRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 17.02.2026
 */
@Slf4j
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

    public ValidationResults validateStatusNotification(@NotNull StatusNotificationRequest params) {
        var results = new ValidationResults(params);

        if (params.getConnectorId() < 0) {
            results.addHard("StatusNotification.connectorId must not be negative");
        }

        if (params.isSetTimestamp()) {
            long deltaMillis = operationalDelta.toMillis();
            if (params.getTimestamp().getMillis() > clock.instant().toEpochMilli() + deltaMillis) {
                results.addSoft("StatusNotification.timestamp is in the future");
            }
        }

        return logErrors(results);
    }

    public ValidationResults validateSecurityEvent(@NotNull SecurityEventNotification params) {
        var results = new ValidationResults(params);

        long deltaMillis = operationalDelta.toMillis();
        if (params.getTimestamp().getMillis() > clock.instant().toEpochMilli() + deltaMillis) {
            results.addSoft("SecurityEventNotification.timestamp is in the future");
        }

        return logErrors(results);
    }

    public ValidationResults validateStart(@NotNull StartTransactionRequest params) {
        var results = new ValidationResults(params);

        if (params.getConnectorId() < 1) {
            results.addHard("StartTransaction.connectorId must be positive");
        }

        if (params.getMeterStart() < 0) {
            results.addHard("StartTransaction.meterStart must not be negative");
        }

        if (params.getTimestamp().getMillis() > clock.instant().plus(operationalDelta).toEpochMilli()) {
            results.addSoft("StartTransaction.timestamp is in the future");
        }

        return logErrors(results);
    }

    public ValidationResults validateStop(TransactionRecord thisTx,
                                          @NotNull StopTransactionRequest stopParams,
                                          @NotNull BiFunction<String, String, Boolean> parentMatcher) {
        var results = new ValidationResults(stopParams);

        if (thisTx == null) {
            results.addHard("The transaction is not found in database");
            return logErrors(results);
        }

        boolean wasStopped = thisTx.getStopEventActor() == TransactionStopEventActor.station
            && thisTx.getStopValue() != null
            && thisTx.getStopTimestamp() != null;

        if (wasStopped) {
            results.addHard("The transaction was already stopped by the station");
        }

        if (thisTx.getStartTimestamp().isAfter(stopParams.getTimestamp())) {
            results.addHard("start.timestamp is after stop.timestamp");
        }

        if (stopParams.getTimestamp().getMillis() > clock.instant().plus(operationalDelta).toEpochMilli()) {
            results.addHard("stop.timestamp is in the future");
        }

        if (Integer.parseInt(thisTx.getStartValue()) > stopParams.getMeterStop()) {
            results.addHard("meterStart is greater than meterStop");
        }

        if (stopParams.isSetIdTag()
            && !Objects.equals(thisTx.getIdTag(), stopParams.getIdTag())
            && !parentMatcher.apply(stopParams.getIdTag(), thisTx.getIdTag())) {
            results.addHard("stop.idTag does not match the transaction's idTag or share its parentIdTag");
        }

        this.validateMeterValuesInternal(stopParams.getTransactionData(), thisTx.getStartTimestamp(), stopParams.getTimestamp(), results);
        return logErrors(results);
    }

    /**
     * Validation for MeterValues with transaction reference, i.e. a transaction must exist
     */
    public ValidationResults validateMeterValues(@NotNull MeterValuesRequest params, TransactionRecord thisTx) {
        var results = new ValidationResults(params);

        if (thisTx == null) {
            results.addHard("The transaction is not found in database");
            return logErrors(results);
        }

        boolean wasStopped = thisTx.getStopEventActor() == TransactionStopEventActor.station
            && thisTx.getStopValue() != null
            && thisTx.getStopTimestamp() != null;

        if (wasStopped) {
            results.addHard("The transaction was already stopped by the station");
        }

        if (params.getConnectorId() < 0) {
            results.addHard("MeterValues.connectorId must not be negative");
        }

        this.validateMeterValuesInternal(params.getMeterValue(), thisTx.getStartTimestamp(), null, results);
        return logErrors(results);
    }

    /**
     * Validation for MeterValues without any transaction reference
     */
    public ValidationResults validateMeterValues(@NotNull MeterValuesRequest params) {
        var results = new ValidationResults(params);

        if (params.getConnectorId() < 0) {
            results.addHard("MeterValues.connectorId must not be negative");
        }

        this.validateMeterValuesInternal(params.getMeterValue(), null, null, results);
        return logErrors(results);
    }

    private void validateMeterValuesInternal(List<MeterValue> meterValues,
                                             @Nullable DateTime startTimestamp,
                                             @Nullable DateTime stopTimestamp,
                                             ValidationResults results) {
        if (CollectionUtils.isEmpty(meterValues)) {
            return;
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
                results.addHard("MeterValue.timestamp is empty");
                return;
            }

            if (ts.isBefore(earliest)) earliest = ts;
            if (ts.isAfter(latest)) latest = ts;
        }

        if (earliest == MAX || latest == MIN) {
            results.addHard("MeterValue.timestamp is empty");
            return;
        }

        // allow operational delta tolerance for the following timestamp checks, since charge points
        // may have slight clock drift and meter values can be sampled a little bit later or before
        // our reference point.
        long deltaMillis = operationalDelta.toMillis();

        if (latest.getMillis() > clock.instant().toEpochMilli() + deltaMillis) {
            results.addSoft("at least one MeterValue.timestamp is in the future");
        }

        if (stopTimestamp != null) {
            if (latest.getMillis() > stopTimestamp.getMillis() + deltaMillis) {
                results.addSoft("at least one MeterValue.timestamp is after stop.timestamp");
            }
        }

        if (startTimestamp != null) {
            if (earliest.getMillis() < startTimestamp.getMillis() - deltaMillis) {
                results.addSoft("at least one MeterValue.timestamp is before start.timestamp");
            }
        }
    }

    private ValidationResults logErrors(ValidationResults results) {
        results.getSoftErrors().forEach(error ->
            log.warn("{} validation soft error: {}", results.messageName, error)
        );
        results.getHardErrors().forEach(error ->
            log.error("{} validation hard error: {}", results.messageName, error)
        );
        return results;
    }

    public static final class ValidationResults {

        private final String messageName;

        /**
         * Soft failures log a warning and continue normally.
         */
        private final List<String> softErrors = new ArrayList<>();

        /**
         * Hard failures log a warning and gate business processing.
         */
        private final List<String> hardErrors = new ArrayList<>();

        private ValidationResults(Object obj) {
            this.messageName = obj.getClass().getSimpleName();
        }

        private void addSoft(String errorMsg) {
            softErrors.add(errorMsg);
        }

        private void addHard(String errorMsg) {
            hardErrors.add(errorMsg);
        }

        public List<String> getSoftErrors() {
            return List.copyOf(softErrors);
        }

        public List<String> getHardErrors() {
            return List.copyOf(hardErrors);
        }

        public boolean hasHardErrors() {
            return !hardErrors.isEmpty();
        }

        public boolean isValid() {
            return softErrors.isEmpty() && hardErrors.isEmpty();
        }
    }
}
