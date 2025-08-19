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
package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.UserRepository;
import de.rwth.idsg.steve.repository.dto.User;
import de.rwth.idsg.steve.web.api.exception.NotFoundException;
import de.rwth.idsg.steve.web.dto.UserForm;
import de.rwth.idsg.steve.web.dto.UserQueryForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UserRepository userRepository;

    public List<User.Overview> getUsers(UserQueryForm form) {
        return userRepository.getOverview(form);
    }

    public User.Details getDetails(int userPk) {
        return userRepository.getDetails(userPk).orElseThrow(
            () -> new NotFoundException(String.format("User with id %d not found", userPk))
        );
    }

    public User.Details add(UserForm form) {
        var id = userRepository.add(form);
        return userRepository.getDetails(id).orElseThrow(
            () -> new SteveException("User not found after creation, this should never happen")
        );
    }

    public User.Details update(UserForm form) {
        userRepository.update(form);
        return userRepository.getDetails(form.getUserPk()).orElseThrow(
            () -> new SteveException("User not found after update, this should never happen")
        );
    }

    public User.Details delete(int userPk) {
        var user = userRepository.getDetails(userPk).orElseThrow(
            () -> new NotFoundException(String.format("User with id %d not found", userPk))
        );
        userRepository.delete(userPk);
        return user;
    }

    public List<User.Overview> getOverview(UserQueryForm params) {
        return userRepository.getOverview(params);
    }
}
