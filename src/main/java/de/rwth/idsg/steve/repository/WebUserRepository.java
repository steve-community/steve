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

import de.rwth.idsg.steve.web.dto.WebUserQueryForm;
import jooq.steve.db.tables.records.WebUserRecord;
import org.jooq.JSON;
import org.jooq.Record4;
import org.jooq.Result;

public interface WebUserRepository  {

    void createUser(WebUserRecord user);

    void updateUser(WebUserRecord user);

    void updateUserByPk(WebUserRecord user);

    void deleteUser(String username);

    void deleteUser(int webUserPk);

    void changeStatusOfUser(String username, boolean enabled);

    Integer getUserCountWithAuthority(String authority);

    void changePassword(String username, String newPassword);

    void changePassword(Integer userPk, String newPassword);

    boolean userExists(String username);

    WebUserRecord loadUserByUsePk(Integer webUserPk);

    WebUserRecord loadUserByUsername(String username);

    Result<Record4<Integer, String, Boolean, JSON>> getOverview(WebUserQueryForm form);
}
