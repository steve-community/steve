/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2025 SteVe Community Team
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
package de.rwth.idsg.steve.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReservationForm {

    @NotBlank(message = "ID Tag is required")
    private String idTag;

    @NotBlank(message = "ChargeBox ID is required")
    private String chargeBoxId;

    @NotNull(message = "Connector ID is required")
    @Min(value = 0, message = "Connector ID must be >= 0")
    private Integer connectorId;

    @NotNull(message = "Start timestamp is required")
    private LocalDateTime startTimestamp;

    @NotNull(message = "Expiry timestamp is required")
    @Future(message = "Expiry timestamp must be in the future")
    private LocalDateTime expiryTimestamp;
}
