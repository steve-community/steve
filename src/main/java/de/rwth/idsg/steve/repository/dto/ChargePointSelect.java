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
package de.rwth.idsg.steve.repository.dto;

import de.rwth.idsg.steve.ocpp.OcppTransport;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 29.12.2014
 */
@RequiredArgsConstructor
@Getter
public final class ChargePointSelect {
    private final OcppTransport ocppTransport;
    private final String chargeBoxId;
    private final String endpointAddress;

    public ChargePointSelect(OcppTransport ocppTransport, String chargeBoxId) {
        // Provide a non-null value (or placeholder if you will) to frontend for JSON charge points.
        // This is clearly a hack. Not my proudest moment.
        this(ocppTransport, chargeBoxId, "-");
    }

    public boolean isEndpointAddressSet() {
        return !("-".equals(endpointAddress));
    }

    public boolean isSoap() {
        return OcppTransport.SOAP == ocppTransport;
    }
}
