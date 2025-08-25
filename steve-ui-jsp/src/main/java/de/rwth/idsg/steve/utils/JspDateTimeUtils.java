package de.rwth.idsg.steve.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JspDateTimeUtils {

    public static long toMillis(LocalDateTime ldt) {
        return ldt == null ? 0 : ldt.atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

    public static long toMillis(OffsetDateTime ldt) {
        return ldt == null ? 0 : ldt.toInstant().toEpochMilli();
    }
}
