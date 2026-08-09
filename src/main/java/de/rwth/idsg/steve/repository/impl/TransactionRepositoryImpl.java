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
package de.rwth.idsg.steve.repository.impl;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.TransactionRepository;
import de.rwth.idsg.steve.repository.dto.Transaction;
import de.rwth.idsg.steve.repository.dto.TransactionDetails;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import de.rwth.idsg.steve.utils.TransactionStopServiceHelper;
import de.rwth.idsg.steve.web.dto.QueryPeriodType;
import de.rwth.idsg.steve.web.dto.TransactionQueryForm;
import jooq.steve.db.enums.EvseTopologySource;
import jooq.steve.db.tables.records.TransactionRecord;
import jooq.steve.db.tables.records.TransactionStartRecord;
import lombok.RequiredArgsConstructor;
import ocpp.cs._2015._10.UnitOfMeasure;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Repository;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import static de.rwth.idsg.steve.utils.CustomDSL.getTimeCondition;
import static jooq.steve.db.Tables.USER_OCPP_TAG;
import static jooq.steve.db.tables.ChargeBox.CHARGE_BOX;
import static jooq.steve.db.tables.Evse.EVSE;
import static jooq.steve.db.tables.ConnectorMeterValue.CONNECTOR_METER_VALUE;
import static jooq.steve.db.tables.OcppTag.OCPP_TAG;
import static jooq.steve.db.tables.Transaction.TRANSACTION;
import static jooq.steve.db.tables.TransactionStart.TRANSACTION_START;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 14.08.2014
 */
