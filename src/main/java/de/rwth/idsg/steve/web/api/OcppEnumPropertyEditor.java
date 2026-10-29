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

import java.beans.PropertyEditorSupport;

/**
 * Binds OCPP enum query parameters by their Java constant name ("TX_PROFILE")
 * or by the OCPP wire value they serialise from/to ("TxProfile").
 *
 * The REST layer returns OCPP values (e.g. profilePurpose "TxProfile"), so a
 * client that filters with a value it just read would otherwise get a binding
 * error. This editor accepts both spellings.
 */
public class OcppEnumPropertyEditor extends PropertyEditorSupport {

    private final Class<? extends Enum<?>> enumType;

    public OcppEnumPropertyEditor(Class<? extends Enum<?>> enumType) {
        this.enumType = enumType;
    }

    @Override
    public void setAsText(String text) {
        if (text == null || text.trim().isEmpty()) {
            setValue(null);
            return;
        }
        String trimmed = text.trim();
        for (Enum<?> constant : enumType.getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(trimmed)) {
                setValue(constant);
                return;
            }
        }
        for (Enum<?> constant : enumType.getEnumConstants()) {
            String wireValue = wireValueOf(constant);
            if (wireValue != null && wireValue.equalsIgnoreCase(trimmed)) {
                setValue(constant);
                return;
            }
        }
        throw new IllegalArgumentException(
            "No value " + trimmed + " for enum " + enumType.getSimpleName());
    }

    @Nullable
    private String wireValueOf(Enum<?> constant) {
        try {
            XmlEnumValue ann = enumType.getField(constant.name()).getAnnotation(XmlEnumValue.class);
            return ann != null ? ann.value() : null;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }
}
