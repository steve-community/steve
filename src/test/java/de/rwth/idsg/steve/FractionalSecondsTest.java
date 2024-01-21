/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2024 SteVe Community Team
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
package de.rwth.idsg.steve;

import de.rwth.idsg.steve.config.BeanConfiguration;
import jooq.steve.db.tables.records.ChargeBoxRecord;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static jooq.steve.db.Tables.CHARGE_BOX;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 21.01.2024
 */
public class FractionalSecondsTest {

    @BeforeAll
    public static void initClass() {
        Assertions.assertEquals(ApplicationProfile.TEST, SteveConfiguration.CONFIG.getProfile());
    }

    public static Stream<String> provideInputDateTimes() {
        return Stream.of(
            "2024-01-21T05:06:07.000Z",
            "2024-01-21T05:06:07.123Z"
        );
    }

    @ParameterizedTest
    @MethodSource("provideInputDateTimes")
    public void testPrecisionInJava(String dateTimeString) {
        DateTime dtIn = DateTime.parse(dateTimeString);
        Assertions.assertEquals(dateTimeString, dtIn.toString());

        DateTime dtOut = DateTime.parse(dtIn.toString());
        Assertions.assertEquals(dateTimeString, dtOut.toString());

        Assertions.assertEquals(dtIn, dtOut);
    }

    @ParameterizedTest
    @MethodSource("provideInputDateTimes")
    public void testPrecisionWithDatabase(String dateTimeString) {
        DateTime dtIn = DateTime.parse(dateTimeString);
        DateTime dtOut = insertAndGetDateTime(dtIn);

        Assertions.assertTrue(dtIn.isEqual(dtOut), () -> "Expected: " + dtIn + " vs actual: " + dtOut);
    }

    /**
     * Persist some DateTime in some table in DB (just to have DB evaluate it) and
     * get the evaluated version back for further checks.
     */
    private static DateTime insertAndGetDateTime(DateTime dtIn) {
        String chargeBoxId = UUID.randomUUID().toString();

        DSLContext ctx = new BeanConfiguration().dslContext();

        // 1. insert
        ctx.insertInto(CHARGE_BOX)
            .set(CHARGE_BOX.CHARGE_BOX_ID, chargeBoxId)
            .set(CHARGE_BOX.LAST_HEARTBEAT_TIMESTAMP, dtIn)
            .execute();

        // 2. read
        Result<ChargeBoxRecord> rows = ctx.selectFrom(CHARGE_BOX)
            .where(CHARGE_BOX.CHARGE_BOX_ID.eq(chargeBoxId))
            .fetch();

        ChargeBoxRecord chargeBoxRecord = rows.get(0);

        // 2. clean-up
        ctx.deleteFrom(CHARGE_BOX)
            .where(CHARGE_BOX.CHARGE_BOX_ID.eq(chargeBoxId))
            .execute();

        return chargeBoxRecord.getLastHeartbeatTimestamp();
    }
}
