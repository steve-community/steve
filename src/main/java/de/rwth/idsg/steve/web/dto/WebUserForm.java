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

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;


/**
 * @author fnkbsi
 * @since 01.04.2022
 */
@Getter
public class WebUserForm extends WebUserBaseForm {

    @NotNull(message = "Password is required")
    @Size(min = 8, message = "Password requires 8 or more characters")
    private String password = "";

    @NotNull(message = "Password repetition is required")
    @Size(min = 8, message = "The repeated password also requires 8 or more characters")
    private String passwordComparison;

    @AssertTrue(message = "The repeated password did not match!")
    private Boolean pwError;

    private String apiToken = "";

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPasswordComparison(String passwordComparison) {
        this.passwordComparison = passwordComparison;
        if (passwordComparison != null) {
            this.pwError = passwordComparison.equals(this.password);
        }
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }
}
