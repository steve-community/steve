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

import de.rwth.idsg.steve.ocpp.OcppSecurityProfile;
import de.rwth.idsg.steve.web.validation.ChargeBoxId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.URL;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 18.12.2014
 */
@Getter
@Setter
@ToString
public class ChargePointForm {

    // Internal database id
    private Integer chargeBoxPk;

    @NotBlank(message = "ChargeBox ID is required")
    @ChargeBoxId
    private String chargeBoxId;

    @NotBlank(message = "Registration status is required")
    private String registrationStatus;

    @NotNull
    private Boolean insertConnectorStatusAfterTransactionMsg;

    @Valid
    private Address address;

    private String description;
    private String note;

    @URL(message = "Admin address must be a valid URL")
    private String adminAddress;

    @NotNull
    private OcppSecurityProfile securityProfile = OcppSecurityProfile.Profile_0;

    /**
     * Reads (from DB to browser): This field is NEVER set. Do not expose to browser.
     *
     * Writes (from browser to backend): The field comes as plain password in form. Service layer REPLACES it with
     * encoded password value, and sends it to repository layer.
     */
    @Schema(accessMode = Schema.AccessMode.WRITE_ONLY)
    private String authPassword;

    private boolean hasAuthPassword;
}