@Repository
@RequiredArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepository {

    private final DSLContext ctx;

    /**
     * Difference from getInternalCSV:
     * Joins with CHARGE_BOX and OCPP_TAG tables, selects CHARGE_BOX_PK and OCPP_TAG_PK additionally
     */
    @Override
    public List<Transaction> getTransactions(TransactionQueryForm form) {
        var conditions = getConditions(form);

        return ctx.select(
                TRANSACTION.TRANSACTION_PK,
                EVSE.CHARGE_BOX_ID,
                EVSE.EVSE_ID,
                TRANSACTION.ID_TAG,
                TRANSACTION.START_TIMESTAMP,
                TRANSACTION.START_VALUE,
                TRANSACTION.STOP_TIMESTAMP,
                TRANSACTION.STOP_VALUE,
                TRANSACTION.STOP_REASON,
                CHARGE_BOX.CHARGE_BOX_PK,
                OCPP_TAG.OCPP_TAG_PK,
                TRANSACTION.STOP_EVENT_ACTOR,
                USER_OCPP_TAG.USER_PK)
            .from(TRANSACTION)
            .join(EVSE).on(TRANSACTION.EVSE_PK.eq(EVSE.EVSE_PK))
            .join(CHARGE_BOX).on(CHARGE_BOX.CHARGE_BOX_ID.eq(EVSE.CHARGE_BOX_ID))
            .join(OCPP_TAG).on(OCPP_TAG.ID_TAG.eq(TRANSACTION.ID_TAG))
            .leftJoin(USER_OCPP_TAG).on(USER_OCPP_TAG.OCPP_TAG_PK.eq(OCPP_TAG.OCPP_TAG_PK))
            .where(conditions)
            .orderBy(TRANSACTION.TRANSACTION_PK.desc())
            .fetch()
            .map(r -> Transaction.builder()
                .id(r.value1())
                .chargeBoxId(r.value2())
                .connectorId(r.value3())
                .ocppIdTag(r.value4())
                .startTimestamp(r.value5())
                .startTimestampFormatted(DateTimeUtils.humanize(r.value5()))
                .startValue(r.value6())
                .stopTimestamp(r.value7())
                .stopTimestampFormatted(DateTimeUtils.humanize(r.value7()))
                .stopValue(r.value8())
                .stopReason(r.value9())
                .chargeBoxPk(r.value10())
                .ocppTagPk(r.value11())
                .stopEventActor(r.value12())
                .userId(r.value13())
                .build()
            );
    }

    @Override
    public void writeTransactionsCSV(TransactionQueryForm form, Writer writer) {
        var conditions = getConditions(form);

        ctx.select(
                TRANSACTION.TRANSACTION_PK,
                EVSE.CHARGE_BOX_ID,
                EVSE.EVSE_ID,
                TRANSACTION.ID_TAG,
                USER_OCPP_TAG.USER_PK,
                TRANSACTION.START_TIMESTAMP,
                TRANSACTION.START_VALUE,
                TRANSACTION.STOP_TIMESTAMP,
                TRANSACTION.STOP_VALUE)
            .from(TRANSACTION)
            .join(EVSE).on(TRANSACTION.EVSE_PK.eq(EVSE.EVSE_PK))
            .join(CHARGE_BOX).on(CHARGE_BOX.CHARGE_BOX_ID.eq(EVSE.CHARGE_BOX_ID))
            .join(OCPP_TAG).on(OCPP_TAG.ID_TAG.eq(TRANSACTION.ID_TAG))
            .leftJoin(USER_OCPP_TAG).on(USER_OCPP_TAG.OCPP_TAG_PK.eq(OCPP_TAG.OCPP_TAG_PK))
            .where(conditions)
            .orderBy(TRANSACTION.TRANSACTION_PK.desc())
            .fetch()
            .formatCSV(writer);
    }

    @Override
    public List<Integer> getActiveTransactionIds(String chargeBoxId) {
        return ctx.select(TRANSACTION.TRANSACTION_PK)
                  .from(TRANSACTION)
                  .join(EVSE)
                    .on(TRANSACTION.EVSE_PK.equal(EVSE.EVSE_PK))
                    .and(EVSE.CHARGE_BOX_ID.equal(chargeBoxId))
                    .and(EVSE.TOPOLOGY_SOURCE.eq(EvseTopologySource.ocpp1))
                  .where(TRANSACTION.STOP_TIMESTAMP.isNull())
                  .fetch(TRANSACTION.TRANSACTION_PK);
    }

    @Override
    public TransactionDetails getDetails(int transactionPk, boolean energyValuesOnly) {

        // -------------------------------------------------------------------------
        // Step 1: Collect general data about transaction
        // -------------------------------------------------------------------------

        TransactionQueryForm form = new TransactionQueryForm();
        form.setTransactionPk(List.of(transactionPk));
        form.setType(TransactionQueryForm.QueryType.ALL);
        form.setPeriodType(QueryPeriodType.ALL);

        var transactions = getTransactions(form);
        if (transactions.size() != 1) {
            throw new SteveException.NotFound("There is no transaction with id '%s'", transactionPk);
        }
        Transaction transaction = transactions.getFirst();

        DateTime startTimestamp = transaction.getStartTimestamp();
        DateTime stopTimestamp = transaction.getStopTimestamp();
        String stopValue = transaction.getStopValue();
        String chargeBoxId = transaction.getChargeBoxId();
        int connectorId = transaction.getConnectorId();

        // -------------------------------------------------------------------------
        // Step 2: Collect intermediate meter values
        // -------------------------------------------------------------------------

        Condition timestampCondition;
        TransactionStartRecord nextTx = null;

        var evsePkSelect = Ocpp1ConnectorEvseBridge.evsePkSelect(ctx, chargeBoxId, connectorId);

        if (stopTimestamp == null && stopValue == null) {

            // https://github.com/steve-community/steve/issues/97
            //
            // handle "zombie" transaction, for which we did not receive any StopTransaction. if we do not handle it,
            // meter values for all subsequent transactions at this chargebox and connector will be falsely attributed
            // to this zombie transaction.
            //
            // "what is the subsequent transaction at the same chargebox and connector?"
            nextTx = ctx.selectFrom(TRANSACTION_START)
                        .where(TRANSACTION_START.EVSE_PK.eq(evsePkSelect))
                        .and(TRANSACTION_START.START_TIMESTAMP.greaterThan(startTimestamp))
                        .orderBy(TRANSACTION_START.START_TIMESTAMP)
                        .limit(1)
                        .fetchOne();

            if (nextTx == null) {
                // the last active transaction
                timestampCondition = CONNECTOR_METER_VALUE.VALUE_TIMESTAMP.greaterOrEqual(startTimestamp);
            } else {
                // startTimestamp is inclusive, nextTx.startTimestamp is exclusive
                timestampCondition = CONNECTOR_METER_VALUE.VALUE_TIMESTAMP.greaterOrEqual(startTimestamp)
                    .and(CONNECTOR_METER_VALUE.VALUE_TIMESTAMP.lessThan(nextTx.getStartTimestamp()));
            }
        } else {
            // finished transaction
            timestampCondition = CONNECTOR_METER_VALUE.VALUE_TIMESTAMP.between(startTimestamp, stopTimestamp);
        }

        // Case 1: Ideal and most accurate case. Station sends meter values with transaction id set.
        //
        // Case 2: Fall back to filtering according to time windows. Timestamp fallback only considers rows
        // not explicitly assigned to another transaction, because such rows can otherwise leak across
        // zombie-transaction boundaries.
        //
        var selectionCriteria = CONNECTOR_METER_VALUE.TRANSACTION_PK.eq(transactionPk)
            .or(timestampCondition
                .and(CONNECTOR_METER_VALUE.TRANSACTION_PK.isNull()
                .and(CONNECTOR_METER_VALUE.EVSE_PK.eq(evsePkSelect))
                )
            );

        List<Condition> conditions = new ArrayList<>();
        conditions.add(selectionCriteria);

        if (energyValuesOnly) {
            // https://github.com/steve-community/steve/issues/1514
            Condition unitCondition = CONNECTOR_METER_VALUE.UNIT.isNull()
                .or(CONNECTOR_METER_VALUE.UNIT.in("", UnitOfMeasure.WH.value(), UnitOfMeasure.K_WH.value()));

            conditions.add(unitCondition);
        }

        List<TransactionDetails.MeterValues> values =
                ctx.selectFrom(CONNECTOR_METER_VALUE)
                   .where(conditions)
                   .orderBy(CONNECTOR_METER_VALUE.VALUE_TIMESTAMP)
                   .fetch()
                   .map(r -> TransactionDetails.MeterValues.builder()
                                                           .valueTimestamp(r.getValueTimestamp())
                                                           .value(r.getValue())
                                                           .readingContext(r.getReadingContext())
                                                           .format(r.getFormat())
                                                           .measurand(r.getMeasurand())
                                                           .location(r.getLocation())
                                                           .unit(r.getUnit())
                                                           .phase(r.getPhase())
                                                           .build())
                   .stream()
                   .filter(v -> {
                       if (energyValuesOnly) {
                           return TransactionStopServiceHelper.isEnergyValue(v);
                       }
                       return true;
                   })
                   .toList();

        return new TransactionDetails(transaction, values, nextTx);
    }

    @Override
    public Result<TransactionRecord> getStoppedTransactions(@NotNull DateTime from, @NotNull DateTime to) {
        if (!to.isAfter(from)) {
            throw new SteveException.BadRequest("'to' must be after 'from'");
        }

        return ctx.selectFrom(TRANSACTION)
            .where(TRANSACTION.STOP_TIMESTAMP.ge(from)
            .and(TRANSACTION.STOP_TIMESTAMP.lt(to)))
            .orderBy(TRANSACTION.START_TIMESTAMP)
            .fetch();
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private List<Condition> getConditions(TransactionQueryForm form) {
        List<Condition> conditions = new ArrayList<>();

        conditions.add(EVSE.TOPOLOGY_SOURCE.eq(EvseTopologySource.ocpp1));

        if (form.isTransactionPkSet()) {
            conditions.add(TRANSACTION.TRANSACTION_PK.in(form.getTransactionPk()));
        }

        if (form.isChargeBoxIdSet()) {
            conditions.add(EVSE.CHARGE_BOX_ID.in(form.getChargeBoxId()));
        }

        if (form.isConnectorIdSet()) {
            conditions.add(EVSE.EVSE_ID.eq(form.getConnectorId()));
        }

        if (form.isOcppIdTagSet()) {
            conditions.add(TRANSACTION.ID_TAG.in(form.getOcppIdTag()));
        }

        if (form.isUserIdSet()) {
            conditions.add(USER_OCPP_TAG.USER_PK.in(form.getUserId()));
        }

        if (form.getType() == TransactionQueryForm.QueryType.ACTIVE) {
            conditions.add(TRANSACTION.STOP_TIMESTAMP.isNull());
        } else if (form.getType() == TransactionQueryForm.QueryType.STOPPED) {
            conditions.add(TRANSACTION.STOP_TIMESTAMP.isNotNull());
        }

        var timeCondition = getTimeCondition(TRANSACTION.START_TIMESTAMP, form, form.getPeriodType());
        if (timeCondition != null) {
            conditions.add(timeCondition);
        }
        return conditions;
    }
}
