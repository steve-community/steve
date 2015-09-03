package de.rwth.idsg.steve.utils;

import org.jooq.Field;
import org.jooq.impl.SQLDataType;

import java.sql.Timestamp;

import static org.jooq.impl.DSL.field;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 03.09.2015
 */
public final class CustomDSL {
    private CustomDSL() {}

    public static Field<Timestamp> utcTimestamp() {
        return field("{utc_timestamp}", SQLDataType.TIMESTAMP);
    }
}
