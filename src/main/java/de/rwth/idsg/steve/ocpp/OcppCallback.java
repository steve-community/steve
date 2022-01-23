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
package de.rwth.idsg.steve.ocpp;

import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonError;

/**
 * We need a mechanism to execute additional arbitrary logic, which _can_ be provided by the call site,
 * that acts on the response or the error.
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 20.11.2015
 */
public interface OcppCallback<T> {

    void success(String chargeBoxId, T response);

    /**
     * Relevant to WebSocket/JSON transport: Even though we have an error, this object is still a valid response from
     * charge point and the implementation should treat it as such. {@link CommunicationTask#addNewError(String, String)}
     * should be used when the request could not be delivered and there is a Java exception.
     */
    void success(String chargeBoxId, OcppJsonError error);

    // -------------------------------------------------------------------------
    // Technical errors ((e.g. communication problems)
    // -------------------------------------------------------------------------

    void failed(String chargeBoxId, Exception e);

}
