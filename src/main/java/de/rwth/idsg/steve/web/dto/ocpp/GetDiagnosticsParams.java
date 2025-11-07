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
package de.rwth.idsg.steve.web.dto.ocpp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 30.12.2014
 */
@Getter
@Setter
public class GetDiagnosticsParams extends MultipleChargePointSelect {

    @Schema(description = "The URL where charge point should upload the log file",
            example = "ftp://user:pass@example.com/logs/")
    @NotBlank(message = "Location is required")
    @Pattern(regexp = "\\S+", message = "Location cannot contain any whitespace")
    private String location;

    @Min(value = 1, message = "Retries must be at least {value}")
    @Schema(description = "Number of times charge point should retry upload if it fails", minimum = "1")
    private Integer retries;

    @Min(value = 1, message = "Retry Interval must be at least {value}")
    @Schema(description = "Interval in seconds between retry attempts", minimum = "1")
    private Integer retryInterval;

    @Past(message = "Start Date/Time must be in the past")
    @Schema(description = "Oldest timestamp to include in log file")
    private DateTime start;

    @Past(message = "Stop Date/Time must be in the past")
    @Schema(description = "Latest timestamp to include in log file")
    private DateTime stop;

    @AssertTrue(message = "Stop Date/Time must be after Start Date/Time")
    public boolean isValid() {
        return !(start != null && stop != null) || stop.isAfter(start);
    }
}
