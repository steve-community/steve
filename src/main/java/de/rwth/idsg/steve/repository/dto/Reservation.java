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

import lombok.Builder;
import lombok.Getter;
import org.joda.time.DateTime;

/**
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 *
 */
@Getter
@Builder
public final class Reservation {
    private final int id, ocppTagPk, chargeBoxPk;
    private final int connectorId;
    private final Integer transactionId;
    private final String ocppIdTag, chargeBoxId, startDatetime, expiryDatetime, status;
    private final DateTime startDatetimeDT;
    private final DateTime expiryDatetimeDT;
}
