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
package de.rwth.idsg.steve;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.rwth.idsg.steve.utils.StringUtils.joinByComma;
import static de.rwth.idsg.steve.utils.StringUtils.splitByComma;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 22.01.2016
 */
@RequiredArgsConstructor
public enum NotificationFeature {

    // Ocpp related
    //
    OcppStationBooted(false, " a charging station sends a boot notification (Note: This activates notifications about failed connection attempts for unregistered JSON stations, as well)"),
    OcppStationStatusFailure(true, " a connector gets faulted"),
    OcppStationWebSocketConnected(false, " a JSON charging station connects"),
    OcppStationWebSocketDisconnected(false, " a JSON charging station disconnects"),
    OcppTransactionStarted(true, " a charging station starts a transaction"),
    OcppStationStatusSuspendedEV(true, " a EV suspended charging"),
    OcppTransactionEnded(true, " a charging station ends a transaction");

    private static final List<NotificationFeature> userNotificationFeatures = Arrays.stream(NotificationFeature.values())
        .filter(NotificationFeature::isForUser)
        .collect(Collectors.toList());

    /**
     * Whether this notification type is intended for end-users as well.
     */
    @Getter
    private final boolean forUser;

    @Getter
    private final String text;

    public static List<NotificationFeature> getUserValues() {
        return userNotificationFeatures;
    }

    public static NotificationFeature fromName(String v) {
        for (NotificationFeature c: NotificationFeature.values()) {
            if (c.name().equalsIgnoreCase(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    public static List<NotificationFeature> splitFeatures(String str) {
        return splitByComma(str).stream()
            .map(NotificationFeature::fromName)
            .collect(Collectors.toList());
    }

    public static String joinFeatures(List<NotificationFeature> enablesFeatures) {
        return joinByComma(enablesFeatures);
    }
}
