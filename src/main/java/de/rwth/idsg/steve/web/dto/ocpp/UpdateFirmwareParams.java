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
package de.rwth.idsg.steve.web.dto.ocpp;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 01.01.2015
 */
@Setter
@Getter
public class UpdateFirmwareParams extends MultipleChargePointSelect {

    @NotBlank(message = "Location is required")
    @Pattern(regexp = "\\S+", message = "Location cannot contain any whitespace")
    private String location;

    @Min(value = 1, message = "Retries must be at least {value}")
    private Integer retries;

    @Min(value = 1, message = "Retry Interval must be at least {value}")
    private Integer retryInterval;

    @Future(message = "Retrieve Date/Time must be in future")
    @NotNull(message = "Retrieve Date/Time is required")
    private LocalDateTime retrieve;
}
