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

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Setter
@Getter
@Schema(description = "Parameters for requesting diagnostic or security logs from charge points")
public class GetLogParams extends MultipleChargePointSelect {

    @NotNull(message = "Log type is required")
    @Schema(description = "Type of log to retrieve", requiredMode = Schema.RequiredMode.REQUIRED)
    private LogEnumType logType;

    @NotNull(message = "Request ID is required")
    @Min(value = 1, message = "Request ID must be at least {value}")
    @Schema(description = "Unique request identifier", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1")
    private Integer requestId;

    @NotBlank(message = "Location is required")
    @Pattern(regexp = "\\S+", message = "Location cannot contain any whitespace")
    @Schema(description = "FTP/SFTP URL where charge point should upload the log file", requiredMode = Schema.RequiredMode.REQUIRED, example = "ftp://user:pass@example.com/logs/")
    private String location;

    @Min(value = 1, message = "Retries must be at least {value}")
    @Schema(description = "Number of times charge point should retry upload if it fails", minimum = "1")
    private Integer retries;

    @Min(value = 1, message = "Retry Interval must be at least {value}")
    @Schema(description = "Interval in seconds between retry attempts", minimum = "1")
    private Integer retryInterval;

    @Schema(description = "Oldest timestamp to include in log file")
    private DateTime oldestTimestamp;

    @Schema(description = "Latest timestamp to include in log file")
    private DateTime latestTimestamp;

    public enum LogEnumType {
        DiagnosticsLog,
        SecurityLog
    }
}