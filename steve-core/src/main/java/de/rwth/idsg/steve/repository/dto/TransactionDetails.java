/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2025 SteVe Community Team
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
package de.rwth.idsg.steve.repository.dto;

import de.rwth.idsg.steve.utils.TransactionStopServiceHelper;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ocpp.cs._2012._06.UnitOfMeasure;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

import static de.rwth.idsg.steve.utils.TransactionStopServiceHelper.floatingStringToIntString;
import static de.rwth.idsg.steve.utils.TransactionStopServiceHelper.kWhStringToWhString;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 27.04.2016
 */
@Getter
@RequiredArgsConstructor
public class TransactionDetails {
    private final Transaction transaction;
    private final List<MeterValues> values;

    /**
     * Subsequent transaction's start event (to the transaction that we give details about),
     * that is at the same chargebox and connector
     */
    private final @Nullable NextTransactionStart nextTransactionStart;

    @Getter
    @Builder
    public static class MeterValues {
        private final @Nullable Instant valueTimestamp;
        private final String value, readingContext, format, measurand, location, unit;

        // New in OCPP 1.6
        private final String phase;
    }

    @Getter
    @Builder
    public static class NextTransactionStart {
        private final String startValue;
        private final Instant startTimestamp;
    }

    public TransactionDetails.@Nullable MeterValues findLastMeterValue() {
        var v = values.stream()
                .filter(TransactionStopServiceHelper::isEnergyValue)
                .max(Comparator.comparing(TransactionDetails.MeterValues::getValueTimestamp))
                .orElse(null);

        // if the list of values is empty, we fall to this case, as well.
        if (v == null) {
            return null;
        }

        // convert kWh to Wh
        if (UnitOfMeasure.K_WH.value().equals(v.getUnit())) {
            return TransactionDetails.MeterValues.builder()
                    .value(kWhStringToWhString(v.getValue()))
                    .valueTimestamp(v.getValueTimestamp())
                    .readingContext(v.getReadingContext())
                    .format(v.getFormat())
                    .measurand(v.getMeasurand())
                    .location(v.getLocation())
                    .unit(UnitOfMeasure.WH.value())
                    .phase(v.getPhase())
                    .build();
        } else {
            return v;
        }
    }

    @Builder
    @Getter
    public static class TerminationValues {
        private final String stopValue;
        private final Instant stopTimestamp;
    }

    public TerminationValues findNeededValues() {

        // -------------------------------------------------------------------------
        // 1. intermediate meter values have priority (most accurate data)
        // -------------------------------------------------------------------------

        var last = findLastMeterValue();
        if (last != null) {
            return TerminationValues.builder()
                    .stopValue(floatingStringToIntString(last.getValue()))
                    .stopTimestamp(last.getValueTimestamp())
                    .build();
        }

        // -------------------------------------------------------------------------
        // 2. a latest energy meter value does not exist, use data of next tx
        // -------------------------------------------------------------------------

        if (nextTransactionStart != null) {
            // some charging stations do not reset the meter value counter after each transaction and
            // continue counting. in such cases, use the value of subsequent transaction's start value
            if (Integer.parseInt(nextTransactionStart.getStartValue())
                    > Integer.parseInt(transaction.getStartValue())) {
                return TerminationValues.builder()
                        .stopValue(nextTransactionStart.getStartValue())
                        .stopTimestamp(nextTransactionStart.getStartTimestamp())
                        .build();
            } else {
                // this mix of strategies might be really confusing
                return TerminationValues.builder()
                        .stopValue(transaction.getStartValue())
                        .stopTimestamp(nextTransactionStart.getStartTimestamp())
                        .build();
            }
        }

        // -------------------------------------------------------------------------
        // 3. neither meter values nor next tx exist, use start values
        // -------------------------------------------------------------------------

        return TerminationValues.builder()
                .stopValue(transaction.getStartValue())
                .stopTimestamp(transaction.getStartTimestamp())
                .build();
    }
}
