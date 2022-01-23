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
package de.rwth.idsg.steve.web.dto;

import de.rwth.idsg.steve.NotificationFeature;
import de.rwth.idsg.steve.web.validation.EmailCollection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 17.09.2014
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettingsForm {

    // -------------------------------------------------------------------------
    // OCPP
    // -------------------------------------------------------------------------

    @Min(value = 1, message = "Heartbeat Interval must be at least {value}")
    @NotNull(message = "Heartbeat Interval is required")
    private Integer heartbeat;

    @Min(value = 0, message = "Expiration must be at least {value}")
    @NotNull(message = "Expiration is required")
    private Integer expiration;

    // -------------------------------------------------------------------------
    // Mail notification
    // -------------------------------------------------------------------------

    @NotNull
    private Boolean enabled;

    @Email(message = "'From' field is not a valid e-mail address")
    private String from;

    private String host, username, password, protocol;

    @Min(value = 1, message = "Port must be positive")
    private Integer port;

    @EmailCollection
    private List<String> recipients;

    private List<NotificationFeature> enabledFeatures;
}
