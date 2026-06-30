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

import com.google.common.collect.Ordering;
import de.rwth.idsg.steve.repository.OcppServerRepository;
import de.rwth.idsg.steve.repository.TransactionRepository;
import de.rwth.idsg.steve.repository.dto.Transaction;
import de.rwth.idsg.steve.repository.dto.TransactionDetails;
import de.rwth.idsg.steve.repository.dto.UpdateTransactionParams;
import de.rwth.idsg.steve.utils.TransactionStopServiceHelper;
import de.rwth.idsg.steve.web.dto.TransactionQueryForm;
import jooq.steve.db.enums.TransactionStopEventActor;
import jooq.steve.db.tables.records.TransactionStartRecord;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2012._06.UnitOfMeasure;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tools.jackson.dataformat.csv.CsvMapper;
import tools.jackson.dataformat.csv.CsvSchema;

import java.io.Writer;
import java.util.Comparator;
import java.util.List;

import static de.rwth.idsg.steve.utils.TransactionStopServiceHelper.floatingStringToIntString;
import static de.rwth.idsg.steve.utils.TransactionStopServiceHelper.kWhStringToWhDouble;
import static de.rwth.idsg.steve.utils.TransactionStopServiceHelper.kWhStringToWhString;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 02.10.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final OcppServerRepository ocppServerRepository;

    private final CsvMapper csvMapper = CsvMapper.builder().findAndAddModules().build();
    private final CsvSchema schema = csvMapper.schemaFor(TransactionDetails.MeterValues.class)
        .withHeader()
        .withNullValue("\"\""); // to be consistent with JOOQ's CSV behavior

    public List<Transaction> getTransactions(TransactionQueryForm form) {
        return transactionRepository.getTransactions(form);
    }

    public void writeTransactionsCSV(TransactionQueryForm form, Writer writer) {
        transactionRepository.writeTransactionsCSV(form, writer);
    }

    public void writeTransactionMeterValuesCSV(int transactionPk, boolean energyValuesOnly, Writer writer) {
        try (var seqWriter = csvMapper.writer(schema).writeValues(writer)) {
            seqWriter.writeAll(getDetails(transactionPk, energyValuesOnly).getValues());
        }
    }

    public List<Integer> getActiveTransactionIds(String chargeBoxId) {
        return transactionRepository.getActiveTransactionIds(chargeBoxId);
    }

    public TransactionDetails getDetails(int transactionPk, boolean energyValuesOnly) {
        return transactionRepository.getDetails(transactionPk, energyValuesOnly);
    }

    public Transaction getTransaction(int transactionPk) {
        TransactionQueryForm form = new TransactionQueryForm();
        form.setTransactionPk(List.of(transactionPk));
        form.setReturnCSV(false);
        form.setType(TransactionQueryForm.QueryType.ALL);

        List<Transaction> transactions = transactionRepository.getTransactions(form);
        if (CollectionUtils.isEmpty(transactions)) {
            return null;
        }
        return transactions.getFirst();
    }

    public Transaction getLatestActiveTransaction(String chargeBoxId, Integer connectorId) {
        TransactionQueryForm form = new TransactionQueryForm();
        form.setChargeBoxId(List.of(chargeBoxId));
        form.setConnectorId(connectorId);
        form.setReturnCSV(false);
        form.setType(TransactionQueryForm.QueryType.ACTIVE);

        var transactions = transactionRepository.getTransactions(form);
        if (transactions.isEmpty()) {
            return null;
        } else if (transactions.size() == 1) {
            return transactions.get(0);
        } else {
            log.warn("Found multiple active transactions for chargeBoxId '{}' and connectorId '{}'. Returning the most recent one.", chargeBoxId, connectorId);
            return transactions.stream()
                .max(Comparator.comparing(Transaction::getStartTimestamp))
                .orElse(null); // Should not be null here, but for safety
        }
    }

    public void stop(List<Integer> transactionPkList) {
        transactionPkList.stream()
                         .sorted(Ordering.natural())
                         .forEach(this::stop);
    }

    public void stop(Integer transactionPk) {
        TransactionDetails thisTxDetails = transactionRepository.getDetails(transactionPk, true);
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

        TransactionDetails.MeterValues last = findLastMeterValue(intermediateValues, thisTx);
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
                                .stopTimestamp(thisTx.getStartTimestamp())
                                .build();
    }

    @Nullable
    static TransactionDetails.MeterValues findLastMeterValue(List<TransactionDetails.MeterValues> values,
                                                             Transaction thisTx) {
        var treatedValues = values.stream()
            .filter(TransactionStopServiceHelper::isEnergyValue)
            .sorted(Comparator.comparing(TransactionDetails.MeterValues::getValueTimestamp))
            .toList();

        TransactionDetails.MeterValues v = findLastMeterValueFromMonotonicStreak(
            treatedValues,
            Double.parseDouble(thisTx.getStartValue())
        );

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
        }

        return v;
    }

    @Nullable
    private static TransactionDetails.MeterValues findLastMeterValueFromMonotonicStreak(List<TransactionDetails.MeterValues> values,
                                                                                        double startValueWh) {
        TransactionDetails.MeterValues lastMatchingValue = null;
        Double previousValueWh = null;
        int currentStreakLength = 0;
        boolean firstStreak = true;

        for (TransactionDetails.MeterValues value : values) {
            double valueWh = toWh(value);

            if (previousValueWh != null && valueWh < previousValueWh) {
                firstStreak = false;
                currentStreakLength = 1;
            } else {
                currentStreakLength++;
            }

            if (valueWh >= startValueWh && (firstStreak || currentStreakLength > 1)) {
                lastMatchingValue = value;
            }

            previousValueWh = valueWh;
        }

        return lastMatchingValue;
    }

    private static double toWh(TransactionDetails.MeterValues meterValue) {
        return UnitOfMeasure.K_WH.value().equals(meterValue.getUnit())
            ? kWhStringToWhDouble(meterValue.getValue())
            : Double.parseDouble(meterValue.getValue());
    }

    @Builder
    private static class TerminationValues {
        private final String stopValue;
        private final DateTime stopTimestamp;
    }
}
