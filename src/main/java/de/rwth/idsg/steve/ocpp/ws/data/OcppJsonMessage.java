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
package de.rwth.idsg.steve.ocpp.ws.data;

import lombok.Getter;
import lombok.Setter;

/**
 * Class hierarchy:
 *
 *      OcppJsonMessage
 *      +
 *      +--> OcppJsonCall
 *      +
 *      +--> OcppJsonResponse
 *           +
 *           +--> OcppJsonResult
 *           +
 *           +--> OcppJsonError
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 12.03.2015
 */
@Getter
@Setter
public abstract class OcppJsonMessage {
    private final MessageType messageType;
    private String messageId;

    public OcppJsonMessage(MessageType messageType) {
        this.messageType = messageType;
    }
}
