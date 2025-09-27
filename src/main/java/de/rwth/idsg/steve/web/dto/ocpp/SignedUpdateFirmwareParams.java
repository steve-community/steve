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

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Setter
@Getter
@Schema(description = "Parameters for signed firmware update with cryptographic verification")
public class SignedUpdateFirmwareParams extends MultipleChargePointSelect {

    @NotNull(message = "Request ID is required")
    @Min(value = 1, message = "Request ID must be at least {value}")
    @Schema(description = "Unique request identifier", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1")
    private Integer requestId;

    @NotBlank(message = "Firmware location is required")
    @Pattern(regexp = "\\S+", message = "Location cannot contain any whitespace")
    @Schema(description = "URL where charge point can download the firmware", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://firmware.example.com/v2.3.bin")
    private String firmwareLocation;

    @NotBlank(message = "Firmware signature is required")
    @Size(max = 5000, message = "Firmware signature must not exceed {max} characters")
    @Schema(description = "Cryptographic signature of the firmware file", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 5000)
    private String firmwareSignature;

    @Size(max = 5500, message = "Signing certificate must not exceed {max} characters")
    @Schema(description = "PEM-encoded certificate used to sign the firmware", maxLength = 5500)
    private String signingCertificate;

    @Min(value = 1, message = "Retries must be at least {value}")
    @Schema(description = "Number of download retry attempts", minimum = "1")
    private Integer retries;

    @Min(value = 1, message = "Retry Interval must be at least {value}")
    @Schema(description = "Interval in seconds between retry attempts", minimum = "1")
    private Integer retryInterval;

    @Future(message = "Retrieve Date/Time must be in future")
    @NotNull(message = "Retrieve Date/Time is required")
    @Schema(description = "When charge point should start downloading firmware", requiredMode = Schema.RequiredMode.REQUIRED)
    private DateTime retrieveDateTime;

    @Future(message = "Install Date/Time must be in future")
    @Schema(description = "When charge point should install the downloaded firmware")
    private DateTime installDateTime;
}