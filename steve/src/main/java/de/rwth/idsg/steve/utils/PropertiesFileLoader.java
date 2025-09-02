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
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

/**
 * Encapsulates java.util.Properties and adds type specific convenience methods
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 01.10.2015
 */
@Slf4j
public class PropertiesFileLoader {

    private final Properties prop;

    /**
     * The name parameter acts as
     * 1) the file name to load from classpath, and
     * 2) the system property which can be set to load from file system.
     */
    public PropertiesFileLoader(String name) {
        var externalFileName = System.getProperty(name);

        if (externalFileName == null) {
            log.info(
                    "Hint: The Java system property '{}' can be set to point to an external properties file, "
                            + "which will be prioritized over the bundled one",
                    name);
            prop = loadFromClasspath(name);
        } else {
            prop = loadFromSystem(externalFileName);
        }
    }

    // -------------------------------------------------------------------------
    // Strict
    // -------------------------------------------------------------------------

    public String getString(String key) {
        var value = getOptionalString(key).orElse(null);
        checkForNullAndEmpty(key, value);
        return value;
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(getString(key));
    }

    public int getInt(String key) {
        return Integer.parseInt(getString(key));
    }

    // -------------------------------------------------------------------------
    // Return null if not set
    // -------------------------------------------------------------------------

    public Optional<String> getOptionalString(String key) {
        var sys = System.getProperty(key);
        var value = Strings.isNullOrEmpty(sys) ? prop.getProperty(key) : sys;
        if (Strings.isNullOrEmpty(value)) {
            return Optional.empty();
        }
        value = resolveIfSystemEnv(value);
        return Optional.ofNullable(value).map(v -> trim(key, v));
    }

    public Optional<Boolean> getOptionalBoolean(String key) {
        var s = getOptionalString(key);
        return s.map(Boolean::parseBoolean);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private static Properties loadFromSystem(String fileName) {
        var prop = new Properties();
        try (var inputStream = new FileInputStream(fileName)) {
            prop.load(inputStream);
            log.info("Loaded properties from {}", fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return prop;
    }

    private static Properties loadFromClasspath(String fileName) {
        var prop = new Properties();
        try (var is = PropertiesFileLoader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) {
                throw new FileNotFoundException("Property file '" + fileName + "' is not found in classpath");
            }
            prop.load(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return prop;
    }

    /**
     * If the first character of the value of the property is a dollar sign, we deduce that this property points to a
     * system environment variable and look it up.
     *
     * However, if the resolved value is null, we do not use it and fallback to the initial value. This might be the
     * case for example with passwords, which use arbitrary characters and start with a dollar sign.
     */
    private static @Nullable String resolveIfSystemEnv(@Nullable String value) {
        if (value == null) {
            return null;
        }
        if ("$".equals(String.valueOf(value.charAt(0)))) {
            var sysEnvValue = System.getenv(value.substring(1));
            if (sysEnvValue != null) {
                return sysEnvValue;
            }
        }
        return value;
    }

    private static String trim(String key, String value) {
        var trimmed = value.trim();
        if (!trimmed.equals(value)) {
            log.debug("The property '{}' has leading or trailing spaces which were removed!", key);
        }
        return trimmed;
    }

    private static void checkForNullAndEmpty(String key, @Nullable String value) {
        if (value == null) {
            throw new IllegalArgumentException("The property '" + key + "' is not found");
        }
        if (value.isEmpty()) {
            throw new IllegalArgumentException("The property '" + key + "' has no value set");
        }
    }
}
