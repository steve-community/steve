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
package de.rwth.idsg.steve.repository.impl;

import de.rwth.idsg.steve.repository.SettingsRepository;
import de.rwth.idsg.steve.web.dto.SettingsForm;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with assistance from GPT-5.3-Codex
 */
public class SettingsRepositoryImplIT extends AbstractRepositoryITBase {

    @Autowired
    private DSLContext dslContext;
    @Autowired
    private SettingsRepository repository;

    @BeforeEach
    public void setup() {
        resetDatabase(dslContext);
    }

    @Test
    public void getForm() {
        var form = assertNoDatabaseException(repository::getForm);
        Assertions.assertNotNull(form);
    }

    @Test
    public void getOcppSettings() {
        var settings = assertNoDatabaseException(repository::getOcppSettings);
        Assertions.assertNotNull(settings);
    }

    @Test
    public void getMailSettings() {
        var settings = assertNoDatabaseException(repository::getMailSettings);
        Assertions.assertNotNull(settings);
    }

    @Test
    public void getHeartbeatIntervalInSeconds() {
        Integer heartbeat = assertNoDatabaseException(repository::getHeartbeatIntervalInSeconds);
        Assertions.assertTrue(heartbeat >= 0);
    }

    @Test
    public void getHoursToExpire() {
        Integer hours = assertNoDatabaseException(repository::getHoursToExpire);
        Assertions.assertTrue(hours >= 0);
    }

    @Test
    public void updateSettingsForm() {
        SettingsForm form = settingsForm();
        assertNoDatabaseException(() -> repository.update(form));

        var after = repository.getOcppSettings();
        Assertions.assertEquals(form.getOcppSettings().getHeartbeat(), after.getHeartbeat());
        Assertions.assertEquals(form.getOcppSettings().getExpiration(), after.getExpiration());
    }

    @Test
    public void updateOcppSettings() {
        SettingsForm form = settingsForm();
        assertNoDatabaseException(() -> repository.update(form.getOcppSettings()));

        var after = repository.getOcppSettings();
        Assertions.assertEquals(form.getOcppSettings().getHeartbeat(), after.getHeartbeat());
        Assertions.assertEquals(form.getOcppSettings().getExpiration(), after.getExpiration());
    }

    @Test
    public void updateMailSettings() {
        SettingsForm form = settingsForm();
        assertNoDatabaseException(() -> repository.update(form.getMailSettings()));

        var after = repository.getMailSettings();
        Assertions.assertEquals(form.getMailSettings().getMailHost(), after.getMailHost());
        Assertions.assertEquals(form.getMailSettings().getRecipients(), after.getRecipients());
    }

    private static SettingsForm settingsForm() {
        var form = new SettingsForm();
        form.setOcppSettings(SettingsForm.OcppSettings.builder().heartbeat(5).expiration(24).build());
        form.setMailSettings(SettingsForm.MailSettings.builder()
            .enabled(false)
            .mailHost("localhost")
            .username("u")
            .password("p")
            .from("from@example.com")
            .protocol("smtp")
            .port(25)
            .recipients(List.of("a@example.com"))
            .enabledFeatures(List.of())
            .build());
        return form;
    }
}
