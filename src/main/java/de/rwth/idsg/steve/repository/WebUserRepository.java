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
package de.rwth.idsg.steve.repository;

import jooq.steve.db.tables.records.WebUserRecord;
import de.rwth.idsg.steve.repository.dto.WebUserOverview;
import de.rwth.idsg.steve.web.dto.WebUserQueryForm;
import java.util.List;

public interface WebUserRepository  {

    void createUser(WebUserRecord user);

    void updateUser(WebUserRecord user);

    void deleteUser(String username);

    void deleteUser(int webUserPk);

    void changeStatusOfUser(String username, boolean enabled);

    Integer getUserCountWithAuthority(String authority);

    void changePassword(String username, String newPassword);

    boolean userExists(String username);

    WebUserRecord loadUserByUsePk(Integer webUserPk);
    WebUserRecord loadUserByUsername(String username);

    // methods for the website
    List<WebUserOverview> getOverview(WebUserQueryForm form);

}