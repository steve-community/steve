/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2023 SteVe Community Team
/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2024 SteVe Community Team
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

import lombok.Getter;
import lombok.Setter;

 /**
 * @author fnkbsi
 * @since 01.04.2022
 */
@Getter
@Setter
public class WebUserQueryForm {

    private Boolean enabled;

    // Free text input
    private String webUsername;
    private String roles;
    private String apiToken;

    public boolean isSetWebusername() {
        return webUsername != null;
    }

    public boolean isSetRoles() {
        return roles != null;
    }

    public boolean isSetEnabled() {
        return enabled != null;
    }

    public boolean isSetApiKey() {
        return apiToken != null;
    }

}
