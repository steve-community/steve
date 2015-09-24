package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.repository.dto.Transaction;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import de.rwth.idsg.steve.web.dto.TransactionQueryForm;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDateTime;
import org.jooq.Configuration;
import org.jooq.Record8;
import org.jooq.RecordMapper;
import org.jooq.Result;
import org.jooq.SelectQuery;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

import static de.rwth.idsg.steve.utils.DateTimeUtils.getCurrentTimestamp;
import static de.rwth.idsg.steve.utils.DateTimeUtils.toTimestamp;
import static jooq.steve.db.tables.Connector.CONNECTOR;
import static jooq.steve.db.tables.Transaction.TRANSACTION;
import static org.jooq.impl.DSL.date;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 14.08.2014
 */
@Slf4j
@Repository
public class TransactionRepositoryImpl implements TransactionRepository {

    @Autowired
    @Qualifier("jooqConfig")
    private Configuration config;

    @Override
    public List<Transaction> getTransactions(TransactionQueryForm form) {
        return internalGetTransactions(form).map(new TransactionMapper());
    }

    @Override
    public String getTransactionsCSV(TransactionQueryForm form) {
        return internalGetTransactions(form).formatCSV();
    }

    @SuppressWarnings("unchecked")
    private Result<Record8<Integer, String, Integer, String, Timestamp, String, Timestamp, String>> internalGetTransactions(TransactionQueryForm form) {
        SelectQuery selectQuery = DSL.using(config).selectQuery();
        selectQuery.addFrom(TRANSACTION);
        selectQuery.addJoin(CONNECTOR, TRANSACTION.CONNECTOR_PK.eq(CONNECTOR.CONNECTOR_PK));
        selectQuery.addSelect(
                TRANSACTION.TRANSACTION_PK,
                CONNECTOR.CHARGEBOXID,
                CONNECTOR.CONNECTORID,
                TRANSACTION.IDTAG,
                TRANSACTION.STARTTIMESTAMP,
                TRANSACTION.STARTVALUE,
                TRANSACTION.STOPTIMESTAMP,
                TRANSACTION.STOPVALUE);

        if (form.isChargeBoxIdSet()) {
            selectQuery.addConditions(CONNECTOR.CHARGEBOXID.eq(form.getChargeBoxId()));
        }

        if (form.isUserIdSet()) {
            selectQuery.addConditions(TRANSACTION.IDTAG.eq(form.getUserId()));
        }

        if (form.getType() == TransactionQueryForm.QueryType.ACTIVE) {
            selectQuery.addConditions(TRANSACTION.STOPTIMESTAMP.isNull());
        }

        processType(selectQuery, form);

        // Default order
        selectQuery.addOrderBy(TRANSACTION.TRANSACTION_PK.desc());

        return selectQuery.fetch();
    }

    @Override
    public List<Integer> getActiveTransactionIds(String chargeBoxId) {
        return DSL.using(config)
                  .select(TRANSACTION.TRANSACTION_PK)
                  .from(TRANSACTION)
                  .join(CONNECTOR)
                    .on(TRANSACTION.CONNECTOR_PK.equal(CONNECTOR.CONNECTOR_PK))
                    .and(CONNECTOR.CHARGEBOXID.equal(chargeBoxId))
                  .where(TRANSACTION.STOPTIMESTAMP.isNull())
                  .fetch(TRANSACTION.TRANSACTION_PK);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private class TransactionMapper implements
            RecordMapper<Record8<Integer, String, Integer, String, Timestamp, String, Timestamp, String>, Transaction> {
        @Override
        public Transaction map(Record8<Integer, String, Integer, String, Timestamp, String, Timestamp, String> r) {
            return Transaction.builder()
                    .id(r.value1())
                    .chargeBoxId(r.value2())
                    .connectorId(r.value3())
                    .idTag(r.value4())
                    .startTimestamp(DateTimeUtils.humanize(r.value5()))
                    .startValue(r.value6())
                    .stopTimestamp(DateTimeUtils.humanize(r.value7()))
                    .stopValue(r.value8())
                    .build();
        }
    }

    private void processType(SelectQuery selectQuery, TransactionQueryForm form) {
        switch (form.getPeriodType()) {
            case TODAY:
                selectQuery.addConditions(
                        date(TRANSACTION.STARTTIMESTAMP).eq(date(getCurrentTimestamp()))
                );
                break;

            case LAST_10:
            case LAST_30:
            case LAST_90:
                selectQuery.addConditions(
                        date(TRANSACTION.STARTTIMESTAMP).between(
                                date(toTimestamp(new LocalDateTime().minusDays(form.getPeriodType().getInterval()))),
                                date(toTimestamp(new LocalDateTime()))
                        )
                );
                break;

            case ALL:
                break;

            case FROM_TO:
                selectQuery.addConditions(
                        TRANSACTION.STARTTIMESTAMP.between(toTimestamp(form.getFrom()), toTimestamp(form.getTo()))
                );
                break;
        }
    }
}