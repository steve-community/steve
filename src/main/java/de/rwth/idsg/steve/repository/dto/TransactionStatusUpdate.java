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

import lombok.Getter;
import ocpp.cs._2015._10.ChargePointErrorCode;
import ocpp.cs._2015._10.ChargePointStatus;

/**
 * Exists only to ensure type safety
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 17.03.2016
 */
@Getter
public enum TransactionStatusUpdate {

    AfterStart(ChargePointStatus.CHARGING),
    AfterStop(ChargePointStatus.AVAILABLE);

    private final String status;
    private final String errorCode = ChargePointErrorCode.NO_ERROR.value();

    TransactionStatusUpdate(ChargePointStatus status) {
        this.status = status.value();
    }
}
