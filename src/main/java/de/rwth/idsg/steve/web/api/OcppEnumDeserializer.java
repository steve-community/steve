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

import jakarta.xml.bind.annotation.XmlEnumValue;
import org.jetbrains.annotations.Nullable;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.std.StdDeserializer;

import java.util.Arrays;

/**
 * Deserialises OCPP enums from their Java constant name ("TX_PROFILE") or from
 * the OCPP wire value ("TxProfile").
 *
 * REST resources return OCPP values (they are what is stored in the database
 * and what the OCPP wire format uses), so accepting the same spelling on
 * request bodies keeps the API symmetric.
 */
public class OcppEnumDeserializer extends StdDeserializer<Enum<?>> {

    private final Class<?> enumType;

    public OcppEnumDeserializer() {
        super(Enum.class);
        this.enumType = null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private OcppEnumDeserializer(Class<?> enumType) {
        super((Class<Enum>) enumType);
        this.enumType = enumType;
    }

    @Override
    public ValueDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
        JavaType type = property != null ? property.getType() : ctxt.getContextualType();
        Class<?> raw = type != null ? type.getRawClass() : null;
        if (raw != null && raw.isEnum()) {
            return new OcppEnumDeserializer(raw);
        }
        return this;
    }

    @Override
    public Enum<?> deserialize(JsonParser parser, DeserializationContext context) {
        String text = parser.getValueAsString();
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        String trimmed = text.trim();
        Class<?> enumType = this.enumType;
        if (enumType == null && context.getContextualType() != null) {
            enumType = context.getContextualType().getRawClass();
        }
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
        // Resources return the OCPP wire values stored in the database (e.g. "TxProfile"),
        // which come from @XmlEnumValue on the generated constants, not the constant names.
        for (Object constant : enumType.getEnumConstants()) {
            Enum<?> e = (Enum<?>) constant;
            String wireValue = wireValueOf(enumType, e);
            if (wireValue != null && wireValue.equalsIgnoreCase(trimmed)) {
                return e;
            }
        }
        throw context.weirdStringException(
            trimmed, enumType, "No enum constant matches value " + trimmed
                + " (available: " + Arrays.toString(enumType.getEnumConstants()) + ")");
    }

    @Nullable
    private static String wireValueOf(Class<?> enumType, Enum<?> constant) {
        try {
            XmlEnumValue ann = enumType.getField(constant.name()).getAnnotation(XmlEnumValue.class);
            return ann != null ? ann.value() : null;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }
}
