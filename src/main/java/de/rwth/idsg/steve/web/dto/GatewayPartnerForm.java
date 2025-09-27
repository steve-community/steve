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

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GatewayPartnerForm {

    private Integer id;

    @NotBlank(message = "Partner name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @NotNull(message = "Protocol is required")
    private String protocol;

    @Size(max = 3, message = "Party ID cannot exceed 3 characters")
    private String partyId;

    @Size(max = 2, message = "Country code must be 2 characters")
    private String countryCode;

    @NotBlank(message = "Endpoint URL is required")
    @Size(max = 255, message = "Endpoint URL cannot exceed 255 characters")
    private String endpointUrl;

    @NotBlank(message = "Token is required")
    @Size(max = 255, message = "Token cannot exceed 255 characters")
    private String token;

    private Boolean enabled = true;

    @NotNull(message = "Role is required")
    private String role;

    public String getTokenMasked() {
        if (token == null || token.length() < 8) {
            return "***";
        }
        return token.substring(0, 3) + "..." + token.substring(token.length() - 3);
    }
}