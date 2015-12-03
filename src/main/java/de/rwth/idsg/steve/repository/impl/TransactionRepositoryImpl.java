package de.rwth.idsg.steve.repository.impl;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.TransactionRepository;
import de.rwth.idsg.steve.repository.dto.Transaction;
import de.rwth.idsg.steve.utils.CustomDSL;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import de.rwth.idsg.steve.web.dto.TransactionQueryForm;
import org.joda.time.DateTime;
import org.jooq.Configuration;
import org.jooq.Record10;
import org.jooq.Record8;
import org.jooq.SelectQuery;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.io.Writer;
import java.util.List;

import static de.rwth.idsg.steve.utils.CustomDSL.date;
import static jooq.steve.db.tables.ChargeBox.CHARGE_BOX;
import static jooq.steve.db.tables.Connector.CONNECTOR;
import static jooq.steve.db.tables.OcppTag.OCPP_TAG;
import static jooq.steve.db.tables.Transaction.TRANSACTION;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 14.08.2014
 */
@Repository
public class TransactionRepositoryImpl implements TransactionRepository {

    @Autowired
    @Qualifier("jooqConfig")
    private Configuration config;

    @Override
    @SuppressWarnings("unchecked")
    public List<Transaction> getTransactions(TransactionQueryForm form) {
        return getInternal(form).fetch()
                                .map(r -> Transaction.builder()
                                                     .id(r.value1())
                                                     .chargeBoxId(r.value2())
                                                     .connectorId(r.value3())
                                                     .ocppIdTag(r.value4())
                                                     .startTimestamp(DateTimeUtils.humanize(r.value5()))
                                                     .startValue(r.value6())
                                                     .stopTimestamp(DateTimeUtils.humanize(r.value7()))
                                                     .stopValue(r.value8())
                                                     .chargeBoxPk(r.value9())
                                                     .ocppTagPk(r.value10())
                                                     .build());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void writeTransactionsCSV(TransactionQueryForm form, Writer writer) {
        getInternalCSV(form).fetch()
                            .formatCSV(writer);
    }

    @Override
    public List<Integer> getActiveTransactionIds(String chargeBoxId) {
        return DSL.using(config)
                  .select(TRANSACTION.TRANSACTION_PK)
                  .from(TRANSACTION)
                  .join(CONNECTOR)
                    .on(TRANSACTION.CONNECTOR_PK.equal(CONNECTOR.CONNECTOR_PK))
                    .and(CONNECTOR.CHARGE_BOX_ID.equal(chargeBoxId))
                  .where(TRANSACTION.STOP_TIMESTAMP.isNull())
                  .fetch(TRANSACTION.TRANSACTION_PK);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private
    SelectQuery<Record8<Integer, String, Integer, String, DateTime, String, DateTime, String> >
    getInternalCSV(TransactionQueryForm form) {

        SelectQuery selectQuery = DSL.using(config).selectQuery();
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
                TRANSACTION.STOP_VALUE
        );

        return addConditions(selectQuery, form);
    }

    /**
     * Difference from getInternalCSV:
     * Joins with CHARGE_BOX and OCPP_TAG tables, selects CHARGE_BOX_PK and OCPP_TAG_PK additionally
     */
    @SuppressWarnings("unchecked")
    private
    SelectQuery<Record10<Integer, String, Integer, String, DateTime, String, DateTime, String, Integer, Integer>>
    getInternal(TransactionQueryForm form) {

        SelectQuery selectQuery = DSL.using(config).selectQuery();
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
                        date(TRANSACTION.START_TIMESTAMP).eq(date(CustomDSL.utcTimestamp()))
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
}
