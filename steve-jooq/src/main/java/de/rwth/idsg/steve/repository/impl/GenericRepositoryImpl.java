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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.DatePart;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

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
@RequiredArgsConstructor
public class GenericRepositoryImpl implements GenericRepository {

    private final DSLContext ctx;

    @EventListener
    public void afterStart(ContextRefreshedEvent event) {
        checkJavaAndMySQLOffsets();
    }

    @Override
    public void checkJavaAndMySQLOffsets() {
        var java = DateTimeUtils.getOffsetFromUtcInSeconds();

        var sql = ctx.select(timestampDiff(DatePart.SECOND, utcTimestamp(), DSL.currentTimestamp()))
                .fetchOne()
                .getValue(0, Long.class);

        if (sql == null || sql != java) {
            throw new SteveException.InternalError("MySQL and Java are not using the same time zone. "
                    + "Java offset in seconds (%s) != MySQL offset in seconds (%s)".formatted(java, sql));
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

        var row = ctx.select(
                        numChargeBoxes,
                        numOcppTags,
                        numUsers,
                        numReservations,
                        numTransactions,
                        heartbeatsToday,
                        heartbeatsYesterday,
                        heartbeatsEarlier,
                        numWebUsers)
                .fetchSingle();

        return Statistics.builder()
                .numChargeBoxes(row.get(numChargeBoxes))
                .numOcppTags(row.get(numOcppTags))
                .numUsers(row.get(numUsers))
                .numReservations(row.get(numReservations))
                .numTransactions(row.get(numTransactions))
                .heartbeatToday(row.get(heartbeatsToday))
                .heartbeatYesterday(row.get(heartbeatsYesterday))
                .heartbeatEarlier(row.get(heartbeatsEarlier))
                .numWebUsers(row.get(numWebUsers))
                .build();
    }

    @Override
    public Optional<DbVersion> getDBVersion() {
        var schemaVersion = ctx.select(SCHEMA_VERSION.VERSION, SCHEMA_VERSION.INSTALLED_ON)
                .from(SCHEMA_VERSION)
                .where(SCHEMA_VERSION.INSTALLED_RANK.eq(
                        select(max(SCHEMA_VERSION.INSTALLED_RANK)).from(SCHEMA_VERSION)))
                .fetchOne();
        if (schemaVersion == null) {
            return Optional.empty();
        }
        var ts = DateTimeUtils.humanize(schemaVersion.value2());
        return Optional.of(DbVersion.builder()
                .version(schemaVersion.value1())
                .updateTimestamp(ts)
                .build());
    }
}
