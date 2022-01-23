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
package de.rwth.idsg.steve;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 22.01.2016
 */
@RequiredArgsConstructor
public enum NotificationFeature {

    // Ocpp related
    //
    OcppStationBooted(" a charging station sends a boot notification (Note: This activates notifications about failed connection attempts for unregistered JSON stations, as well)"),
    OcppStationStatusFailure(" a connector gets faulted"),
    OcppStationWebSocketConnected(" a JSON charging station connects"),
    OcppStationWebSocketDisconnected(" a JSON charging station disconnects"),
    OcppTransactionStarted(" a charging station starts a transaction"),
    OcppTransactionEnded(" a charging station ends a transaction");


    @Getter
    private final String text;

    public static NotificationFeature fromName(String v) {
        for (NotificationFeature c: NotificationFeature.values()) {
            if (c.name().equalsIgnoreCase(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
