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
package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.repository.dto.WebUser;
import de.rwth.idsg.steve.service.dto.WebUserOverview;
import de.rwth.idsg.steve.web.dto.WebUserQueryForm;

import java.util.List;
import java.util.Optional;

public interface WebUserRepository {

    void createUser(WebUser user);

    void updateUser(WebUser user);

    void updateUserByPk(WebUser user);

    void deleteUser(String username);

    void deleteUser(int webUserPk);

    void changeStatusOfUser(String username, boolean enabled);

    int getUserCountWithAuthority(String authority);

    void changePassword(String username, String newPassword);

    void changePassword(Integer userPk, String newPassword);

    void changeApiPassword(Integer userPk, String newPassword);

    boolean userExists(String username);

    Optional<WebUser> loadUserByUserPk(Integer webUserPk);

    Optional<WebUser> loadUserByUsername(String username);

    List<WebUserOverview> getOverview(WebUserQueryForm form);
}
