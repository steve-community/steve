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

import de.rwth.idsg.steve.NotificationFeature;
import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.SettingsRepository;
import de.rwth.idsg.steve.web.dto.SettingsForm;
import de.rwth.idsg.steve.web.dto.SettingsForm.MailSettings;
import de.rwth.idsg.steve.web.dto.SettingsForm.OcppSettings;
import jooq.steve.db.tables.records.SettingsRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static de.rwth.idsg.steve.utils.StringUtils.joinByComma;
import static de.rwth.idsg.steve.utils.StringUtils.splitByComma;
import static jooq.steve.db.tables.Settings.SETTINGS;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 06.11.2015
 */
@Repository
@RequiredArgsConstructor
public class SettingsRepositoryImpl implements SettingsRepository {

    // Totally unnecessary to specify charset here. We just do it to make findbugs plugin happy.
    //
    private static final String APP_ID = new String(
        Base64.getEncoder().encode("SteckdosenVerwaltung".getBytes(StandardCharsets.UTF_8)),
        StandardCharsets.UTF_8
    );

    private final DSLContext ctx;

    @Override
    public SettingsForm getForm() {
        SettingsRecord r = getInternal();

        var form = new SettingsForm();
        form.setOcppSettings(mapToOcppSettings(r));
        form.setMailSettings(mapToMailSettings(r));
        return form;
    }

    @Override
    public OcppSettings getOcppSettings() {
        SettingsRecord r = getInternal();
        return mapToOcppSettings(r);
    }

    @Override
    public MailSettings getMailSettings() {
        SettingsRecord r = getInternal();
        return mapToMailSettings(r);
    }

    @Override
    public int getHeartbeatIntervalInSeconds() {
        return getInternal().getHeartbeatIntervalInSeconds();
    }

    @Override
    public int getHoursToExpire() {
        return getInternal().getHoursToExpire();
    }

    @Override
    public void update(SettingsForm form) {
        ctx.transaction(configuration -> {
            DSLContext ctx = DSL.using(configuration);
            try {
                updateInternal(ctx, form.getOcppSettings());
                updateInternal(ctx, form.getMailSettings());
            } catch (DataAccessException e) {
                throw new SteveException("FAILED to save the settings", e);
            }
        });
    }

    @Override
    public void update(OcppSettings ocppForm) {
        try {
            updateInternal(ctx, ocppForm);
        } catch (DataAccessException e) {
            throw new SteveException("FAILED to save Ocpp settings", e);
        }
    }

    @Override
    public void update(MailSettings mailForm) {
        try {
            updateInternal(ctx, mailForm);
        } catch (DataAccessException e) {
            throw new SteveException("FAILED to save mail settings", e);
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private SettingsRecord getInternal() {
        return ctx.selectFrom(SETTINGS)
            .where(SETTINGS.APP_ID.eq(APP_ID))
            .fetchOne();
    }

    private static void updateInternal(DSLContext ctx, OcppSettings ocppForm) {
        ctx.update(SETTINGS)
            .set(SETTINGS.HEARTBEAT_INTERVAL_IN_SECONDS, toSec(ocppForm.getHeartbeat()))
            .set(SETTINGS.HOURS_TO_EXPIRE, ocppForm.getExpiration())
            .where(SETTINGS.APP_ID.eq(APP_ID))
            .execute();
    }

    private static void updateInternal(DSLContext ctx, MailSettings mailForm) {
        String eMails = joinByComma(mailForm.getRecipients());
        String features = joinByComma(mailForm.getEnabledFeatures());

        ctx.update(SETTINGS)
            .set(SETTINGS.MAIL_ENABLED, mailForm.getEnabled())
            .set(SETTINGS.MAIL_HOST, mailForm.getMailHost())
            .set(SETTINGS.MAIL_USERNAME, mailForm.getUsername())
            .set(SETTINGS.MAIL_PASSWORD, mailForm.getPassword())
            .set(SETTINGS.MAIL_FROM, mailForm.getFrom())
            .set(SETTINGS.MAIL_PROTOCOL, mailForm.getProtocol())
            .set(SETTINGS.MAIL_PORT, mailForm.getPort())
            .set(SETTINGS.MAIL_RECIPIENTS, eMails)
            .set(SETTINGS.NOTIFICATION_FEATURES, features)
            .where(SETTINGS.APP_ID.eq(APP_ID))
            .execute();
    }

    private static OcppSettings mapToOcppSettings(SettingsRecord r) {
        return OcppSettings.builder()
            .heartbeat(toMin(r.getHeartbeatIntervalInSeconds()))
            .expiration(r.getHoursToExpire())
            .build();
    }

    private static MailSettings mapToMailSettings(SettingsRecord r) {
        List<String> eMails = splitByComma(r.getMailRecipients());

        List<NotificationFeature> features = splitByComma(r.getNotificationFeatures())
            .stream()
            .map(NotificationFeature::fromName)
            .collect(Collectors.toList());

        return MailSettings.builder()
            .enabled(r.getMailEnabled())
            .mailHost(r.getMailHost())
            .username(r.getMailUsername())
            .password(r.getMailPassword())
            .from(r.getMailFrom())
            .protocol(r.getMailProtocol())
            .port(r.getMailPort())
            .recipients(eMails)
            .enabledFeatures(features)
            .build();
    }

    private static int toMin(int seconds) {
        return (int) TimeUnit.SECONDS.toMinutes(seconds);
    }

    private static int toSec(int minutes) {
        return (int) TimeUnit.MINUTES.toSeconds(minutes);
    }
}
