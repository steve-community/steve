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
import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.cxf.common.util.CollectionUtils;
import org.joda.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 25.11.2015
 */
@Getter
@Setter
@ToString
public class UserForm {

    // Internal database id
    private Integer userPk;

    private List<String> idTagList = Collections.emptyList();

    private String firstName;
    private String lastName;
    private LocalDate birthDay;
    private String phone;
    private String note;
    private UserSex sex = UserSex.OTHER;

    @Email(message = "Not a valid e-mail address")
    private String eMail;

    private List<NotificationFeature> notificationFeatures;

    private Address address;

    @AssertTrue(message = "Some of the selected notification features cannot be enabled for a user")
    public boolean isNotificationFeaturesForUser() {
        if (CollectionUtils.isEmpty(notificationFeatures)) {
            return true;
        }

        for (var selectedFeature : notificationFeatures) {
            if (!selectedFeature.isForUser()) {
                return false;
            }
        }

        return true;
    }

    public void setSex(UserSex sex) {
        this.sex = (sex == null) ? UserSex.OTHER : sex;
    }
}
