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
package de.rwth.idsg.steve.repository.impl;

import de.rwth.idsg.steve.NotificationFeature;
import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.SettingsRepository;
import de.rwth.idsg.steve.repository.dto.MailSettings;
import de.rwth.idsg.steve.web.dto.SettingsForm;
import jooq.steve.db.tables.records.SettingsRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.springframework.stereotype.Repository;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
            StandardCharsets.UTF_8);

    private static List<String> parseRecipients(SettingsRecord r) {
        return splitByComma(r.getMailRecipients());
    }

    private static List<NotificationFeature> parseEnabledFeatures(SettingsRecord r) {
        return splitByComma(r.getNotificationFeatures()).stream()
                .map(NotificationFeature::fromName)
                .toList();
    }

    private final DSLContext ctx;

    @Override
    public SettingsForm getForm() {
        var r = getInternal();

        var emails = parseRecipients(r);
        var features = parseEnabledFeatures(r);

        return SettingsForm.builder()
                .heartbeat(toMin(r.getHeartbeatIntervalInSeconds()))
                .expiration(r.getHoursToExpire())
                .enabled(r.getMailEnabled())
                .mailHost(r.getMailHost())
                .username(r.getMailUsername())
                .password(r.getMailPassword())
                .from(r.getMailFrom())
                .protocol(r.getMailProtocol())
                .port(r.getMailPort())
                .recipients(emails)
                .enabledFeatures(features)
                .build();
    }

    @Override
    public MailSettings getMailSettings() {
        var r = getInternal();

        var eMails = parseRecipients(r);
        var features = parseEnabledFeatures(r);

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
        var eMails = joinByComma(form.getRecipients());
        var features = joinByComma(form.getEnabledFeatures());

        try {
            ctx.update(SETTINGS)
                    .set(SETTINGS.HEARTBEAT_INTERVAL_IN_SECONDS, toSec(form.getHeartbeat()))
                    .set(SETTINGS.HOURS_TO_EXPIRE, form.getExpiration())
                    .set(SETTINGS.MAIL_ENABLED, form.getEnabled())
                    .set(SETTINGS.MAIL_HOST, form.getMailHost())
                    .set(SETTINGS.MAIL_USERNAME, form.getUsername())
                    .set(SETTINGS.MAIL_PASSWORD, form.getPassword())
                    .set(SETTINGS.MAIL_FROM, form.getFrom())
                    .set(SETTINGS.MAIL_PROTOCOL, form.getProtocol())
                    .set(SETTINGS.MAIL_PORT, form.getPort())
                    .set(SETTINGS.MAIL_RECIPIENTS, eMails)
                    .set(SETTINGS.NOTIFICATION_FEATURES, features)
                    .where(SETTINGS.APP_ID.eq(APP_ID))
                    .execute();

        } catch (DataAccessException e) {
            throw new SteveException.InternalError("FAILED to save the settings", e);
        }
    }

    private SettingsRecord getInternal() {
        return ctx.selectFrom(SETTINGS).where(SETTINGS.APP_ID.eq(APP_ID)).fetchOne();
    }

    private static int toMin(int seconds) {
        return (int) TimeUnit.SECONDS.toMinutes(seconds);
    }

    private static int toSec(int minutes) {
        return (int) TimeUnit.MINUTES.toSeconds(minutes);
    }
}
