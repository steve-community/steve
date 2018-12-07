package de.rwth.idsg.steve.repository.impl;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.TransactionRepository;
import de.rwth.idsg.steve.repository.dto.Transaction;
import de.rwth.idsg.steve.repository.dto.TransactionDetails;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import de.rwth.idsg.steve.web.dto.TransactionQueryForm;
import jooq.steve.db.tables.records.ConnectorMeterValueRecord;
import org.joda.time.DateTime;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record11;
import org.jooq.Record9;
import org.jooq.RecordMapper;
import org.jooq.SelectQuery;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.Writer;
import java.util.List;

import static de.rwth.idsg.steve.utils.CustomDSL.date;
import static jooq.steve.db.tables.ChargeBox.CHARGE_BOX;
import static jooq.steve.db.tables.Connector.CONNECTOR;
import static jooq.steve.db.tables.ConnectorMeterValue.CONNECTOR_METER_VALUE;
import static jooq.steve.db.tables.OcppTag.OCPP_TAG;
import static jooq.steve.db.tables.Transaction.TRANSACTION;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 14.08.2014
 */
@Repository
public class TransactionRepositoryImpl implements TransactionRepository {

    private final DSLContext ctx;

    @Autowired
    public TransactionRepositoryImpl(DSLContext ctx) {
        this.ctx = ctx;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Transaction> getTransactions(TransactionQueryForm form) {
        return getInternal(form).fetch()
                                .map(new TransactionMapper());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void writeTransactionsCSV(TransactionQueryForm form, Writer writer) {
        getInternalCSV(form).fetch()
                            .formatCSV(writer);
    }

    @Override
    public List<Integer> getActiveTransactionIds(String chargeBoxId) {
        return ctx.select(TRANSACTION.TRANSACTION_PK)
                  .from(TRANSACTION)
                  .join(CONNECTOR)
                    .on(TRANSACTION.CONNECTOR_PK.equal(CONNECTOR.CONNECTOR_PK))
                    .and(CONNECTOR.CHARGE_BOX_ID.equal(chargeBoxId))
                  .where(TRANSACTION.STOP_TIMESTAMP.isNull())
                  .fetch(TRANSACTION.TRANSACTION_PK);
    }

    @Override
    public TransactionDetails getDetails(int transactionPk) {

        // -------------------------------------------------------------------------
        // Step 1: Collect general data about transaction
        // -------------------------------------------------------------------------

        TransactionQueryForm form = new TransactionQueryForm();
        form.setTransactionPk(transactionPk);
        form.setType(TransactionQueryForm.QueryType.ALL);
        form.setPeriodType(TransactionQueryForm.QueryPeriodType.ALL);

        Record11<Integer, String, Integer, String, DateTime, String, DateTime, String, String, Integer, Integer>
                transaction = getInternal(form).fetchOne();

        if (transaction == null) {
            throw new SteveException("There is no transaction with id '%s'", transactionPk);
        }

        DateTime startTimestamp = transaction.value5();
        DateTime stopTimestamp = transaction.value7();
        String stopValue = transaction.value8();
        String chargeBoxId = transaction.value2();
        int connectorId = transaction.value3();

        // -------------------------------------------------------------------------
        // Step 2: Collect intermediate meter values
        // -------------------------------------------------------------------------

        Condition timestampCondition;
        if (stopTimestamp == null && stopValue == null) {

            // handle "zombie" transaction, for which we did not receive any StopTransaction. if we do not handle it,
            // meter values for all subsequent transactions at this chargebox and connector will be falsely attributed
            // to this zombie transaction.
            //
            // "what is the start time of the first subsequent transaction at the same chargebox and connector?"
            DateTime startOfNext = ctx.select(TRANSACTION.START_TIMESTAMP)
                                      .from(TRANSACTION)
                                      .join(CONNECTOR)
                                        .on(TRANSACTION.CONNECTOR_PK.equal(CONNECTOR.CONNECTOR_PK))
                                      .where(CONNECTOR.CHARGE_BOX_ID.equal(chargeBoxId))
                                        .and(CONNECTOR.CONNECTOR_ID.equal(connectorId))
                                        .and(TRANSACTION.START_TIMESTAMP.greaterThan(startTimestamp))
                                      .orderBy(TRANSACTION.START_TIMESTAMP)
                                      .limit(1)
                                      .fetchOne(TRANSACTION.START_TIMESTAMP);

            if (startOfNext == null) {
                // the last active transaction
                timestampCondition = CONNECTOR_METER_VALUE.VALUE_TIMESTAMP.greaterOrEqual(startTimestamp);
            } else {
                timestampCondition = CONNECTOR_METER_VALUE.VALUE_TIMESTAMP.between(startTimestamp, startOfNext);
            }
        } else {
            // finished transaction
            timestampCondition = CONNECTOR_METER_VALUE.VALUE_TIMESTAMP.between(startTimestamp, stopTimestamp);
        }

        // Case 1: Ideal and most accurate case. Station sends meter values with transaction id set.
        //
        SelectQuery<ConnectorMeterValueRecord> transactionQuery =
                ctx.selectFrom(CONNECTOR_METER_VALUE)
                   .where(CONNECTOR_METER_VALUE.TRANSACTION_PK.eq(transactionPk))
                   .getQuery();

        // Case 2: Fall back to filtering according to time windows
        //
        SelectQuery<ConnectorMeterValueRecord> timestampQuery =
                ctx.selectFrom(CONNECTOR_METER_VALUE)
                   .where(CONNECTOR_METER_VALUE.CONNECTOR_PK.eq(ctx.select(CONNECTOR.CONNECTOR_PK)
                                                                   .from(CONNECTOR)
                                                                   .where(CONNECTOR.CHARGE_BOX_ID.eq(chargeBoxId))
                                                                   .and(CONNECTOR.CONNECTOR_ID.eq(connectorId))))
                   .and(timestampCondition)
                   .getQuery();

        // Actually, either case 1 applies or 2. If we retrieved values using 1, case 2 is should not be
        // executed (best case). In worst case (1 returns empty list and we fall back to case 2) though,
        // we make two db calls. Alternatively, we can pass both queries in one go, and make the db work.
        //
        // UNION removes all duplicate records
        //
        Table<ConnectorMeterValueRecord> t1 = transactionQuery.union(timestampQuery).asTable("t1");

        // -------------------------------------------------------------------------
        // Step 3: Charging station might send meter vales at fixed intervals (e.g.
        // every 15 min) regardless of the fact that connector's meter value did not
        // change (e.g. vehicle is fully charged, but cable is still connected). This
        // yields multiple entries in db with the same value but different timestamp.
        // We are only interested in the first arriving entry.
        // -------------------------------------------------------------------------

        Field<DateTime> dateTimeField = DSL.min(t1.field(2, DateTime.class)).as("min");

        List<TransactionDetails.MeterValues> values =
                ctx.select(
                        dateTimeField,
                        t1.field(3, String.class),
                        t1.field(4, String.class),
                        t1.field(5, String.class),
                        t1.field(6, String.class),
                        t1.field(7, String.class),
                        t1.field(8, String.class),
                        t1.field(9, String.class))
                   .from(t1)
                   .groupBy(
                           t1.field(3),
                           t1.field(4),
                           t1.field(5),
                           t1.field(6),
                           t1.field(7),
                           t1.field(8),
                           t1.field(9))
                   .orderBy(dateTimeField)
                   .fetch()
                   .map(r -> TransactionDetails.MeterValues.builder()
                                                           .valueTimestamp(r.value1())
                                                           .value(r.value2())
                                                           .readingContext(r.value3())
                                                           .format(r.value4())
                                                           .measurand(r.value5())
                                                           .location(r.value6())
                                                           .unit(r.value7())
                                                           .phase(r.value8())
                                                           .build());

        return new TransactionDetails(new TransactionMapper().map(transaction), values);
    }

    /**
     * See documentation of {@link TransactionRepository#getChargeBoxIdsOfActiveTransactions(String)}
     */
    @Override
    public List<String> getChargeBoxIdsOfActiveTransactions(String ocppIdTag) {
        return ctx.select(CONNECTOR.CHARGE_BOX_ID)
                  .from(CONNECTOR)
                  .join(TRANSACTION)
                    .on(TRANSACTION.CONNECTOR_PK.equal(CONNECTOR.CONNECTOR_PK))
                  .where(TRANSACTION.ID_TAG.eq(ocppIdTag))
                    .and(TRANSACTION.STOP_VALUE.isNull())
                    .and(TRANSACTION.STOP_TIMESTAMP.isNull())
                  .fetch(CONNECTOR.CHARGE_BOX_ID);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private
    SelectQuery<Record9<Integer, String, Integer, String, DateTime, String, DateTime, String, String>>
    getInternalCSV(TransactionQueryForm form) {

        SelectQuery selectQuery = ctx.selectQuery();
        selectQuery.addFrom(TRANSACTION);
        selectQuery.addJoin(CONNECTOR, TRANSACTION.CONNECTOR_PK.eq(CONNECTOR.CONNECTOR_PK));
        selectQuery.addSelect(
                TRANSACTION.TRANSACTION_PK,
                CONNECTOR.CHARGE_BOX_ID,
                CONNECTOR.CONNECTOR_ID,
                TRANSACTION.ID_TAG,
                TRANSACTION.START_TIMESTAMP,
                TRANSACTION.START_VALUE,
                TRANSACTION.STOP_TIMESTAMP,
                TRANSACTION.STOP_VALUE,
                TRANSACTION.STOP_REASON
        );

        return addConditions(selectQuery, form);
    }

    /**
     * Difference from getInternalCSV:
     * Joins with CHARGE_BOX and OCPP_TAG tables, selects CHARGE_BOX_PK and OCPP_TAG_PK additionally
     */
    @SuppressWarnings("unchecked")
    private
    SelectQuery<Record11<Integer, String, Integer, String, DateTime, String, DateTime, String, String, Integer, Integer>>
    getInternal(TransactionQueryForm form) {

        SelectQuery selectQuery = ctx.selectQuery();
        selectQuery.addFrom(TRANSACTION);
        selectQuery.addJoin(CONNECTOR, TRANSACTION.CONNECTOR_PK.eq(CONNECTOR.CONNECTOR_PK));
        selectQuery.addJoin(CHARGE_BOX, CHARGE_BOX.CHARGE_BOX_ID.eq(CONNECTOR.CHARGE_BOX_ID));
        selectQuery.addJoin(OCPP_TAG, OCPP_TAG.ID_TAG.eq(TRANSACTION.ID_TAG));
        selectQuery.addSelect(
                TRANSACTION.TRANSACTION_PK,
                CONNECTOR.CHARGE_BOX_ID,
                CONNECTOR.CONNECTOR_ID,
                TRANSACTION.ID_TAG,
                TRANSACTION.START_TIMESTAMP,
                TRANSACTION.START_VALUE,
                TRANSACTION.STOP_TIMESTAMP,
                TRANSACTION.STOP_VALUE,
                TRANSACTION.STOP_REASON,
                CHARGE_BOX.CHARGE_BOX_PK,
                OCPP_TAG.OCPP_TAG_PK
        );

        return addConditions(selectQuery, form);
    }

    @SuppressWarnings("unchecked")
    private SelectQuery addConditions(SelectQuery selectQuery, TransactionQueryForm form) {
        if (form.isTransactionPkSet()) {
            selectQuery.addConditions(TRANSACTION.TRANSACTION_PK.eq(form.getTransactionPk()));
        }

        if (form.isChargeBoxIdSet()) {
            selectQuery.addConditions(CONNECTOR.CHARGE_BOX_ID.eq(form.getChargeBoxId()));
        }

        if (form.isOcppIdTagSet()) {
            selectQuery.addConditions(TRANSACTION.ID_TAG.eq(form.getOcppIdTag()));
        }

        if (form.getType() == TransactionQueryForm.QueryType.ACTIVE) {
            selectQuery.addConditions(TRANSACTION.STOP_TIMESTAMP.isNull());
        }

        processType(selectQuery, form);

        // Default order
        selectQuery.addOrderBy(TRANSACTION.TRANSACTION_PK.desc());

        return selectQuery;
    }

    private void processType(SelectQuery selectQuery, TransactionQueryForm form) {
        switch (form.getPeriodType()) {
            case TODAY:
                selectQuery.addConditions(
                        date(TRANSACTION.START_TIMESTAMP).eq(date(DateTime.now()))
                );
                break;

            case LAST_10:
            case LAST_30:
            case LAST_90:
                DateTime now = DateTime.now();
                selectQuery.addConditions(
                        date(TRANSACTION.START_TIMESTAMP).between(
                                date(now.minusDays(form.getPeriodType().getInterval())),
                                date(now)
                        )
                );
                break;

            case ALL:
                break;

            case FROM_TO:
                selectQuery.addConditions(
                        TRANSACTION.START_TIMESTAMP.between(form.getFrom().toDateTime(), form.getTo().toDateTime())
                );
                break;

            default:
                throw new SteveException("Unknown enum type");
        }
    }

    private static class TransactionMapper
            implements RecordMapper<Record11<Integer, String, Integer, String, DateTime, String, DateTime,
                                             String, String, Integer, Integer>, Transaction> {
        @Override
        public Transaction map(Record11<Integer, String, Integer, String, DateTime, String, DateTime,
                                        String, String, Integer, Integer> r) {
            return Transaction.builder()
                              .id(r.value1())
                              .chargeBoxId(r.value2())
                              .connectorId(r.value3())
                              .ocppIdTag(r.value4())
                              .startTimestampDT(r.value5())
                              .startTimestamp(DateTimeUtils.humanize(r.value5()))
                              .startValue(r.value6())
                              .stopTimestampDT(r.value7())
                              .stopTimestamp(DateTimeUtils.humanize(r.value7()))
                              .stopValue(r.value8())
                              .stopReason(r.value9())
                              .chargeBoxPk(r.value10())
                              .ocppTagPk(r.value11())
                              .build();
        }
    }
}
