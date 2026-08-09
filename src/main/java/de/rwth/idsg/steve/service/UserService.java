/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
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

import com.google.common.base.Strings;
import de.rwth.idsg.steve.NotificationFeature;
import de.rwth.idsg.steve.repository.UserRepository;
import de.rwth.idsg.steve.repository.dto.User;
import de.rwth.idsg.steve.web.dto.UserForm;
import de.rwth.idsg.steve.web.dto.UserQueryForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 29.10.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User.Overview getUserForMail(String ocppIdTag, NotificationFeature feature) {
        UserQueryForm form = new UserQueryForm();
        form.setOcppIdTag(ocppIdTag);

        List<User.Overview> overview = this.getOverview(form);
        if (overview.isEmpty()) {
            return null;
        } else if (overview.size() > 1) {
            // should not happen
            log.warn("Multiple users found for OcppTag {}", ocppIdTag);
            return null;
        }

        var user = overview.get(0);
        if (!hasOcppTag(user, ocppIdTag)) {
            return null;
        }

        String eMailAddress = user.getEmail();
        if (Strings.isNullOrEmpty(eMailAddress)) {
            return null;
        }

        if (!user.getNotificationFeatures().contains(feature)) {
            return null;
        }

        return user;
    }

    public List<User.Overview> getOverview(UserQueryForm form) {
        return userRepository.getOverview(form);
    }

    public User.Details getDetails(int userPk) {
        return userRepository.getDetails(userPk);
    }

    public void add(UserForm form) {
        userRepository.add(form);
    }

    public void update(UserForm form) {
        userRepository.update(form);
    }

    public void delete(int userPk) {
        var details = getDetails(userPk);
        userRepository.delete(userPk);
        log.info("Deleted user with userPk={} and email={}", userPk, details.getUserRecord().getEMail());
    }

    /**
     * We check this here again, because userRepository.getOverview(..) also returns partial match OcppTags.
     */
    private static boolean hasOcppTag(User.Overview user, String ocppIdTag) {
        for (User.OcppTagEntry ocppTagEntry : user.getOcppTagEntries()) {
            if (ocppIdTag.equalsIgnoreCase(ocppTagEntry.getIdTag())) {
                return true;
            }
        }
        return false;
    }
}
