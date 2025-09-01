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
package de.rwth.idsg.steve.repository.dto;

import de.rwth.idsg.steve.web.dto.WebUserAuthority;
import lombok.Builder;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

import java.util.Set;

@Getter
@Builder
public class WebUser {
    private final @Nullable Integer webUserPk;
    private final String login;
    private final @Nullable String password;
    private final @Nullable String salt;
    private final @Nullable String firstname;
    private final @Nullable String lastname;
    private final @Nullable String email;
    private final boolean enabled;
    private final Set<WebUserAuthority> authorities;
    private final @Nullable String apiPassword;
}
