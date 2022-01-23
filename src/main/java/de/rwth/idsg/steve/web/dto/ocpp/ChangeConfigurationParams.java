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
package de.rwth.idsg.steve.web.dto.ocpp;

import com.google.common.base.Strings;
import de.rwth.idsg.steve.SteveException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Objects;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 08.03.2018
 */
@Getter
@Setter
public class ChangeConfigurationParams extends MultipleChargePointSelect {

    private String confKey;

    private String customConfKey;

    @NotNull(message = "Key type is required")
    private ConfigurationKeyType keyType = ConfigurationKeyType.PREDEFINED;

    // Disabled @NotBlank after https://github.com/RWTH-i5-IDSG/steve/issues/148
    // @NotBlank(message = "Value is required")
    @Pattern(regexp = "\\S+", message = "Value cannot contain any whitespace")
    private String value;

    @AssertTrue(message = "Custom Configuration Key cannot be left blank")
    public boolean isValidCustom() {
        if (keyType == ConfigurationKeyType.CUSTOM) {
            return !Strings.isNullOrEmpty(customConfKey);
        } else {
            return true;
        }
    }

    @AssertTrue(message = "Configuration Key is required")
    public boolean isValidPredefined() {
        if (keyType == ConfigurationKeyType.PREDEFINED) {
            return confKey != null;
        } else {
            return true;
        }
    }

    public String getKey() {
        if (keyType == ConfigurationKeyType.PREDEFINED) {
            return confKey;
        } else if (keyType == ConfigurationKeyType.CUSTOM) {
            return customConfKey;
        }

        // This should not happen
        throw new SteveException("Cannot determine key (KeyType in illegal state)");
    }

    /**
     * Because we want to permit empty values
     *
     * https://github.com/RWTH-i5-IDSG/steve/issues/148
     */
    public String getValue() {
        return Objects.requireNonNullElse(value, "");
    }

    // -------------------------------------------------------------------------
    // Enum
    // -------------------------------------------------------------------------

    @RequiredArgsConstructor
    private enum ConfigurationKeyType {
        PREDEFINED("Predefined"),
        CUSTOM("Custom");

        @Getter private final String value;

        public static ConfigurationKeyType fromValue(String v) {
            for (ConfigurationKeyType c : ConfigurationKeyType.values()) {
                if (c.value.equals(v)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(v);
        }
    }

}
