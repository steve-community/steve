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
package de.rwth.idsg.ocpp.jaxb;

import de.rwth.idsg.ocpp.DateTimeUtils;
import org.jspecify.annotations.Nullable;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * JavaTime and XSD represent data and time information according to ISO 8601.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 20.10.2014
 */
public class JavaDateTimeConverter extends XmlAdapter<String, OffsetDateTime> {

    private final ZoneId fallbackZoneId;
    private final boolean marchallToUtc;

    public JavaDateTimeConverter() {
        this(
                ZoneId.systemDefault(),
                System.getProperty("steve.ocpp.marshall-to-utc", "true").equals("true"));
    }

    public JavaDateTimeConverter(ZoneId fallbackZoneId, boolean marchallToUtc) {
        this.fallbackZoneId = fallbackZoneId;
        this.marchallToUtc = marchallToUtc;
    }

    @Override
    public @Nullable OffsetDateTime unmarshal(@Nullable String v) {
        return DateTimeUtils.toOffsetDateTime(v, fallbackZoneId);
    }

    @Override
    public @Nullable String marshal(@Nullable OffsetDateTime v) {
        return DateTimeUtils.toString(v, marchallToUtc ? null : fallbackZoneId);
    }
}
