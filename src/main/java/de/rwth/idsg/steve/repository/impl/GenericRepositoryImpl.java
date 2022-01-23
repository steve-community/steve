/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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

import de.rwth.idsg.steve.repository.GenericRepository;
import de.rwth.idsg.steve.repository.ReservationStatus;
import de.rwth.idsg.steve.repository.dto.DbVersion;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import de.rwth.idsg.steve.web.dto.Statistics;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Record8;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static de.rwth.idsg.steve.utils.CustomDSL.date;
import static jooq.steve.db.Tables.RESERVATION;
import static jooq.steve.db.Tables.TRANSACTION;
import static jooq.steve.db.tables.ChargeBox.CHARGE_BOX;
import static jooq.steve.db.tables.OcppTag.OCPP_TAG;
import static jooq.steve.db.tables.SchemaVersion.SCHEMA_VERSION;
import static jooq.steve.db.tables.User.USER;
import static org.jooq.impl.DSL.max;
import static org.jooq.impl.DSL.select;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 14.08.2014
 */
@Slf4j
@Repository
public class GenericRepositoryImpl implements GenericRepository {

    @Autowired private DSLContext ctx;

    @Override
    public Statistics getStats() {
        DateTime now = DateTime.now();
        DateTime yesterdaysNow = now.minusDays(1);

        Field<Integer> numChargeBoxes =
                ctx.selectCount()
                   .from(CHARGE_BOX)
                   .asField("num_charge_boxes");

        Field<Integer> numOcppTags =
                ctx.selectCount()
                   .from(OCPP_TAG)
                   .asField("num_ocpp_tags");

        Field<Integer> numUsers =
                ctx.selectCount()
                   .from(USER)
                   .asField("num_users");

        Field<Integer> numReservations =
                ctx.selectCount()
                   .from(RESERVATION)
                   .where(RESERVATION.EXPIRY_DATETIME.greaterThan(now))
                   .and(RESERVATION.STATUS.eq(ReservationStatus.ACCEPTED.name()))
                   .asField("num_reservations");

        Field<Integer> numTransactions =
                ctx.selectCount()
                   .from(TRANSACTION)
                   .where(TRANSACTION.STOP_TIMESTAMP.isNull())
                   .asField("num_transactions");

        Field<Integer> heartbeatsToday =
                ctx.selectCount()
                   .from(CHARGE_BOX)
                   .where(date(CHARGE_BOX.LAST_HEARTBEAT_TIMESTAMP).eq(date(now)))
                   .asField("heartbeats_today");

        Field<Integer> heartbeatsYesterday =
                ctx.selectCount()
                   .from(CHARGE_BOX)
                   .where(date(CHARGE_BOX.LAST_HEARTBEAT_TIMESTAMP).eq(date(yesterdaysNow)))
                   .asField("heartbeats_yesterday");

        Field<Integer> heartbeatsEarlier =
                ctx.selectCount()
                   .from(CHARGE_BOX)
                   .where(date(CHARGE_BOX.LAST_HEARTBEAT_TIMESTAMP).lessThan(date(yesterdaysNow)))
                   .asField("heartbeats_earlier");

        Record8<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> gs =
                ctx.select(
                        numChargeBoxes,
                        numOcppTags,
                        numUsers,
                        numReservations,
                        numTransactions,
                        heartbeatsToday,
                        heartbeatsYesterday,
                        heartbeatsEarlier
                ).fetchOne();

        return Statistics.builder()
                         .numChargeBoxes(gs.value1())
                         .numOcppTags(gs.value2())
                         .numUsers(gs.value3())
                         .numReservations(gs.value4())
                         .numTransactions(gs.value5())
                         .heartbeatToday(gs.value6())
                         .heartbeatYesterday(gs.value7())
                         .heartbeatEarlier(gs.value8())
                         .build();
    }

    @Override
    public DbVersion getDBVersion() {
        Record2<String, DateTime> record = ctx.select(SCHEMA_VERSION.VERSION, SCHEMA_VERSION.INSTALLED_ON)
                                              .from(SCHEMA_VERSION)
                                              .where(SCHEMA_VERSION.INSTALLED_RANK.eq(
                                                      select(max(SCHEMA_VERSION.INSTALLED_RANK)).from(SCHEMA_VERSION)))
                                              .fetchOne();

        String ts = DateTimeUtils.humanize(record.value2());
        return DbVersion.builder()
                        .version(record.value1())
                        .updateTimestamp(ts)
                        .build();
    }
}
