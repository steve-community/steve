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

import de.rwth.idsg.steve.NotificationFeature;
import de.rwth.idsg.steve.web.validation.EmailCollection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 17.09.2014
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SettingsForm {

    @NotNull @Valid private OcppSettings ocppSettings;

    @NotNull @Valid private MailSettings mailSettings;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OcppSettings {
        @PositiveOrZero(message = "Heartbeat Interval must be at least {value}") @NotNull(message = "Heartbeat Interval is required") private int heartbeat;

        @PositiveOrZero(message = "Expiration must be at least {value}") @NotNull(message = "Expiration is required") private int expiration;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MailSettings {
        @NotNull @Builder.Default
        private boolean enabled = false;

        @Builder.Default
        private String protocol = "smtp";

        private String mailHost;

        @Positive(message = "Port must be positive") private Integer port;

        @Email(message = "'From' field is not a valid e-mail address") private String from;

        private String username;
        private String password;

        @EmailCollection
        @Builder.Default
        private List<String> recipients = List.of();

        @Builder.Default
        private List<NotificationFeature> enabledFeatures = List.of();
    }
}
