package de.rwth.idsg.steve.utils;

import org.joda.time.DateTime;
import org.jooq.Field;
import org.jooq.impl.DSL;

import static org.jooq.impl.DSL.field;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 03.09.2015
 */
public final class CustomDSL {
    private CustomDSL() { }

    public static Field<DateTime> utcTimestamp() {
        return field("{utc_timestamp(6)}", DateTime.class);
    }

    public static Field<DateTime> date(DateTime dt) {
        return date(DSL.val(dt, DateTime.class));
    }

    public static Field<DateTime> date(Field<DateTime> dt) {
        return field("date({0})", DateTime.class, dt);
    }

    /**
     * http://dev.mysql.com/doc/refman/5.6/en/information-functions.html#function_last-insert-id
     */
    public static Field<Integer> lastInsertId() {
        return field("last_insert_id()", Integer.class);
    }
}
