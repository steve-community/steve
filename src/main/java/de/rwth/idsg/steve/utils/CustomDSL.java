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
package de.rwth.idsg.steve.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.DatePart;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.sql.Timestamp;

import static org.jooq.impl.DSL.field;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 03.09.2015
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CustomDSL {

    public static Field<DateTime> date(DateTime dt) {
        return date(DSL.val(dt, DateTime.class));
    }

    public static Field<DateTime> date(Field<DateTime> dt) {
        return field("date({0})", DateTime.class, dt);
    }

    /**
     * http://dev.mysql.com/doc/refman/5.7/en/pattern-matching.html
     *
     * It's not as advanced as fuzzy full-text search, but still better than nothing. DB will look
     * for all the fields that include the input. On the downside, it has the potential to be inefficient,
     * when the table is big. We should probably keep an eye on this.
     */
    public static Condition includes(Field<String> field, String input) {

        // The user can send the search parameter with empty spaces within the input. We replace this
        // with '%' since DB uses it to match any string of zero or more characters.
        //
        // The method returns a reference to the old object, when empty space does not occur in the string.
        // Therefore, no if-statement is needed.
        //
        input = input.replaceAll("\\s+", "%");

        return field.like("%" + input + "%");
    }

    public static Long selectOffsetFromUtcInSeconds(DSLContext ctx) {
        return ctx.select(timestampDiffBetweenUtcAndCurrent(DatePart.SECOND))
                  .fetchOne()
                  .getValue(0, Long.class);
    }

    private static Field<Long> timestampDiffBetweenUtcAndCurrent(DatePart part) {
        return timestampDiff(part, utcTimestamp(), DSL.currentTimestamp());
    }

    /**
     * Taken from https://github.com/jOOQ/jOOQ/issues/4303#issuecomment-105519975
     */
    private static Field<Long> timestampDiff(DatePart part, Field<Timestamp> t1, Field<Timestamp> t2) {
        return field("timestampdiff({0}, {1}, {2})", Long.class, DSL.keyword(part.toSQL()), t1, t2);
    }

    private static Field<Timestamp> utcTimestamp() {
        return field("{utc_timestamp()}", Timestamp.class);
    }
}
