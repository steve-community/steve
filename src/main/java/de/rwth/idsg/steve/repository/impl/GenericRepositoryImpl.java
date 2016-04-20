package de.rwth.idsg.steve.repository.impl;

import de.rwth.idsg.steve.repository.GenericRepository;
import de.rwth.idsg.steve.repository.dto.DbVersion;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import de.rwth.idsg.steve.web.dto.Statistics;
import jooq.steve.db.routines.GetStats;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static jooq.steve.db.tables.SchemaVersion.SCHEMA_VERSION;
import static org.jooq.impl.DSL.max;
import static org.jooq.impl.DSL.select;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 14.08.2014
 */
@Slf4j
@Repository
public class GenericRepositoryImpl implements GenericRepository {

    @Autowired private DSLContext ctx;

    @Override
    public Statistics getStats() {

        // getStats is the stored procedure in our MySQL DB
        GetStats gs = new GetStats();
        gs.execute(ctx.configuration());

        return Statistics.builder()
                         .numChargeBoxes(gs.getNumChargeBoxes())
                         .numOcppTags(gs.getNumOcppTags())
                         .numUsers(gs.getNumUsers())
                         .numReservations(gs.getNumReservations())
                         .numTransactions(gs.getNumTransactions())
                         .heartbeatToday(gs.getHeartbeatsToday())
                         .heartbeatYesterday(gs.getHeartbeatsYesterday())
                         .heartbeatEarlier(gs.getHeartbeatsEarlier())
                         .build();
    }

    @Override
    public DbVersion getDBVersion() {
        Record2<String, DateTime> record = ctx.select(SCHEMA_VERSION.VERSION, SCHEMA_VERSION.INSTALLED_ON)
                                              .from(SCHEMA_VERSION)
                                              .where(SCHEMA_VERSION.VERSION_RANK.eq(
                                                      select(max(SCHEMA_VERSION.VERSION_RANK)).from(SCHEMA_VERSION)))
                                              .fetchOne();

        String ts = DateTimeUtils.humanize(record.value2());
        return DbVersion.builder()
                        .version(record.value1())
                        .updateTimestamp(ts)
                        .build();
    }
}
