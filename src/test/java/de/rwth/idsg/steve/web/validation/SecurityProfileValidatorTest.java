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
package de.rwth.idsg.steve.web.validation;

import de.rwth.idsg.steve.ocpp.OcppSecurityProfile;
import de.rwth.idsg.steve.repository.dto.ChargePointRegistration;
import de.rwth.idsg.steve.service.ChargePointService;
import de.rwth.idsg.steve.web.dto.ChargePointForm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class SecurityProfileValidatorTest {

    @Mock
    private ChargePointService chargePointService;

    @InjectMocks
    private SecurityProfileValidator securityProfileValidator;

    @ParameterizedTest
    @CsvSource(
        useHeadersInDisplayName = true,
        nullValues = {"null"},
        textBlock = """
            securityProfileNumber,  authPassword,   stationExistsInDB,  authPasswordInDB,   isValid
            0,                      null,           false,              null,               true
            0,                      password123,    false,              null,               true
            0,                      null,           true,               null,               true
            0,                      null,           true,               oldPass,            true
            1,                      password123,    false,              null,               true
            1,                      null,           false,              null,               false
            1,                      password123,    true,               oldPass,            true
            1,                      null,           true,               oldPass,            true
            1,                      null,           true,               null,               false
            2,                      password123,    false,              null,               true
            2,                      null,           false,              null,               false
            2,                      password123,    true,               oldPass,            true
            2,                      null,           true,               oldPass,            true
            2,                      null,           true,               null,               false
            3,                      null,           false,              null,               true
            3,                      password123,    false,              null,               true
            3,                      null,           true,               null,               true
            3,                      null,           true,               oldPass,            true
            """)
    public void testInputValidation(int securityProfileNumber,
                                    String authPassword,
                                    boolean stationExistsInDB,
                                    String authPasswordInDB,
                                    boolean isValid) {
        OcppSecurityProfile securityProfile = OcppSecurityProfile.fromValue(securityProfileNumber);

        ChargePointForm form = new ChargePointForm();
        form.setSecurityProfile(securityProfile);
        form.setAuthPassword(authPassword);

        ChargePointRegistration toReturn = stationExistsInDB
            ? new ChargePointRegistration(-1, "chargeBoxId", "Accepted", securityProfile, authPasswordInDB, "cpoName", "serialNumber")
            : null;

        when(chargePointService.getRegistrationDirect(any())).thenReturn(Optional.ofNullable(toReturn));

        boolean result = securityProfileValidator.isValid(form, null);
        Assertions.assertEquals(isValid, result);
    }

}
