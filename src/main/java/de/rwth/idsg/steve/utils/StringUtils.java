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

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import de.rwth.idsg.steve.config.WebSocketConfiguration;
import de.rwth.idsg.steve.ocpp.CommunicationTask;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 12.01.2015
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StringUtils {

    private static final Splitter SPLITTER = Splitter.on(",").trimResults().omitEmptyStrings();
    private static final Joiner JOINER = Joiner.on(",").skipNulls();

    /**
     * We don't want to hard-code operation names, but derive them from the actual request object.
     *
     * Example for "ChangeAvailabilityTask":
     * - Remove "Task" at the end -> "ChangeAvailability"
     * - Insert space -> "Change Availability"
     */
    public static String getOperationName(CommunicationTask task) {
        String s = task.getClass().getSimpleName();

        if (s.endsWith("Task")) {
            s = s.substring(0, s.length() - 4);
        }

        // http://stackoverflow.com/a/4886141
        s = s.replaceAll("(\\p{Ll})(\\p{Lu})", "$1 $2");

        return s;
    }

    @Nullable
    public static String joinByComma(Collection<?> col) {
        if (CollectionUtils.isEmpty(col)) {
            return null;
        } else {
            // Use set to trim duplicates and keep collection order
            return JOINER.join(new LinkedHashSet<>(col));
        }
    }

    public static List<String> splitByComma(String str) {
        if (Strings.isNullOrEmpty(str)) {
            return Collections.emptyList();
        } else {
            return SPLITTER.splitToList(str);
        }
    }

    public static String getLastBitFromUrl(final String input) {
        if (Strings.isNullOrEmpty(input)) {
            return "";
        }

        final String substring = WebSocketConfiguration.PATH_INFIX;

        int index = input.indexOf(substring);
        if (index == -1) {
            return "";
        } else {
            return input.substring(index + substring.length());
        }
    }
}
