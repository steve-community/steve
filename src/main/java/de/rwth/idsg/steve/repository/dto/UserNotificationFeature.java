/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2024 SteVe Community Team
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

package de.rwth.idsg.steve.repository.dto;

//import static de.rwth.idsg.steve.utils.StringUtils.joinByComma;
//import static de.rwth.idsg.steve.utils.StringUtils.splitByComma;
//import java.util.List;
//import java.util.stream.Collectors;
import static de.rwth.idsg.steve.utils.StringUtils.joinByComma;
import static de.rwth.idsg.steve.utils.StringUtils.splitByComma;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author fnkbsi
 */
@RequiredArgsConstructor
public enum UserNotificationFeature {

    // Ocpp related
    //
    //OcppStationBooted(" a charging station sends a boot notification (Note: This activates notifications about failed connection attempts for unregistered JSON stations, as well)"),
    OcppStationStatusFailure(" a connector gets faulted"),
    //OcppStationWebSocketConnected(" a JSON charging station connects"),
    //OcppStationWebSocketDisconnected(" a JSON charging station disconnects"),
    OcppTransactionStarted(" a charging station starts a transaction"),
    OcppStationStatusSuspendedEV(" a EV suspended charging"),
    OcppTransactionEnded(" a charging station ends a transaction");


    @Getter
    private final String text;

    public static UserNotificationFeature fromName(String v) {
        for (UserNotificationFeature c: UserNotificationFeature.values()) {
            if (c.name().equalsIgnoreCase(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    public static List<UserNotificationFeature> splitFeatures(String str) {
        return splitByComma(str).stream()
                                .map(UserNotificationFeature::fromName)
                                .collect(Collectors.toList());
    }

    public static String joinFeatures(List<UserNotificationFeature> enablesFeatures) {
        return joinByComma(enablesFeatures);
    }
}