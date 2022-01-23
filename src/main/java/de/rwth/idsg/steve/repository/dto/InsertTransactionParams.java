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
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 17.11.2015
 */
@Getter
@Builder
public class InsertTransactionParams {
    private final String chargeBoxId;
    private final int connectorId;
    private final String idTag;
    private final DateTime startTimestamp;
    private final String startMeterValue;

    private final TransactionStatusUpdate statusUpdate = TransactionStatusUpdate.AfterStart;

    // Only in OCPP1.5
    private final Integer reservationId;

    // this came after splitting transaction table into two tables (start and stop)
    private final DateTime eventTimestamp;

    public boolean isSetReservationId() {
        return reservationId != null;
    }

}
