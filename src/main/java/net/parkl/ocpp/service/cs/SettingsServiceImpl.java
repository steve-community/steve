/*
 * Parkl Digital Technologies
 * Copyright (C) 2020-2021
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
package net.parkl.ocpp.service.cs;

import de.rwth.idsg.steve.NotificationFeature;
import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.dto.MailSettings;
import de.rwth.idsg.steve.web.dto.SettingsForm;
import net.parkl.ocpp.entities.Setting;
import net.parkl.ocpp.repositories.SettingRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static de.rwth.idsg.steve.utils.StringUtils.joinByComma;
import static de.rwth.idsg.steve.utils.StringUtils.splitByComma;


@Service
public class SettingsServiceImpl implements SettingsService {

	private static final String APP_ID = new String(
            Base64.getEncoder().encode("ParklOcppCs".getBytes(StandardCharsets.UTF_8)),
            StandardCharsets.UTF_8
    );

	@Autowired
	private SettingRepository settingRepo;


	private Setting getInternal() {
        return settingRepo.findById(APP_ID).orElse(null);
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
    public MailSettings getMailSettings() {
        Setting r = getInternal();
        if (r==null) {
            LoggerFactory.getLogger(SettingsServiceImpl.class).error("No settings in database");;
            return null;
        }
        List<String> eMails = splitByComma(r.getMailRecipients());
        List<NotificationFeature> features = splitFeatures(r.getNotificationFeatures());

        return MailSettings.builder()
                           .enabled(r.isMailEnabled())
                           .host(r.getMailHost())
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
	public SettingsForm getForm() {
		Setting r = getInternal();

        List<String> eMails = splitByComma(r.getMailRecipients());
        List<NotificationFeature> features = splitFeatures(r.getNotificationFeatures());

        return SettingsForm.builder()
                           .heartbeat(toMin(r.getHeartbeatIntervalInSeconds()))
                           .expiration(r.getHoursToExpire())
                           .enabled(r.isMailEnabled())
                           .host(r.getMailHost())
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
	@Transactional
	public void update(SettingsForm form) {
		Setting s=settingRepo.findById(APP_ID).
				orElseThrow(() -> new IllegalStateException("No setting record found for app id: "+APP_ID));

        String eMails = joinByComma(form.getRecipients());
        String features = joinByComma(form.getEnabledFeatures());

        try {
        	s.setHeartbeatIntervalInSeconds(toSec(form.getHeartbeat()));
        	s.setHoursToExpire(form.getExpiration());
        	s.setMailEnabled(form.getEnabled()!=null?form.getEnabled():false);
        	s.setMailHost(form.getHost());
        	s.setMailUsername(form.getUsername());
        	s.setMailPassword(form.getPassword());
        	s.setMailFrom(form.getFrom());
        	s.setMailProtocol(form.getProtocol());

        	s.setMailPort(form.getPort()!=null?form.getPort():0);
        	s.setMailRecipients(eMails);
        	s.setNotificationFeatures(features);
        	settingRepo.save(s);
        } catch (Exception e) {
            throw new SteveException("FAILED to save the settings", e);
        }
	}

	private static int toMin(int seconds) {
        return (int) TimeUnit.SECONDS.toMinutes(seconds);
    }

    private static int toSec(int minutes) {
        return (int) TimeUnit.MINUTES.toSeconds(minutes);
    }



    private List<NotificationFeature> splitFeatures(String str) {
        return splitByComma(str).stream()
                                .map(NotificationFeature::fromName)
                                .collect(Collectors.toList());
    }

	@Override
	public boolean isSettingsExisting() {
		return getInternal()!=null;
	}

	@Override
	@Transactional
	public void saveDefaultSettings() {
		Setting s=new Setting();
		s.setAppId(APP_ID);
		s.setHeartbeatIntervalInSeconds(14400);
		s.setHoursToExpire(1);
		s.setMailPort(25);
		settingRepo.save(s);
	}

}
