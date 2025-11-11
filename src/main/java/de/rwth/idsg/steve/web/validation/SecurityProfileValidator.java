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
package de.rwth.idsg.steve.web.validation;

import de.rwth.idsg.steve.ocpp.OcppSecurityProfile;
import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.web.dto.ChargePointForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
@RequiredArgsConstructor
public class SecurityProfileValidator implements ConstraintValidator<SecurityProfileValid, ChargePointForm> {

    private final ChargePointRepository chargePointRepository;

    @Override
    public boolean isValid(ChargePointForm form, ConstraintValidatorContext context) {
        OcppSecurityProfile securityProfile = form.getSecurityProfile();
        String newAuthPassword = form.getAuthPassword();

        if (!securityProfile.requiresBasicAuth()) {
            return true;
        }

        // from here on: station with profile 1 or 2

        // let's handle the easiest possibility first
        boolean hasNewPassword = StringUtils.hasText(newAuthPassword);
        if (hasNewPassword) {
            return true;
        }

        // from here on: station with profile 1 or 2 zone without Auth pwd in form

        // we do not require it to be set, if the DB already has it.
        var optionalRegistration = chargePointRepository.getRegistration(form.getChargeBoxId());

        // must be new station without pwd
        if (optionalRegistration.isEmpty()) {
            return false;
        }

        // from here on: existing station with profile 1 or 2 without Auth pwd in form

        // all is good, if we have pwd in DB
        String pwd = optionalRegistration.get().hashedAuthPassword();
        return StringUtils.hasText(pwd);
    }
}
