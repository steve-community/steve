package de.rwth.idsg.steve.service;

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
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
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

        boolean valuesExist = !intermediateValues.isEmpty();
        boolean nextTxExists = nextTx != null;

        // -------------------------------------------------------------------------
        // 1. intermediate meter values have priority (most accurate data)
        // -------------------------------------------------------------------------

        if (valuesExist) {
            TransactionDetails.MeterValues last = findLastMeterValue(intermediateValues);
            return TerminationValues.builder()
                                    .stopValue(floatingStringToIntString(last.getValue()))
                                    .stopTimestamp(last.getValueTimestamp())
                                    .build();
        }

        // -------------------------------------------------------------------------
        // 2. meter values do not exist, use data of next tx
        // -------------------------------------------------------------------------

        if (nextTxExists) {
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

    private static TransactionDetails.MeterValues findLastMeterValue(List<TransactionDetails.MeterValues> values) {
        // sort by DESCENDING timestamps and process
        values.sort(Comparator.comparing(TransactionDetails.MeterValues::getValueTimestamp).reversed());

        TransactionDetails.MeterValues v =
                values.stream()
                      .filter(TransactionStopService::isEnergyUnit)
                      .findFirst()
                      // the station has not the habit of setting additional info like energy units. just return the
                      // first meter value, which theoretically can be any measurement of any component and of any unit
                      // that the station decided to send us. let's pray that it is an energy value.
                      .orElseGet(() -> values.get(0));

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

    private static boolean isEnergyUnit(TransactionDetails.MeterValues v) {
        return UnitOfMeasure.WH.value().equals(v.getUnit()) || UnitOfMeasure.K_WH.value().equals(v.getUnit());
    }

    @Builder
    private static class TerminationValues {
        private final String stopValue;
        private final DateTime stopTimestamp;
    }
}
