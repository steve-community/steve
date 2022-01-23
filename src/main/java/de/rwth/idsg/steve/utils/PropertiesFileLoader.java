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
package de.rwth.idsg.steve.utils;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Encapsulates java.util.Properties and adds type specific convenience methods
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 01.10.2015
 */
@Slf4j
public class PropertiesFileLoader {

    private Properties prop;

    /**
     * The name parameter acts as
     * 1) the file name to load from classpath, and
     * 2) the system property which can be set to load from file system.
     */
    public PropertiesFileLoader(String name) {
        String externalFileName = System.getProperty(name);

        if (externalFileName == null) {
            log.info("Hint: The Java system property '{}' can be set to point to an external properties file, " +
                    "which will be prioritized over the bundled one", name);
            loadFromClasspath(name);

        } else {
            loadFromSystem(externalFileName);
        }
    }

    // -------------------------------------------------------------------------
    // Strict
    // -------------------------------------------------------------------------

    public String getString(String key) {
        String s = prop.getProperty(key);
        // initial property value might be null/empty
        checkForNullAndEmpty(key,s);

        s = resolveIfSystemEnv(s);
        // check again, system env value might be null/empty
        checkForNullAndEmpty(key,s);

        return trim(key, s);
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

    public String getOptionalString(String key) {
        String s = prop.getProperty(key);
        if (Strings.isNullOrEmpty(s)) {
            return null;
        }
        s = resolveIfSystemEnv(s);
        return trim(key, s);
    }

    public List<String> getStringList(String key) {
        String s = prop.getProperty(key);
        if (Strings.isNullOrEmpty(s)) {
            return Collections.emptyList();
        }
        s = resolveIfSystemEnv(s);
        return Splitter.on(",")
                       .trimResults()
                       .omitEmptyStrings()
                       .splitToList(s);
    }

    public boolean getOptionalBoolean(String key) {
        String s = getOptionalString(key);
        if (s == null) {
            // In this special case, to make findbugs happy, we don't return null.
            // Reason: http://findbugs.sourceforge.net/bugDescriptions.html#NP_BOOLEAN_RETURN_NULL
            return false;
        } else {
            return Boolean.parseBoolean(s);
        }
    }

    public Integer getOptionalInt(String key) {
        String s = getOptionalString(key);
        if (s == null) {
            return null;
        } else {
            return Integer.parseInt(s);
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private void loadFromSystem(String fileName) {
        try (FileInputStream inputStream = new FileInputStream(fileName)) {
            prop = new Properties();
            prop.load(inputStream);
            log.info("Loaded properties from {}", fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadFromClasspath(String fileName) {
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) {
                throw new FileNotFoundException("Property file '" + fileName + "' is not found in classpath");
            }
            prop = new Properties();
            prop.load(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * If the first character of the value of the property is a dollar sign, we deduce that this property points to a
     * system environment variable and look it up.
     *
     * However, if the resolved value is null, we do not use it and fallback to the initial value. This might be the
     * case for example with passwords, which use arbitrary characters and start with a dollar sign.
     */
    private static String resolveIfSystemEnv(String value) {
        if (value == null) {
            return null;
        }
        if ("$".equals(String.valueOf(value.charAt(0)))) {
            String sysEnvValue = System.getenv(value.substring(1));
            if (sysEnvValue != null) {
                return sysEnvValue;
            }
        }
        return value;
    }

    private static String trim(String key, String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (!trimmed.equals(value)) {
            log.warn("The property '{}' has leading or trailing spaces which were removed!", key);
        }
        return trimmed;
    }

    private static void checkForNullAndEmpty(String key, String value) {
        if (value == null) {
            throw new IllegalArgumentException("The property '" + key + "' is not found");
        }
        if (value.isEmpty()) {
            throw new IllegalArgumentException("The property '" + key + "' has no value set");
        }
    }
}
