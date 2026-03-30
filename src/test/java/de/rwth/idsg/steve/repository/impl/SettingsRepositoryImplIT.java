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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@ActiveProfiles(profiles = "test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
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
        assertNoDatabaseException(repository::getForm);
    }

    @Test
    public void getOcppSettings() {
        assertNoDatabaseException(repository::getOcppSettings);
    }

    @Test
    public void getMailSettings() {
        assertNoDatabaseException(repository::getMailSettings);
    }

    @Test
    public void getHeartbeatIntervalInSeconds() {
        assertNoDatabaseException(repository::getHeartbeatIntervalInSeconds);
    }

    @Test
    public void getHoursToExpire() {
        assertNoDatabaseException(repository::getHoursToExpire);
    }

    @Test
    public void updateSettingsForm() {
        assertNoDatabaseException(() -> repository.update(settingsForm()));
    }

    @Test
    public void updateOcppSettings() {
        assertNoDatabaseException(() -> repository.update(settingsForm().getOcppSettings()));
    }

    @Test
    public void updateMailSettings() {
        assertNoDatabaseException(() -> repository.update(settingsForm().getMailSettings()));
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
