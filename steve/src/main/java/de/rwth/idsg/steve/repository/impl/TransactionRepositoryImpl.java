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
package de.rwth.idsg.steve.repository.impl;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.TransactionRepository;
import de.rwth.idsg.steve.repository.dto.Transaction;
import de.rwth.idsg.steve.repository.dto.TransactionDetails;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import de.rwth.idsg.steve.utils.TransactionStopServiceHelper;
import de.rwth.idsg.steve.web.dto.TransactionQueryForm;
import jooq.steve.db.enums.TransactionStopEventActor;
import jooq.steve.db.tables.records.ConnectorMeterValueRecord;
import jooq.steve.db.tables.records.TransactionStartRecord;
import ocpp.cs._2015._10.UnitOfMeasure;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record12;
import org.jooq.Record9;
import org.jooq.RecordMapper;
import org.jooq.SelectQuery;
import org.jooq.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.Writer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static de.rwth.idsg.steve.repository.impl.RepositoryUtils.ocppTagByUserIdQuery;
import static de.rwth.idsg.steve.utils.CustomDSL.date;
import static de.rwth.idsg.steve.utils.DateTimeUtils.toOffsetDateTime;
import static jooq.steve.db.tables.ChargeBox.CHARGE_BOX;
import static jooq.steve.db.tables.Connector.CONNECTOR;
import static jooq.steve.db.tables.ConnectorMeterValue.CONNECTOR_METER_VALUE;
import static jooq.steve.db.tables.OcppTag.OCPP_TAG;
import static jooq.steve.db.tables.Transaction.TRANSACTION;
import static jooq.steve.db.tables.TransactionStart.TRANSACTION_START;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
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
    public Optional<Transaction> getTransaction(int transactionPk) {
        var form = new TransactionQueryForm();
        form.setTransactionPk(transactionPk);
        form.setReturnCSV(false);
        form.setType(TransactionQueryForm.QueryType.ALL);
        var r = getInternal(form).fetchAny(new TransactionMapper());
        return Optional.ofNullable(r);
    }

    @Override
    public List<Transaction> getTransactions(TransactionQueryForm form) {
        return getInternal(form).fetch()
                                .map(new TransactionMapper());
    }

    @Override
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
    public Optional<Integer> getActiveTransactionId(String chargeBoxId, int connectorId) {
        var r = ctx.select(TRANSACTION.TRANSACTION_PK)
                  .from(TRANSACTION)
                  .join(CONNECTOR)
                    .on(TRANSACTION.CONNECTOR_PK.equal(CONNECTOR.CONNECTOR_PK))
                    .and(CONNECTOR.CHARGE_BOX_ID.equal(chargeBoxId))
                  .where(TRANSACTION.STOP_TIMESTAMP.isNull())
                    .and(CONNECTOR.CONNECTOR_ID.equal(connectorId))
                  .orderBy(TRANSACTION.TRANSACTION_PK.desc()) // to avoid fetching ghost transactions, fetch the latest
                  .limit(1)
                  .fetchOne(TRANSACTION.TRANSACTION_PK);
        return Optional.ofNullable(r);
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

        Record12<Integer, String, Integer, String, LocalDateTime, String, LocalDateTime, String, String, Integer,
                Integer, TransactionStopEventActor>
                transaction = getInternal(form).fetchOne();

        if (transaction == null) {
            throw new SteveException("There is no transaction with id '%s'", transactionPk);
        }

        var startTimestamp = transaction.value5();
        var stopTimestamp = transaction.value7();
        String stopValue = transaction.value8();
        String chargeBoxId = transaction.value2();
        int connectorId = transaction.value3();

        // -------------------------------------------------------------------------
        // Step 2: Collect intermediate meter values
        // -------------------------------------------------------------------------

        Condition timestampCondition;
        TransactionStartRecord nextTx = null;

        if (stopTimestamp == null && stopValue == null) {

            // https://github.com/steve-community/steve/issues/97
            //
            // handle "zombie" transaction, for which we did not receive any StopTransaction. if we do not handle it,
            // meter values for all subsequent transactions at this chargebox and connector will be falsely attributed
            // to this zombie transaction.
            //
            // "what is the subsequent transaction at the same chargebox and connector?"
            nextTx = ctx.selectFrom(TRANSACTION_START)
                        .where(TRANSACTION_START.CONNECTOR_PK.eq(ctx.select(CONNECTOR.CONNECTOR_PK)
                                                                    .from(CONNECTOR)
                                                                    .where(CONNECTOR.CHARGE_BOX_ID.equal(chargeBoxId))
                                                                    .and(CONNECTOR.CONNECTOR_ID.equal(connectorId))))
                        .and(TRANSACTION_START.START_TIMESTAMP.greaterThan(startTimestamp))
                        .orderBy(TRANSACTION_START.START_TIMESTAMP)
                        .limit(1)
                        .fetchOne();

            if (nextTx == null) {
                // the last active transaction
                timestampCondition = CONNECTOR_METER_VALUE.VALUE_TIMESTAMP.greaterOrEqual(startTimestamp);
            } else {
                timestampCondition = CONNECTOR_METER_VALUE.VALUE_TIMESTAMP.between(startTimestamp, nextTx.getStartTimestamp());
            }
        } else {
            // finished transaction
            timestampCondition = CONNECTOR_METER_VALUE.VALUE_TIMESTAMP.between(startTimestamp, stopTimestamp);
        }

        // https://github.com/steve-community/steve/issues/1514
        Condition unitCondition = CONNECTOR_METER_VALUE.UNIT.isNull()
            .or(CONNECTOR_METER_VALUE.UNIT.in("", UnitOfMeasure.WH.value(), UnitOfMeasure.K_WH.value()));

        // Case 1: Ideal and most accurate case. Station sends meter values with transaction id set.
        //
        SelectQuery<ConnectorMeterValueRecord> transactionQuery =
                ctx.selectFrom(CONNECTOR_METER_VALUE)
                   .where(CONNECTOR_METER_VALUE.TRANSACTION_PK.eq(transactionPk))
                   .and(unitCondition)
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
                   .and(unitCondition)
                   .getQuery();

        // Actually, either case 1 applies or 2. If we retrieved values using 1, case 2 is should not be
        // executed (best case). In worst case (1 returns empty list and we fall back to case 2) though,
        // we make two db calls. Alternatively, we can pass both queries in one go, and make the db work.
        //
        // UNION removes all duplicate records
        //
        Table<ConnectorMeterValueRecord> t1 = transactionQuery.union(timestampQuery).asTable("t1");

        var dateTimeField = t1.field(2, LocalDateTime.class);

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
                   .orderBy(dateTimeField)
                   .fetch()
                   .map(r -> TransactionDetails.MeterValues.builder()
                                                           .valueTimestamp(toOffsetDateTime(r.value1()))
                                                           .value(r.value2())
                                                           .readingContext(r.value3())
                                                           .format(r.value4())
                                                           .measurand(r.value5())
                                                           .location(r.value6())
                                                           .unit(r.value7())
                                                           .phase(r.value8())
                                                           .build())
                   .stream()
                   .filter(TransactionStopServiceHelper::isEnergyValue)
                   .toList();

        return new TransactionDetails(new TransactionMapper().map(transaction), values, nextTx);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private
    SelectQuery<Record9<Integer, String, Integer, String, LocalDateTime, String, LocalDateTime, String, String>>
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
    SelectQuery<Record12<Integer, String, Integer, String, LocalDateTime, String, LocalDateTime, String, String,
            Integer, Integer, TransactionStopEventActor>>
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
                OCPP_TAG.OCPP_TAG_PK,
                TRANSACTION.STOP_EVENT_ACTOR
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

        if (form.isUserIdSet()) {
            var query = ocppTagByUserIdQuery(ctx, form.getUserId());
            selectQuery.addConditions(TRANSACTION.ID_TAG.in(query));
        }

        if (form.getType() == TransactionQueryForm.QueryType.ACTIVE) {
            selectQuery.addConditions(TRANSACTION.STOP_TIMESTAMP.isNull());

        } else if (form.getType() == TransactionQueryForm.QueryType.STOPPED) {
            selectQuery.addConditions(TRANSACTION.STOP_TIMESTAMP.isNotNull());
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
                        date(TRANSACTION.START_TIMESTAMP).eq(LocalDate.now())
                );
                break;

            case LAST_10:
            case LAST_30:
            case LAST_90:
                var now = LocalDate.now();
                selectQuery.addConditions(
                        date(TRANSACTION.START_TIMESTAMP).between(
                                now.minusDays(form.getPeriodType().getInterval()),
                                now
                        )
                );
                break;

            case ALL:
                break;

            case FROM_TO:
                var from = form.getFrom();
                var to = form.getTo();

                if (form.getType() == TransactionQueryForm.QueryType.ACTIVE) {
                    selectQuery.addConditions(TRANSACTION.START_TIMESTAMP.between(from, to));

                } else if (form.getType() == TransactionQueryForm.QueryType.STOPPED) {
                    selectQuery.addConditions(TRANSACTION.STOP_TIMESTAMP.between(from, to));
                }

                break;

            default:
                throw new SteveException("Unknown enum type");
        }
    }

    private static class TransactionMapper implements RecordMapper<Record12<Integer, String, Integer, String,
            LocalDateTime, String, LocalDateTime, String, String, Integer, Integer, TransactionStopEventActor>, Transaction> {
        @Override
        public Transaction map(Record12<Integer, String, Integer, String, LocalDateTime, String, LocalDateTime, String,
                String, Integer, Integer, TransactionStopEventActor> r) {
            return Transaction.builder()
                              .id(r.value1())
                              .chargeBoxId(r.value2())
                              .connectorId(r.value3())
                              .ocppIdTag(r.value4())
                              .startTimestamp(toOffsetDateTime(r.value5()))
                              .startTimestampFormatted(DateTimeUtils.humanize(r.value5()))
                              .startValue(r.value6())
                              .stopTimestamp(toOffsetDateTime(r.value7()))
                              .stopTimestampFormatted(DateTimeUtils.humanize(r.value7()))
                              .stopValue(r.value8())
                              .stopReason(r.value9())
                              .chargeBoxPk(r.value10())
                              .ocppTagPk(r.value11())
                              .stopEventActor(r.value12())
                              .build();
        }
    }
}
