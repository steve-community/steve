/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Setter
@Getter
@Schema(description = "Parameters for signed firmware update with cryptographic verification")
public class SignedUpdateFirmwareParams extends UpdateFirmwareParams {

    @NotBlank(message = "Firmware signature is required")
    @Size(max = 800, message = "Firmware signature must not exceed {max} characters")
    @Schema(description = "Cryptographic signature of the firmware file")
    private String signature;

    @NotBlank(message = "Signing certificate is required")
    @Size(max = 5_500, message = "Signing certificate must not exceed {max} characters")
    @Schema(description = "PEM-encoded certificate used to sign the firmware")
    private String signingCertificate;

    @Future(message = "Install Date/Time must be in future")
    @Schema(description = "When charge point should install the downloaded firmware")
    private DateTime installDateTime;
}
