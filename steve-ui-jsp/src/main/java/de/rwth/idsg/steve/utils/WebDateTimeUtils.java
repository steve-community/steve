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

import com.google.common.base.Strings;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.TimeZone;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WebDateTimeUtils {

    public static long toMillis(LocalDateTime ldt) {
        return ldt == null ? 0 : ldt.atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

    public static long toMillis(OffsetDateTime odt) {
        return odt == null ? 0 : odt.toInstant().toEpochMilli();
    }

    public static long toMillis(Instant instant) {
        return instant == null ? 0 : instant.toEpochMilli();
    }

    public static ZoneOffset resolveZoneFromRequest(NativeWebRequest request) {
        var defaultTimeZone = ZoneOffset.systemDefault().getRules().getOffset(Instant.now());

        // Header X-Timezone (IANA zone ex: Europe/Paris)
        String zoneId = request.getHeader("X-Timezone");
        if (!Strings.isNullOrEmpty(zoneId)) {
            try {
                return toOffset(ZoneId.of(zoneId));
            } catch (Exception e) {
                log.warn("Cannot parse ZoneId from request header: {}", zoneId, e);
            }
        }

        // Fallback: TimeZone known by Spring
        HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
        if (servletRequest != null) {
            TimeZone tzFromSpring = RequestContextUtils.getTimeZone(servletRequest);
            if (tzFromSpring != null) {
                return toOffset(tzFromSpring.toZoneId());
            }
        }

        // Fallback: default
        return defaultTimeZone;
    }

    private static ZoneOffset toOffset(ZoneId zoneId) {
        return zoneId.getRules().getOffset(LocalDateTime.now());
    }
}
