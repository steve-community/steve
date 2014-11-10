package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.repository.dto.DbVersion;
import de.rwth.idsg.steve.repository.dto.Statistics;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import jooq.steve.db.routines.Getstats;
import jooq.steve.db.tables.records.DbversionRecord;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Configuration;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import static jooq.steve.db.tables.Dbversion.DBVERSION;

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
    public final Statistics getStats() {

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
     * SELECT version, updateTimestamp
     * FROM dbVersion
     */
    @Override
    public final DbVersion getDBVersion() {
        DbversionRecord record = DSL.using(config)
                                    .selectFrom(DBVERSION)
                                    .fetchOne();

        String ts = DateTimeUtils.humanize(record.getUpdatetimestamp());
        return DbVersion.builder()
                .version(record.getVersion())
                .updateTimestamp(ts)
                .build();
    }
}