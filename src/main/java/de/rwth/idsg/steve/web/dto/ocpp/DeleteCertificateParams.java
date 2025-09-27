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

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Setter
@Getter
public class DeleteCertificateParams extends MultipleChargePointSelect {

    @NotBlank(message = "Certificate hash data is required")
    @Size(max = 128, message = "Hash data must not exceed {max} characters")
    private String certificateHashData;

    @NotBlank(message = "Hash algorithm is required")
    private String hashAlgorithm;

    @NotBlank(message = "Issuer name hash is required")
    @Size(max = 128, message = "Issuer name hash must not exceed {max} characters")
    private String issuerNameHash;

    @NotBlank(message = "Issuer key hash is required")
    @Size(max = 128, message = "Issuer key hash must not exceed {max} characters")
    private String issuerKeyHash;

    @NotBlank(message = "Serial number is required")
    @Size(max = 40, message = "Serial number must not exceed {max} characters")
    private String serialNumber;
}