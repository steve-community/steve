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

import de.rwth.idsg.steve.web.dto.ChargePointBatchInsertForm;
import de.rwth.idsg.steve.web.dto.OcppTagBatchInsertForm;
import de.rwth.idsg.steve.web.dto.SettingsForm;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContainerElementValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    public static void closeValidatorFactory() {
        validatorFactory.close();
    }

    @Test
    public void validatesChargeBoxIdElements() {
        var form = new ChargePointBatchInsertForm();
        form.setIdList(List.of("valid", "invalid/charge-box"));

        assertEquals(Set.of("idList[1].<list element>"), violationPaths(form));
    }

    @Test
    public void validatesIdTagElementsAndListItself() {
        var form = new OcppTagBatchInsertForm();
        form.setIdList(List.of("valid", "invalid tag"));

        assertEquals(Set.of("idList[1].<list element>"), violationPaths(form));

        form.setIdList(Arrays.asList("valid", null, "", " "));
        assertEquals(Set.of(
            "idList[1].<list element>",
            "idList[2].<list element>",
            "idList[3].<list element>"
        ), violationPaths(form));

        form.setIdList(Collections.emptyList());
        assertEquals(Set.of("idList"), violationPaths(form));
    }

    @Test
    public void validatesEmailElementsAndAllowsNullList() {
        var settings = SettingsForm.MailSettings.builder()
            .enabled(true)
            .recipients(List.of("valid@example.org", "invalid"))
            .build();

        assertEquals(Set.of("recipients[1].<list element>"), violationPaths(settings));

        settings.setRecipients(null);
        assertTrue(validator.validate(settings).isEmpty());
    }

    private static Set<String> violationPaths(Object value) {
        return validator.validate(value).stream()
            .map(violation -> violation.getPropertyPath().toString())
            .collect(Collectors.toSet());
    }
}
