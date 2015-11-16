package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.repository.dto.DbVersion;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import de.rwth.idsg.steve.web.dto.Statistics;
import jooq.steve.db.routines.GetStats;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.jooq.Configuration;
import org.jooq.Record2;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Autowired
    @Qualifier("jooqConfig")
    private Configuration config;

    @Override
    public Statistics getStats() {

        // getStats is the stored procedure in our MySQL DB
        GetStats gs = new GetStats();
        gs.execute(config);

        return Statistics.builder()
                         .numChargeBoxes(gs.getNumChargeBoxes())
                         .numUsers(gs.getNumUsers())
                         .numReservations(gs.getNumReservations())
                         .numTransactions(gs.getNumTransactions())
                         .heartbeatToday(gs.getHeartbeatsToday())
                         .heartbeatYesterday(gs.getHeartbeatsYesterday())
                         .heartbeatEarlier(gs.getHeartbeatsEarlier())
                         .connAvailable(gs.getConnectorsAvailable())
                         .connOccupied(gs.getConnectorsOccupied())
                         .connFaulted(gs.getConnectorsFaulted())
                         .connUnavailable(gs.getConnectorsUnavailable())
                         .build();
    }

    @Override
    public DbVersion getDBVersion() {
        Record2<String, DateTime> record = DSL.using(config)
                                              .select(SCHEMA_VERSION.VERSION, SCHEMA_VERSION.INSTALLED_ON)
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
