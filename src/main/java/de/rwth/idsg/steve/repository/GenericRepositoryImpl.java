package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.repository.dto.DbVersion;
import de.rwth.idsg.steve.repository.dto.Statistics;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import jooq.steve.db.routines.Getstats;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Configuration;
import org.jooq.Record2;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

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

    /**
     * {CALL getStats(?,?,?,?,?,?,?,?,?,?,?)}
     */
    @Override
    public Statistics getStats() {

        // getStats is the stored procedure in our MySQL DB
        Getstats gs = new Getstats();
        gs.execute(config);

        return Statistics.builder()
                .numChargeBoxes(gs.getNumchargeboxes())
                .numUsers(gs.getNumusers())
                .numReservations(gs.getNumreservs())
                .numTransactions(gs.getNumtranses())
                .heartbeatToday(gs.getHeartbeattoday())
                .heartbeatYesterday(gs.getHeartbeatyester())
                .heartbeatEarlier(gs.getHeartbeatearl())
                .connAvailable(gs.getConnavail())
                .connOccupied(gs.getConnocc())
                .connFaulted(gs.getConnfault())
                .connUnavailable(gs.getConnunavail())
                .build();
    }

    /**
     * SELECT version, installed_on
     * FROM schema_version
     * WHERE version_rank = (SELECT MAX(version_rank) FROM schema_version)
     */
    @Override
    public DbVersion getDBVersion() {
        Record2<String, Timestamp> record = DSL.using(config)
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