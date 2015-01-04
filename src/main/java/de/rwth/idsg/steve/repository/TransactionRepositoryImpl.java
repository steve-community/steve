package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.repository.dto.Transaction;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Configuration;
import org.jooq.Record8;
import org.jooq.RecordMapper;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

import static jooq.steve.db.tables.Connector.CONNECTOR;
import static jooq.steve.db.tables.Transaction.TRANSACTION;

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
    public List<Transaction> getTransactions() {
        return internalGetTransactions().map(new TransactionMapper());
    }

    @Override
    public String getTransactionsCSV() {
        return internalGetTransactions().formatCSV();
    }

    /**
     * SELECT transaction.transaction_pk,
     *        connector.chargeBoxId,
     *        connector.connectorId,
     *        transaction.idTag,
     *        transaction.startTimestamp,
     *        transaction.startValue,
     *        transaction.stopTimestamp,
     *        transaction.stopValue
     * FROM transaction
     * JOIN connector
     *      ON transaction.connector_pk = connector.connector_pk
     */
    private Result<Record8<Integer, String, Integer, String, Timestamp, String, Timestamp, String>> internalGetTransactions() {
        return DSL.using(config)
                .select(TRANSACTION.TRANSACTION_PK,
                        CONNECTOR.CHARGEBOXID,
                        CONNECTOR.CONNECTORID,
                        TRANSACTION.IDTAG,
                        TRANSACTION.STARTTIMESTAMP,
                        TRANSACTION.STARTVALUE,
                        TRANSACTION.STOPTIMESTAMP,
                        TRANSACTION.STOPVALUE)
                .from(TRANSACTION)
                .join(CONNECTOR).onKey()
                .fetch();
    }

    /**
     * SELECT transaction.transaction_pk FROM transaction
     * JOIN connector
     *      ON transaction.connector_pk = connector.connector_pk
     *      AND chargeBoxId = ?
     * WHERE stopTimestamp IS NULL
     */
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
}