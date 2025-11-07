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

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Setter
@Getter
@Schema(description = "Parameters for sending a signed certificate to charge points")
public class CertificateSignedParams extends MultipleChargePointSelect {

    @Schema(description = "Certificate ID from database")
    private int certificateId;

    @NotBlank(message = "Certificate chain is required")
    @Size(max = 10000, message = "Certificate chain must not exceed {max} characters")
    @Schema(description = "PEM-encoded certificate chain (signed certificate + CA certificate)",
            requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 10000)
    private String certificateChain;
}
