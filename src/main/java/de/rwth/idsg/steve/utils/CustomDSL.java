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
package de.rwth.idsg.steve.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.jooq.Condition;
import org.jooq.DataType;
import org.jooq.DatePart;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import java.sql.Timestamp;

import static org.jooq.impl.DSL.field;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 03.09.2015
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CustomDSL {

    // https://github.com/steve-community/steve/issues/1520
    private static final DataType<DateTime> DATE_TIME_TYPE = SQLDataType.TIMESTAMP.asConvertedDataType(new DateTimeConverter());

    public static Field<DateTime> date(DateTime dt) {
        return date(DSL.val(dt, DATE_TIME_TYPE));
    }

    public static Field<DateTime> date(Field<DateTime> dt) {
        return field("date({0})", DATE_TIME_TYPE, dt);
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

    /**
     * Taken from https://github.com/jOOQ/jOOQ/issues/4303#issuecomment-105519975
     */
    public static Field<Long> timestampDiff(DatePart part, Field<Timestamp> t1, Field<Timestamp> t2) {
        return field("timestampdiff({0}, {1}, {2})", Long.class, DSL.keyword(part.toSQL()), t1, t2);
    }

    public static Field<Timestamp> utcTimestamp() {
        return field("{utc_timestamp()}", Timestamp.class);
    }
}
