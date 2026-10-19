/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
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
package de.rwth.idsg.steve.web.api;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Arrays;

/**
 * Deserialises OCPP enums from their Java constant name ("TX_PROFILE") or from
 * the OCPP value ("TxProfile").
 *
 * REST resources return OCPP values (they are what is stored in the database
 * and what the OCPP wire format uses), so accepting the same spelling on
 * request bodies keeps the API symmetric.
 */
public class OcppEnumDeserializer extends StdDeserializer<Enum<?>> {

    public OcppEnumDeserializer() {
        super(Enum.class);
    }

    @Override
    public Enum<?> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String text = parser.getValueAsString();
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        String trimmed = text.trim();
        Class<?> enumType = context.getContextualType() != null
            ? context.getContextualType().getRawClass()
            : null;
        if (enumType == null || !enumType.isEnum()) {
            throw context.weirdStringException(
                trimmed, Enum.class, "Cannot determine target enum type");
        }
        for (Object constant : enumType.getEnumConstants()) {
            Enum<?> e = (Enum<?>) constant;
            if (e.name().equalsIgnoreCase(trimmed)) {
                return e;
            }
        }
        throw context.weirdStringException(
            trimmed, enumType, "No enum constant matches value " + trimmed
                + " (available: " + Arrays.toString(enumType.getEnumConstants()) + ")");
    }
}
