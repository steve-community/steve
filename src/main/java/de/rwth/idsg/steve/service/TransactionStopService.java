/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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

import com.google.common.base.Strings;
import com.google.common.collect.Ordering;
import de.rwth.idsg.steve.repository.OcppServerRepository;
import de.rwth.idsg.steve.repository.TransactionRepository;
import de.rwth.idsg.steve.repository.dto.Transaction;
import de.rwth.idsg.steve.repository.dto.TransactionDetails;
import de.rwth.idsg.steve.repository.dto.UpdateTransactionParams;
import jooq.steve.db.enums.TransactionStopEventActor;
import jooq.steve.db.tables.records.TransactionStartRecord;
import lombok.Builder;
import ocpp.cs._2012._06.UnitOfMeasure;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 09.12.2018
 */
@Service
public class TransactionStopService {

    @Autowired private TransactionRepository transactionRepository;
    @Autowired private OcppServerRepository ocppServerRepository;

    public void stop(List<Integer> transactionPkList) {
        transactionPkList.stream()
                         .sorted(Ordering.natural())
                         .forEach(this::stop);
    }

    public void stop(Integer transactionPk) {
        TransactionDetails thisTxDetails = transactionRepository.getDetails(transactionPk, false);
        Transaction thisTx = thisTxDetails.getTransaction();

        // early exit, if transaction is already stopped
        if (thisTx.getStopValue() != null && thisTx.getStopTimestamp() != null) {
            return;
        }

        TerminationValues values = findNeededValues(thisTxDetails);

        ocppServerRepository.updateTransaction(UpdateTransactionParams.builder()
                                                                      .transactionId(thisTx.getId())
                                                                      .chargeBoxId(thisTx.getChargeBoxId())
                                                                      .stopMeterValue(values.stopValue)
                                                                      .stopTimestamp(values.stopTimestamp)
                                                                      .eventActor(TransactionStopEventActor.manual)
                                                                      .eventTimestamp(DateTime.now())
                                                                      .build());
    }

    private static TerminationValues findNeededValues(TransactionDetails thisTxDetails) {
        Transaction thisTx = thisTxDetails.getTransaction();
        TransactionStartRecord nextTx = thisTxDetails.getNextTransactionStart();
        List<TransactionDetails.MeterValues> intermediateValues = thisTxDetails.getValues();

        // -------------------------------------------------------------------------
        // 1. intermediate meter values have priority (most accurate data)
        // -------------------------------------------------------------------------

        TransactionDetails.MeterValues last = findLastMeterValue(intermediateValues);
        if (last != null) {
            return TerminationValues.builder()
                                    .stopValue(floatingStringToIntString(last.getValue()))
                                    .stopTimestamp(last.getValueTimestamp())
                                    .build();
        }

        // -------------------------------------------------------------------------
        // 2. a latest energy meter value does not exist, use data of next tx
        // -------------------------------------------------------------------------

        if (nextTx != null) {
            // some charging stations do not reset the meter value counter after each transaction and
            // continue counting. in such cases, use the value of subsequent transaction's start value
            if (Integer.parseInt(nextTx.getStartValue()) > Integer.parseInt(thisTx.getStartValue())) {
                return TerminationValues.builder()
                                        .stopValue(nextTx.getStartValue())
                                        .stopTimestamp(nextTx.getStartTimestamp())
                                        .build();
            } else {
                // this mix of strategies might be really confusing
                return TerminationValues.builder()
                                        .stopValue(thisTx.getStartValue())
                                        .stopTimestamp(nextTx.getStartTimestamp())
                                        .build();
            }
        }

        // -------------------------------------------------------------------------
        // 3. neither meter values nor next tx exist, use start values
        // -------------------------------------------------------------------------

        return TerminationValues.builder()
                                .stopValue(thisTx.getStartValue())
                                .stopTimestamp(thisTx.getStartTimestampDT())
                                .build();
    }

    @Nullable
    private static TransactionDetails.MeterValues findLastMeterValue(List<TransactionDetails.MeterValues> values) {
        TransactionDetails.MeterValues v =
                values.stream()
                      .filter(TransactionStopService::isEnergyValue)
                      .max(Comparator.comparing(TransactionDetails.MeterValues::getValueTimestamp))
                      .orElse(null);

        // if the list of values is empty, we fall to this case, as well.
        if (v == null) {
            return null;
        }

        // convert kWh to Wh
        if (UnitOfMeasure.K_WH.value().equals(v.getUnit())) {
            double kWhValue = Double.parseDouble(v.getValue());
            return TransactionDetails.MeterValues.builder()
                                                 .value(Double.toString(kWhValue * 1000))
                                                 .valueTimestamp(v.getValueTimestamp())
                                                 .readingContext(v.getReadingContext())
                                                 .format(v.getFormat())
                                                 .measurand(v.getMeasurand())
                                                 .location(v.getLocation())
                                                 .unit(v.getUnit())
                                                 .phase(v.getPhase())
                                                 .build();
        } else {
            return v;
        }
    }

    private static String floatingStringToIntString(String s) {
        // meter values can be floating, whereas start/end values are int
        return Integer.toString((int) Math.ceil(Double.parseDouble(s)));
    }

    private static boolean isEnergyValue(TransactionDetails.MeterValues v) {
        // should not happen, but check it to be safe.
        // https://github.com/RWTH-i5-IDSG/steve/issues/249
        if (Strings.isNullOrEmpty(v.getValue())) {
            return false;
        }

        if (v.getUnit() == null) {
            return v.getMeasurand() == null || v.getMeasurand().startsWith("Energy.Active.Import");
        } else {
            return UnitOfMeasure.WH.value().equals(v.getUnit()) || UnitOfMeasure.K_WH.value().equals(v.getUnit());
        }
    }

    @Builder
    private static class TerminationValues {
        private final String stopValue;
        private final DateTime stopTimestamp;
    }
}
