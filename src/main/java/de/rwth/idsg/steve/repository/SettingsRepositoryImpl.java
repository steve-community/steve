package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.dto.Settings;
import de.rwth.idsg.steve.web.dto.SettingsForm;
import jooq.steve.db.tables.records.SettingsRecord;
import org.jooq.Configuration;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Base64;
import java.util.concurrent.TimeUnit;

import static jooq.steve.db.tables.Settings.SETTINGS;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 06.11.2015
 */
@Repository
public class SettingsRepositoryImpl implements SettingsRepository {

    private static final String APP_ID = new String(Base64.getEncoder().encode("SteckdosenVerwaltung".getBytes()));

    @Autowired
    @Qualifier("jooqConfig")
    private Configuration config;

    @Override
    public Settings get() {
        SettingsRecord r = getInternal();

        return Settings.builder()
                       .hoursToExpire(r.getHourstoexpire())
                       .heartbeatIntervalInMinutes(toMin(r.getHeartbeatintervalinseconds()))
                       .build();
    }

    @Override
    public int getHeartbeatIntervalInSeconds() {
        return getInternal().getHeartbeatintervalinseconds();
    }

    @Override
    public int getHoursToExpire() {
        return getInternal().getHourstoexpire();
    }

    @Override
    public void update(SettingsForm form) {
        try {
            DSL.using(config)
               .update(SETTINGS)
               .set(SETTINGS.HEARTBEATINTERVALINSECONDS, toSec(form.getHeartbeat()))
               .set(SETTINGS.HOURSTOEXPIRE, form.getExpiration())
               .where(SETTINGS.APPID.eq(APP_ID))
               .execute();
        } catch (DataAccessException e) {
            throw new SteveException("FAILED to save the settings", e);
        }
    }

    private SettingsRecord getInternal() {
        return DSL.using(config)
                  .selectFrom(SETTINGS)
                  .where(SETTINGS.APPID.eq(APP_ID))
                  .fetchOne();
    }

    private static int toMin(int seconds) {
        return (int) TimeUnit.SECONDS.toMinutes(seconds);
    }

    private static int toSec(int minutes) {
        return (int) TimeUnit.MINUTES.toSeconds(minutes);
    }
}
