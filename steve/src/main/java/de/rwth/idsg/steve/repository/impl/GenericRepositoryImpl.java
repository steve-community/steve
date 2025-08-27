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
import de.rwth.idsg.steve.repository.GenericRepository;
import de.rwth.idsg.steve.repository.ReservationStatus;
import de.rwth.idsg.steve.repository.dto.DbVersion;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import de.rwth.idsg.steve.web.dto.Statistics;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.DatePart;
import org.jooq.Field;
import org.jooq.Record9;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import static de.rwth.idsg.steve.utils.CustomDSL.date;
import static de.rwth.idsg.steve.utils.CustomDSL.timestampDiff;
import static de.rwth.idsg.steve.utils.CustomDSL.utcTimestamp;
import static jooq.steve.db.Tables.RESERVATION;
import static jooq.steve.db.Tables.TRANSACTION;
import static jooq.steve.db.tables.ChargeBox.CHARGE_BOX;
import static jooq.steve.db.tables.OcppTag.OCPP_TAG;
import static jooq.steve.db.tables.SchemaVersion.SCHEMA_VERSION;
import static jooq.steve.db.tables.User.USER;
import static jooq.steve.db.tables.WebUser.WEB_USER;
import static org.jooq.impl.DSL.max;
import static org.jooq.impl.DSL.select;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 14.08.2014
 */
@Slf4j
@Repository
public class GenericRepositoryImpl implements GenericRepository {

    @Autowired
    private DSLContext ctx;

    @EventListener
    public void afterStart(ContextRefreshedEvent event) {
        checkJavaAndMySQLOffsets();
    }

    @Override
    public void checkJavaAndMySQLOffsets() {
        long java = DateTimeUtils.getOffsetFromUtcInSeconds();

        long sql = ctx.select(timestampDiff(DatePart.SECOND, utcTimestamp(), DSL.currentTimestamp()))
                .fetchOne()
                .getValue(0, Long.class);

        if (sql != java) {
            throw new SteveException(
                    "MySQL and Java are not using the same time zone. "
                            + "Java offset in seconds (%s) != MySQL offset in seconds (%s)",
                    java, sql);
        }
    }

    @Override
    public Statistics getStats() {
        var now = LocalDateTime.now();
        var today = now.toLocalDate();
        var yesterday = today.minusDays(1);

        Field<Integer> numChargeBoxes = ctx.selectCount().from(CHARGE_BOX).asField("num_charge_boxes");

        Field<Integer> numOcppTags = ctx.selectCount().from(OCPP_TAG).asField("num_ocpp_tags");

        Field<Integer> numUsers = ctx.selectCount().from(USER).asField("num_users");

        Field<Integer> numReservations = ctx.selectCount()
                .from(RESERVATION)
                .where(RESERVATION.EXPIRY_DATETIME.greaterThan(now))
                .and(RESERVATION.STATUS.eq(ReservationStatus.ACCEPTED.name()))
                .asField("num_reservations");

        Field<Integer> numTransactions = ctx.selectCount()
                .from(TRANSACTION)
                .where(TRANSACTION.STOP_TIMESTAMP.isNull())
                .asField("num_transactions");

        Field<Integer> heartbeatsToday = ctx.selectCount()
                .from(CHARGE_BOX)
                .where(date(CHARGE_BOX.LAST_HEARTBEAT_TIMESTAMP).eq(today))
                .asField("heartbeats_today");

        Field<Integer> heartbeatsYesterday = ctx.selectCount()
                .from(CHARGE_BOX)
                .where(date(CHARGE_BOX.LAST_HEARTBEAT_TIMESTAMP).eq(yesterday))
                .asField("heartbeats_yesterday");

        Field<Integer> heartbeatsEarlier = ctx.selectCount()
                .from(CHARGE_BOX)
                .where(date(CHARGE_BOX.LAST_HEARTBEAT_TIMESTAMP).lessThan(yesterday))
                .asField("heartbeats_earlier");

        Field<Integer> numWebUsers = ctx.selectCount().from(WEB_USER).asField("num_webusers");

        Record9<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> gs = ctx.select(
                        numChargeBoxes,
                        numOcppTags,
                        numUsers,
                        numReservations,
                        numTransactions,
                        heartbeatsToday,
                        heartbeatsYesterday,
                        heartbeatsEarlier,
                        numWebUsers)
                .fetchOne();

        return Statistics.builder()
                .numChargeBoxes(gs.value1())
                .numOcppTags(gs.value2())
                .numUsers(gs.value3())
                .numReservations(gs.value4())
                .numTransactions(gs.value5())
                .heartbeatToday(gs.value6())
                .heartbeatYesterday(gs.value7())
                .heartbeatEarlier(gs.value8())
                .numWebUsers(gs.value9())
                .build();
    }

    @Override
    public DbVersion getDBVersion() {
        var record = ctx.select(SCHEMA_VERSION.VERSION, SCHEMA_VERSION.INSTALLED_ON)
                .from(SCHEMA_VERSION)
                .where(SCHEMA_VERSION.INSTALLED_RANK.eq(
                        select(max(SCHEMA_VERSION.INSTALLED_RANK)).from(SCHEMA_VERSION)))
                .fetchOne();

        String ts = DateTimeUtils.humanize(record.value2());
        return DbVersion.builder().version(record.value1()).updateTimestamp(ts).build();
    }
}
