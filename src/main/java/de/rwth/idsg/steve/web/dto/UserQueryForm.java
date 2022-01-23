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

import lombok.Getter;
import lombok.Setter;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 26.11.2015
 */
@Getter
@Setter
public class UserQueryForm {

    private Integer userPk;

    // Free text input
    private String ocppIdTag;
    private String name;
    private String email;

    public boolean isSetUserPk() {
        return userPk != null;
    }

    public boolean isSetOcppIdTag() {
        return ocppIdTag != null;
    }

    public boolean isSetName() {
        return name != null;
    }

    public boolean isSetEmail() {
        return email != null;
    }
}
