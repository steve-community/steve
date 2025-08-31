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
import de.rwth.idsg.steve.jooq.mapper.TransactionMapper;
import de.rwth.idsg.steve.repository.TransactionRepository;
import de.rwth.idsg.steve.repository.dto.Transaction;
import de.rwth.idsg.steve.repository.dto.TransactionDetails;
import de.rwth.idsg.steve.utils.TransactionStopServiceHelper;
import de.rwth.idsg.steve.web.dto.TransactionQueryForm;
import ocpp.cs._2015._10.UnitOfMeasure;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record9;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.Writer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static de.rwth.idsg.steve.repository.impl.RepositoryUtils.ocppTagByUserIdQuery;
import static de.rwth.idsg.steve.utils.CustomDSL.date;
import static de.rwth.idsg.steve.utils.DateTimeUtils.toInstant;
import static de.rwth.idsg.steve.utils.DateTimeUtils.toLocalDateTime;
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
        return getInternal(form).fetchOptional(TransactionMapper::fromRecord);
    }

    @Override
    public List<Transaction> getTransactions(TransactionQueryForm form) {
        return getInternal(form).fetch(TransactionMapper::fromRecord);
    }

    @Override
    public void writeTransactionsCSV(TransactionQueryForm form, Writer writer) {
        getInternalCSV(form).fetch().formatCSV(writer);
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
    public Optional<TransactionDetails> getDetails(int transactionPk) {

        // -------------------------------------------------------------------------
        // Step 1: Collect general data about transaction
        // -------------------------------------------------------------------------

        var form = new TransactionQueryForm();
        form.setTransactionPk(transactionPk);
        form.setType(TransactionQueryForm.QueryType.ALL);
        form.setPeriodType(TransactionQueryForm.QueryPeriodType.ALL);

        var transaction = getInternal(form).fetchOne();

        if (transaction == null) {
            return Optional.empty();
        }

        var startTimestamp = transaction.get(TRANSACTION.START_TIMESTAMP);
        var stopTimestamp = transaction.get(TRANSACTION.STOP_TIMESTAMP);
        var stopValue = transaction.get(TRANSACTION.STOP_VALUE);
        var chargeBoxId = transaction.get(CHARGE_BOX.CHARGE_BOX_ID);
        var connectorId = transaction.get(CONNECTOR.CONNECTOR_ID);

        // -------------------------------------------------------------------------
        // Step 2: Collect intermediate meter values
        // -------------------------------------------------------------------------

        Condition timestampCondition;
        TransactionDetails.NextTransactionStart nextTx = null;

        if (stopTimestamp == null && stopValue == null) {

            // https://github.com/steve-community/steve/issues/97
            //
            // handle "zombie" transaction, for which we did not receive any StopTransaction. if we do not handle it,
            // meter values for all subsequent transactions at this chargebox and connector will be falsely attributed
            // to this zombie transaction.
            //
            // "what is the subsequent transaction at the same chargebox and connector?"
            var nextTxRecord = ctx.selectFrom(TRANSACTION_START)
                    .where(TRANSACTION_START.CONNECTOR_PK.eq(ctx.select(CONNECTOR.CONNECTOR_PK)
                            .from(CONNECTOR)
                            .where(CONNECTOR.CHARGE_BOX_ID.equal(chargeBoxId))
                            .and(CONNECTOR.CONNECTOR_ID.equal(connectorId))))
                    .and(TRANSACTION_START.START_TIMESTAMP.greaterThan(startTimestamp))
                    .orderBy(TRANSACTION_START.START_TIMESTAMP)
                    .limit(1)
                    .fetchOne();

            if (nextTxRecord != null) {
                nextTx = TransactionDetails.NextTransactionStart.builder()
                        .startValue(nextTxRecord.getStartValue())
                        .startTimestamp(toInstant(nextTxRecord.getStartTimestamp()))
                        .build();
            }

            if (nextTxRecord == null) {
                // the last active transaction
                timestampCondition = CONNECTOR_METER_VALUE.VALUE_TIMESTAMP.greaterOrEqual(startTimestamp);
            } else {
                timestampCondition =
                        CONNECTOR_METER_VALUE.VALUE_TIMESTAMP.between(startTimestamp, nextTxRecord.getStartTimestamp());
            }
        } else {
            // finished transaction
            timestampCondition = CONNECTOR_METER_VALUE.VALUE_TIMESTAMP.between(startTimestamp, stopTimestamp);
        }

        // https://github.com/steve-community/steve/issues/1514
        Condition unitCondition = CONNECTOR_METER_VALUE
                .UNIT
                .isNull()
                .or(CONNECTOR_METER_VALUE.UNIT.in("", UnitOfMeasure.WH.value(), UnitOfMeasure.K_WH.value()));

        // Case 1: Ideal and most accurate case. Station sends meter values with transaction id set.
        //
        var transactionQuery = ctx.selectFrom(CONNECTOR_METER_VALUE)
                .where(CONNECTOR_METER_VALUE.TRANSACTION_PK.eq(transactionPk))
                .and(unitCondition)
                .getQuery();

        // Case 2: Fall back to filtering according to time windows
        //
        var timestampQuery = ctx.selectFrom(CONNECTOR_METER_VALUE)
                .where(CONNECTOR_METER_VALUE.CONNECTOR_PK.eq(ctx.select(CONNECTOR.CONNECTOR_PK)
                        .from(CONNECTOR)
                        .where(CONNECTOR.CHARGE_BOX_ID.equal(chargeBoxId))
                        .and(CONNECTOR.CONNECTOR_ID.equal(connectorId))))
                .and(timestampCondition)
                .and(unitCondition)
                .getQuery();

        // Actually, either case 1 applies or 2. If we retrieved values using 1, case 2 is should not be
        // executed (best case). In worst case (1 returns empty list and we fall back to case 2) though,
        // we make two db calls. Alternatively, we can pass both queries in one go, and make the db work.
        //
        // UNION removes all duplicate records
        //
        var t1 = transactionQuery.union(timestampQuery).asTable("t1");

        var dateTimeField = t1.field(2, LocalDateTime.class);

        var values = ctx
                .select(
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
                        .valueTimestamp(toInstant(r.value1()))
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

        return Optional.of(new TransactionDetails(TransactionMapper.fromRecord(transaction), values, nextTx));
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private SelectQuery<Record9<Integer, String, Integer, String, LocalDateTime, String, LocalDateTime, String, String>>
            getInternalCSV(TransactionQueryForm form) {

        var selectQuery = ctx.selectQuery();
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
                TRANSACTION.STOP_REASON);

        return addConditions(selectQuery, form);
    }

    /**
     * Difference from getInternalCSV:
     * Joins with CHARGE_BOX and OCPP_TAG tables, selects CHARGE_BOX_PK and OCPP_TAG_PK additionally
     */
    private SelectQuery<Record> getInternal(TransactionQueryForm form) {

        var selectQuery = ctx.selectQuery();
        selectQuery.addFrom(TRANSACTION);
        selectQuery.addJoin(CONNECTOR, TRANSACTION.CONNECTOR_PK.eq(CONNECTOR.CONNECTOR_PK));
        selectQuery.addJoin(CHARGE_BOX, CHARGE_BOX.CHARGE_BOX_ID.eq(CONNECTOR.CHARGE_BOX_ID));
        selectQuery.addJoin(OCPP_TAG, OCPP_TAG.ID_TAG.eq(TRANSACTION.ID_TAG));
        selectQuery.addSelect(TRANSACTION.fields());
        selectQuery.addSelect(
                CHARGE_BOX.CHARGE_BOX_ID, CONNECTOR.CONNECTOR_ID, OCPP_TAG.OCPP_TAG_PK, CHARGE_BOX.CHARGE_BOX_PK);

        return addConditions(selectQuery, form);
    }

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
            case TODAY ->
                selectQuery.addConditions(date(TRANSACTION.START_TIMESTAMP).eq(LocalDate.now()));
            case LAST_10, LAST_30, LAST_90 -> {
                var now = LocalDate.now();
                selectQuery.addConditions(date(TRANSACTION.START_TIMESTAMP)
                        .between(now.minusDays(form.getPeriodType().getInterval()), now));
            }
            case ALL -> {
                // want all: no timestamp filter
            }
            case FROM_TO -> {
                var from = toLocalDateTime(form.getFrom());
                var to = toLocalDateTime(form.getTo());

                switch (form.getType()) {
                    case ACTIVE -> selectQuery.addConditions(TRANSACTION.START_TIMESTAMP.between(from, to));
                    case STOPPED -> selectQuery.addConditions(TRANSACTION.STOP_TIMESTAMP.between(from, to));
                }
            }
            default -> throw new SteveException.InternalError("Unknown enum type");
        }
    }
}
