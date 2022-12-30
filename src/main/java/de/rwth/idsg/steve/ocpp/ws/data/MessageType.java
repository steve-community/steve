/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2019 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
 * All Rights Reserved.
 *
 * Parkl Digital Technologies
 * Copyright (C) 2020-2021
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
package de.rwth.idsg.steve.ocpp.ws.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 13.03.2015
 */
@RequiredArgsConstructor
@Getter
public enum MessageType {
    CALL(2),
    CALL_RESULT(3),
    CALL_ERROR(4);

    private final int typeNr;

    public static MessageType fromTypeNr(int typeNr) {
        for (MessageType messageType : MessageType.values()) {
            if (messageType.typeNr == typeNr) {
                return messageType;
            }
        }
        throw new IllegalArgumentException(typeNr + " is an unknown message type");
    }
}
